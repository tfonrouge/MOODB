package tech.fonrouge.daemon.build;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class FileMaker {
    private final Path pathTable;
    private final Path pathModel;
    private final String className;
    private final Boolean isAbstract;
    private final String extendClass;
    private final String tableName;
    private final String genre;
    private final String genres;
    private final String database;
    private String masterSourceClass = null;
    private String masterSourceField = null;

    private int numCalcFields = 0;
    private int numValidateFields = 0;
    private List<FieldModel> fieldModels;
    private List<IndexModel> indexModels;
    private List<FieldFilterModel> fieldFilterModels;
    private List<String> calcFieldList = new ArrayList<>();
    private List<String> onValidateList = new ArrayList<>();

    FileMaker(final Path pathExtLess, final Document document, String className) {

        NamedNodeMap namedNodeMap = document.getElementsByTagNameNS("*", "Table").item(0).getAttributes();
        isAbstract = namedNodeMap.getNamedItem("abstract") != null && namedNodeMap.getNamedItem("abstract").getNodeValue().equals("true");
        extendClass = namedNodeMap.getNamedItem("extends") == null ? "MTable" : namedNodeMap.getNamedItem("extends").getNodeValue();
        tableName = namedNodeMap.getNamedItem("tableName") == null ? null : namedNodeMap.getNamedItem("tableName").getNodeValue();
        genre = namedNodeMap.getNamedItem("genre") == null ? null : namedNodeMap.getNamedItem("genre").getNodeValue();
        genres = namedNodeMap.getNamedItem("genres") == null ? null : namedNodeMap.getNamedItem("genres").getNodeValue();
        database = namedNodeMap.getNamedItem("database") == null ? null : namedNodeMap.getNamedItem("database").getNodeValue();

        this.pathTable = Paths.get(pathExtLess.toString() + ".java");
        this.pathModel = Paths.get(pathExtLess.toString() + "Data.java");
        this.className = className;
        fieldModels = new ArrayList<>();
        indexModels = new ArrayList<>();
        fieldFilterModels = new ArrayList<>();

        NodeList nodeList = document.getElementsByTagNameNS("*", "Fields").item(0).getChildNodes();

        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                FieldModel fieldModel = new FieldModel(node);
                if (fieldModel.valid()) {
                    fieldModels.add(fieldModel);
                    if (fieldModel.calculated) {
                        ++numCalcFields;
                    }
                    if (fieldModel.validate) {
                        ++numValidateFields;
                    }
                }
            }
        }

        for (int i = 0; i < document.getElementsByTagNameNS("*", "Index").getLength(); i++) {
            Node node = document.getElementsByTagNameNS("*", "Index").item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                indexModels.add(new IndexModel(node));
            }
        }

        for (int i = 0; i < document.getElementsByTagNameNS("*", "FieldFilter").getLength(); ++i) {
            Node node = document.getElementsByTagNameNS("*", "FieldFilter").item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                fieldFilterModels.add(new FieldFilterModel(node));
            }
        }

        nodeList = document.getElementsByTagNameNS("*", "MasterSource");
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            masterSourceClass = node.getAttributes().getNamedItem("class").getNodeValue();
            masterSourceField = node.getAttributes().getNamedItem("field").getNodeValue();
        }
    }

    void run() throws IOException {
        if (Files.exists(pathTable) && Files.size(pathTable) > 0) {
            updateFileTable();
        } else {
            createFileTable();
        }
        if (Files.exists(pathModel) && Files.size(pathModel) > 0) {
            updateDataModel();
        } else {
            createDataModel();
        }
    }

    private String getTableDescriptorBuffer() {
        StringBuilder builder = new StringBuilder();

        if (fieldModels.size() > 0) {
            builder.append("\n");
            for (FieldModel fieldModel : fieldModels) {
                String cast;
                if (fieldModel.type.contentEquals("TableField")) {
                    cast = "<" + fieldModel.className + ">";
                } else {
                    cast = "";
                }
                builder.
                        append("    public final MField").
                        append(fieldModel.type).append(cast).
                        append(" field_").
                        append(fieldModel.fieldName).
                        append(" = new MField").append(fieldModel.type).
                        append(cast).
                        append("(this, \"").
                        append(fieldModel.fieldName).
                        append("\"").
                        append(")");

                String initializeString = "";

                if (fieldModel.autoInc) {
                    initializeString += "            autoInc = true;\n";
                }

                if (fieldModel.calculated) {
                    initializeString += "            calculated = true;\n";
                    initializeString += "            calcValue = () -> calcField_" + fieldModel.fieldName + "();\n";
                }

                if (fieldModel.validate) {
                    initializeString += "            onValidate = () -> onValidate_" + fieldModel.fieldName + "();\n";
                }

                if (fieldModel.required) {
                    initializeString += "            required = true;\n";
                }

                if (fieldModel.notNullable) {
                    initializeString += "            notNullable = true;\n";
                }

                if (fieldModel.description != null) {
                    initializeString += "            description = \"" + fieldModel.description + "\";\n";
                }

                if (fieldModel.label != null) {
                    initializeString += "            label = \"" + fieldModel.label + "\";\n";
                }

                if (fieldModel.newDate) {
                    initializeString += "            mNewDate = true;\n";
                }

                if (fieldModel.newValue != null) {
                    initializeString += "            setCallableNewValue(() -> " + fieldModel.newValue + ");\n";
                }

                if (fieldModel.defaultValue != null) {
                    initializeString += "            setDefaultValue(" + fieldModel.defaultValue + ");\n";
                }

                if (fieldModel.onAfterChangeValue != null) {
                    initializeString += "            setRunnableOnAfterChangeValue(() -> " + fieldModel.onAfterChangeValue + ");\n";
                }

                if (fieldModel.keyValueItems.size() > 0) {
                    initializeString += "\n            valueItems = new HashMap<>();\n";
                    final String[] line = {""};
                    fieldModel.keyValueItems.forEach((key, value) -> line[0] += "            valueItems.put(" + key + ", \"" + value + "\");\n");
                    initializeString += line[0];
                }

                /* initialize method */
                builder.
                        append(" {\n").
                        append("        @Override\n").
                        append("        protected void initialize() {\n").
                        append(initializeString).
                        append("        }\n");

                /* FieldTableField */
                if (fieldModel.type.contentEquals("TableField")) {
                    builder.
                            append("\n").
                            append("        @Override\n").
                            append("        protected ").
                            append(fieldModel.className).
                            append(" buildTableField() {\n").
                            append("            return new ").
                            append(fieldModel.className).
                            append("();\n").
                            append("        }\n");
                }

                builder.append("    };\n\n");
            }
        }

        /* Indexes */
        if (indexModels.size() > 0) {
            indexModels.forEach(indexModel -> {
                builder.
                        append("    public final MIndex index_").
                        append(indexModel.getName()).
                        append(" = new MIndex(").
                        append("this, \"").
                        append(indexModel.getName()).
                        append("\", \"").
                        append(indexModel.getMasterKeyField()).
                        append("\", \"").
                        append(indexModel.getKeyField()).
                        append("\", ").
                        append(indexModel.isUnique()).
                        append(", ").
                        append(indexModel.isSparse()).
                        append(") {\n").
                        append("        @Override\n").
                        append("        protected void initialize() {\n");
                if (indexModel.getIndexFieldItems().size() > 0) {
                    indexModel.getIndexFieldItems().forEach(indexFieldItem -> {
                        builder.append("            partialFilter = Filters.").append(indexFieldItem.getLogicOperator()).append("(");
                        if (indexFieldItem.getPartialFilterItems().size() > 0) {
                            final String[] s = {""};
                            indexFieldItem.getPartialFilterItems().forEach((value) -> s[0] += s[0].isEmpty() ? value : (", " + value));
                            builder.append(s[0]);
                        }
                    });
                    builder.append(");\n");
                }
                builder.append("        }\n");
                builder.append("    };\n");
            });
        }

        /* masterSource */
        if (masterSourceClass != null) {
            builder.
                    append("\n").
                    append("    public ").
                    append(className).append("(").
                    append(masterSourceClass).
                    append(" masterSource) {\n").
                    append("        setMasterSource(masterSource, field_").
                    append(masterSourceField).
                    append(");\n").
                    append("    }\n");

            builder.
                    append("\n").
                    append("    @Override\n").
                    append("    public ").
                    append(masterSourceClass).
                    append(" getMasterSource() {\n").
                    append("        return (").
                    append(masterSourceClass).
                    append(") super.getMasterSource();\n").
                    append("    }\n");
        }

        /* FieldFilters */
        if (fieldFilterModels.size() > 0) {
            builder.append("\n");
            builder.append("    public void setFieldFilters() {\n");
            fieldFilterModels.forEach(fieldFilterModel -> {
                builder.append("        field_").append(fieldFilterModel.fieldName).append(".setFilterValue(").append(fieldFilterModel.filterValue).append(");\n");
            });
            builder.append("    }\n");
        }

        /* getTableName */
        if (tableName != null) {
            builder.
                    append("\n").
                    append("    @Override\n").
                    append("    public final String getTableName() {\n").
                    append("        return \"").
                    append(tableName).
                    append("\";\n").
                    append("    }\n");
        }

        /* getGenre */
        if (genre != null) {
            builder.
                    append("\n").
                    append("    @Override\n").
                    append("    public String getGenre() {\n").
                    append("        return \"").
                    append(genre).
                    append("\";\n").
                    append("    }\n");
        }

        /* getGenres */
        if (genre != null) {
            builder.
                    append("\n").
                    append("    @Override\n").
                    append("    public String getGenres() {\n").
                    append("        return \"").
                    append(genres).
                    append("\";\n").
                    append("    }\n");
        }

        /* newDatabase */
        if (database != null) {
            builder.
                    append("\n").
                    append("    @Override\n").
                    append("    protected MDatabase newDatabase() {\n").
                    append("        return new ").
                    append(database).
                    append("(this);\n").
                    append("    }\n");
        }

        if (!isAbstract) {
            builder.
                    append("\n").
                    append("    @Override\n").
                    append("    public ").
                    append(className).
                    append("Data getData() {\n").
                    append("        return new ").
                    append(className).
                    append("Data<>(this);\n").
                    append("    }\n");
        }

        return builder.toString();
    }

    private void createFileTable() {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(pathTable.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (writer != null) {

            writer.println("package " + getPackageName() + ";");
            writer.println();
            writer.println("import tech.fonrouge.MOODB.*;");
            writer.println();
            writer.print("public ");
            if (isAbstract) {
                writer.print("abstract ");
            }
            writer.println("class " + className + " extends " + extendClass + " {");
            writer.println();
            writer.println("    /* @@ begin field descriptor @@ */");
            writer.print(getTableDescriptorBuffer());
            writer.println("    /* @@ end field descriptor @@ */");
            if (fieldModels.size() > 0 && numCalcFields > 0) {
                for (FieldModel fieldModel : fieldModels) {
                    if (fieldModel.calculated) {
                        writer.println(getCalculatedFieldBuffer(fieldModel));
                    }
                }
            }
            if (fieldModels.size() > 0 && numValidateFields > 0) {
                for (FieldModel fieldModel : fieldModels) {
                    if (fieldModel.validate) {
                        writer.println(getValidateFieldBuffer(fieldModel));
                    }
                }
            }
            writer.println("}");
            writer.close();
        }
    }

    private String getCalculatedFieldBuffer(FieldModel fieldModel) {
        String buffer = "";

        buffer += "\n";
        buffer += "    /* @@ begin calcField_" + fieldModel.fieldName + " @@ */\n";
        buffer += "    private " + fieldModel.type + " calcField_" + fieldModel.fieldName + "() {\n";
        buffer += "        return null;\n";
        buffer += "    }\n";
        buffer += "    /* @@ end calcField_" + fieldModel.fieldName + " @@ */";
        return buffer;
    }

    private String getValidateFieldBuffer(FieldModel fieldModel) {
        String buffer = "";

        buffer += "\n";
        buffer += "    /* @@ begin onValidate_" + fieldModel.fieldName + " @@ */\n";
        buffer += "    private boolean onValidate_" + fieldModel.fieldName + "() {\n";
        buffer += "        return true;\n";
        buffer += "    }\n";
        buffer += "    /* @@ end onValidate_" + fieldModel.fieldName + " @@ */";
        return buffer;
    }

    private String getPackageName() {
        StringBuilder result = new StringBuilder();
        String name;
        for (int i = pathTable.getNameCount() - 2; i >= 0; i--) {
            name = pathTable.getName(i).toString();
            if (name.isEmpty() || name.equals(".") || name.equals("src")) {
                return result.toString();
            }
            result.insert(0, name + ((result.length() == 0) ? "" : "."));
        }
        return result.toString();
    }

    private void updateFileTable() {
        Scanner scanner = null;
        StringBuilder calculatedFieldsBuffer = new StringBuilder();
        StringBuilder validateFieldsBuffer = new StringBuilder();

        try {
            scanner = new Scanner(pathTable).useDelimiter("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (scanner != null) {
            List<String> stringList = new ArrayList<>();

            String line;
            final String tokenCalcField = "@@ begin calcField_";
            final String tokenOnValidate = "@@ begin onValidate_";
            String fieldName;

            while (scanner.hasNext()) {
                line = scanner.next();
                if (line.contains(tokenCalcField)) {
                    fieldName = line.substring(line.indexOf(tokenCalcField) + tokenCalcField.length(), line.lastIndexOf(" @@"));
                    if (calcFieldList.indexOf(fieldName) < 0) {
                        calcFieldList.add(fieldName);
                    }
                } else if (line.contains(tokenOnValidate)) {
                    fieldName = line.substring(line.indexOf(tokenOnValidate) + tokenOnValidate.length(), line.lastIndexOf(" @@"));
                    if (onValidateList.indexOf(fieldName) < 0) {
                        onValidateList.add(fieldName);
                    }
                } else {
                    /* check abstract */
                    if (line.contains("class " + className + " extends")) {
                        if (isAbstract && !line.contains("abstract")) {
                            line = line.replace(" class", " abstract class");
                        }
                        if (!isAbstract && line.contains("abstract")) {
                            line = line.replace(" abstract ", " ");
                        }
                        /* checks extends class */
                        if (!line.matches(".* +extends +" + extendClass + " +" + extendClass + ".*")) {
                            line = line.replaceFirst("(extends +\\w+ \\{)", "extends " + extendClass + " {");
                        }
                    }
                }
                stringList.add(line);
            }

            for (FieldModel fieldModel : fieldModels) {
                int i = calcFieldList.indexOf(fieldModel.fieldName);
                if (fieldModel.calculated)
                    if (i >= 0) {
                        calcFieldList.remove(i);
                    } else {
                        calcFieldList.add(fieldModel.fieldName);
                        calculatedFieldsBuffer.append(getCalculatedFieldBuffer(fieldModel));
                    }
                i = onValidateList.indexOf(fieldModel.fieldName);
                if (fieldModel.validate)
                    if (i >= 0) {
                        onValidateList.remove(i);
                    } else {
                        onValidateList.add(fieldModel.fieldName);
                        validateFieldsBuffer.append(getValidateFieldBuffer(fieldModel));
                    }
            }

            PrintWriter writer = getPrintWriter(pathTable);

            boolean insideDescriptor = false;

            if (writer != null) {
                for (String s : stringList) {
                    if (!insideDescriptor) {
                        writer.println(s);
                    }
                    if (s.contains("/* @@ begin field descriptor @@ */")) {
                        writer.print(getTableDescriptorBuffer());
                        insideDescriptor = true;
                    }
                    if (s.contains("/* @@ end field descriptor @@ */")) {
                        insideDescriptor = false;
                        writer.println(s);
                        if (calculatedFieldsBuffer.length() > 0) {
                            writer.println(calculatedFieldsBuffer);
                        }
                        if (validateFieldsBuffer.length() > 0) {
                            writer.println(validateFieldsBuffer);
                        }
                    }
                }
                writer.close();
            }
        }
    }

    private String getModelDescriptorBuffer() {
        StringBuilder builder = new StringBuilder();

        builder.append("    public ").
                append(className).
                append("Data(").
                append("T").
                append(" ").
                append(className.toLowerCase()).
                append(") {\n");
        builder.append("        super(").
                append(className.toLowerCase()).
                append(");\n");
        builder.append("    }\n");

        for (FieldModel fieldModel : fieldModels) {
            builder.append("\n");
            builder.append("    public ");
            String type;
            if ("TableField".equals(fieldModel.type)) {
                type = "ObjectId";
            } else {
                type = fieldModel.type;
            }
            builder.append(type);
            builder.append(" get").
                    append(fieldModel.fieldName.substring(0, 1).toUpperCase()).
                    append(fieldModel.fieldName.substring(1)).
                    append("() {\n");
            builder.append("        return tableState.getFieldValue(");
            builder.append("table.field_").
                    append(fieldModel.fieldName).
                    append(", ").
                    append(type).
                    append(".").
                    append("class);\n");
            builder.append("    }\n");
        }

        return builder.toString();
    }

    private void createDataModel() {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(pathModel.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (writer != null) {
            boolean importDate = false;
            boolean importOTable = false;
            for (FieldModel fieldModel : fieldModels) {
                if (!importDate && fieldModel.type.equals("Date")) {
                    importDate = true;
                }
                if (!importOTable && fieldModel.type.equals("TableField")) {
                    importOTable = true;
                }
            }
            writer.println("package " + getPackageName() + ";");
            writer.println();
            if (importDate) {
                writer.println("import org.bson.types.Binary;\n");
            }
            if (importOTable) {
                writer.println("import org.bson.types.ObjectId;\n");
            }
            if (importDate) {
                writer.println("import java.util.Date;\n");
            }

            writer.println(classInit());

            writer.println();
            writer.println("    /* @@ begin field descriptor @@ */");
            writer.print(getModelDescriptorBuffer());
            writer.println("    /* @@ end field descriptor @@ */");
            writer.println("}\n");

            writer.close();
        }
    }

    private String classInit() {
        String classInit = "public ";
        if (isAbstract) {
            classInit += "abstract ";
        }
        classInit += ("class " + className + "Data<T extends " + className + "> ");
        if (extendClass.contentEquals("MTable")) {
            classInit += ("extends " + "MBaseData");
        } else {
            classInit += ("extends " + extendClass + "Data");
        }
        classInit += ("<T> {");
        return classInit;
    }

    private void updateDataModel() {
        Scanner scanner = null;
        boolean hasFieldDescriptor = false;
        boolean findClassInit = false;

        try {
            scanner = new Scanner(pathModel).useDelimiter("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (scanner != null) {
            List<String> stringList = new ArrayList<>();

            String line;

            while (scanner.hasNext()) {
                line = scanner.next();
                if (!findClassInit && line.matches(" *public +(abstract +)*class .*")) {
                    findClassInit = true;
                    line = classInit();
                }
                stringList.add(line);
            }

            PrintWriter writer = getPrintWriter(pathModel);

            boolean insideDescriptor = false;

            if (writer != null) {
                for (String s : stringList) {
                    if (!insideDescriptor) {
                        writer.println(s);
                    }
                    if (s.contains("/* @@ begin field descriptor @@ */")) {
                        writer.print(getModelDescriptorBuffer());
                        insideDescriptor = true;
                        hasFieldDescriptor = true;
                    }
                    if (s.contains("/* @@ end field descriptor @@ */")) {
                        insideDescriptor = false;
                        writer.println(s);
                    }
                }
                if (!hasFieldDescriptor) {
                    writer.println("/* ERROR: can't find field descriptor delimiters */");
                }
                writer.close();
            }
        }
    }

    private PrintWriter getPrintWriter(Path path) {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(path.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }
}
