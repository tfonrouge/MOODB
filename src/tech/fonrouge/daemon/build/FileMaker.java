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
    private final String database;
    private String masterSourceClass = null;
    private String masterSourceField = null;

    private int numCalcFields = 0;
    private List<FieldModel> fieldModels;
    private ArrayList<IndexItem> mIndices;
    private List<String> calcFieldList = new ArrayList<>();

    FileMaker(Path pathExtLess, Document document, String className) {

        NamedNodeMap namedNodeMap = document.getElementsByTagNameNS("*", "Table").item(0).getAttributes();
        isAbstract = namedNodeMap.getNamedItem("abstract") != null && namedNodeMap.getNamedItem("abstract").getNodeValue().equals("true");
        extendClass = namedNodeMap.getNamedItem("extends") == null ? "MTable" : namedNodeMap.getNamedItem("extends").getNodeValue();
        tableName = namedNodeMap.getNamedItem("tableName") == null ? null : namedNodeMap.getNamedItem("tableName").getNodeValue();
        database = namedNodeMap.getNamedItem("database") == null ? null : namedNodeMap.getNamedItem("database").getNodeValue();

        this.pathTable = Paths.get(pathExtLess.toString() + ".java");
        this.pathModel = Paths.get(pathExtLess.toString() + "Model.java");
        this.className = className;
        fieldModels = new ArrayList<>();
        mIndices = new ArrayList<>();

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
                }
            }
        }

        for (int i = 0; i < document.getElementsByTagNameNS("*", "Index").getLength(); i++) {
            Node node = document.getElementsByTagNameNS("*", "Index").item(i);
            Node node1;

            NamedNodeMap nodeMap = node.getAttributes();

            String name = nodeMap.getNamedItem("name").getNodeValue();

            node1 = nodeMap.getNamedItem("keyField");
            String keyField = node1 == null ? "" : node1.getNodeValue();

            node1 = nodeMap.getNamedItem("masterKeyField");
            String masterKeyField = node1 == null ? "" : node1.getNodeValue();

            node1 = nodeMap.getNamedItem("descending");
            boolean ascending = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = nodeMap.getNamedItem("unique");
            boolean unique = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            mIndices.add(new IndexItem(name, keyField, masterKeyField, ascending, unique));
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
        writeModel();
    }

    private String getTableDescriptorBuffer() {
        StringBuilder buffer = new StringBuilder();

        if (fieldModels.size() > 0) {
            buffer.append("\n");
            for (FieldModel fieldModel : fieldModels) {
                String cast = "";
                if (fieldModel.type.contentEquals("TableField")) {
                    cast = "<" + fieldModel.className + ">";
                } else {
                    cast = "";
                }
                buffer.
                        append("    public final MField").
                        append(fieldModel.type + cast).
                        append(" field_").
                        append(fieldModel.fieldName).
                        append(" = new MField").
                        append(fieldModel.type + cast).
                        append("(this, \"").
                        append(fieldModel.fieldName).
                        append("\"").
                        append(")");

                String initializeString = "";

                if (fieldModel.calculated) {
                    initializeString += "            mCalculated = true;\n";
                    initializeString += "            calcValue = () -> calcField_" + fieldModel.fieldName + "();\n";
                }

                if (fieldModel.required) {
                    initializeString += "            mRequired = true;\n";
                }

                if (fieldModel.description != null) {
                    initializeString += "            mDescription = \"" + fieldModel.description + "\";\n";
                }

                if (fieldModel.keyValueItems.size() > 0) {
                    initializeString += "\n            mKeyValueItems = new HashMap<>();\n";
                    final String[] line = {""};
                    fieldModel.keyValueItems.forEach((key, value) -> line[0] += "            mKeyValueItems.put(\"" + key + "\", \"" + value + "\");\n");
                    initializeString += line[0];
                }

                /* initialize method */
                buffer.
                        append(" {\n").
                        append("        @Override\n").
                        append("        protected void initialize() {\n").
                        append(initializeString).
                        append("        }\n");

                /* FieldTableField */
                if (fieldModel.type.contentEquals("TableField")) {
                    buffer.
                            append("\n").
                            append("        @Override\n").
                            append("        protected " + fieldModel.className + " buildTable() {\n").
                            append("            return new ").
                            append(fieldModel.className).
                            append("();\n").
                            append("        }\n");
                }

                /* newValue */
                if (!fieldModel.newValue.isEmpty()) {
                    buffer.
                            append("\n").
                            append("        @Override\n").
                            append("        protected " + fieldModel.type + " getNewValue() {\n").
                            append("            return ").
                            append(fieldModel.newValue).
                            append("\n").
                            append("        }\n");
                }

                buffer.append("    };\n");
            }
        }

        buffer.append("\n");

        if (mIndices.size() > 0) {
            mIndices.forEach(mIndex -> buffer.
                    append("    public final MIndex index_").
                    append(mIndex.mName).
                    append(" = new MIndex(").
                    append("this, \"").
                    append(mIndex.name()).
                    append("\", \"").
                    append(mIndex.masterKeyField()).
                    append("\", \"").
                    append(mIndex.keyField()).
                    append("\", ").
                    append(mIndex.descending()).
                    append(", ").
                    append(mIndex.unique()).
                    append(");\n")
            );
            buffer.append("\n");
        }

        if (!isAbstract) {
            buffer.
                    append("    private ").
                    append(className).
                    append("Model m;\n");
        }

        /* masterSource */
        if (masterSourceClass != null) {
            buffer.
                    append("\n").
                    append("    public ").
                    append(className).
                    append("(" + masterSourceClass + " masterSource) {\n").
                    append("        setMasterSource(masterSource, field_" + masterSourceField + ");\n").
                    append("    }\n");
        }

        /* getTableName */
        if (tableName != null) {
            buffer.
                    append("\n").
                    append("    @Override\n").
                    append("    public final String getTableName() {\n").
                    append("        return \"").
                    append(tableName).
                    append("\";\n").
                    append("    }\n");
        }

        /* newDatabase */
        if (database != null) {
            buffer.
                    append("\n").
                    append("    @Override\n").
                    append("    protected MDatabase newDatabase() {\n").
                    append("        return new ").
                    append(database).
                    append("(this);\n").
                    append("    }\n");
        }

        if (!isAbstract) {
            buffer.
                    append("\n").
                    append("    @Override\n").
                    append("    protected void initializeModel() {\n").
                    append("        m = new ").
                    append(className).
                    append("Model();\n").
                    append("        m.setTable(this);\n").
                    append("    }\n");
        }

        return buffer.toString();
    }

    private void createFileTable() {
        try {
            PrintWriter writer = new PrintWriter(pathTable.toString());
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
            writer.println("}");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        try {
            scanner = new Scanner(pathTable).useDelimiter("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (scanner != null) {
            List<String> stringList = new ArrayList<>();

            String line;
            String token = "@@ begin calcField_";
            String fieldName;

            while (scanner.hasNext()) {
                line = scanner.next();
                if (line.contains(token)) {
                    fieldName = line.substring(line.indexOf(token) + token.length(), line.lastIndexOf(" @@"));
                    if (calcFieldList.indexOf(fieldName) < 0) {
                        calcFieldList.add(fieldName);
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
            }

            PrintWriter writer = null;

            try {
                //Files.deleteIfExists(pathTable);
                writer = new PrintWriter(pathTable.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean insideFieldDescriptor = false;

            if (writer != null) {
                for (String s : stringList) {
                    if (!insideFieldDescriptor) {
                        writer.println(s);
                    }
                    if (s.contains("/* @@ begin field descriptor @@ */")) {
                        writer.print(getTableDescriptorBuffer());
                        insideFieldDescriptor = true;
                    }
                    if (s.contains("/* @@ end field descriptor @@ */")) {
                        insideFieldDescriptor = false;
                        writer.println(s);
                        if (calculatedFieldsBuffer.length() > 0) {
                            writer.println(calculatedFieldsBuffer);
                        }
                    }
                }
                writer.close();
            }
        }
    }

    private void writeModel() {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(pathModel.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (writer != null) {
            boolean importDate = false;
            boolean importOTable = false;
            boolean importBinary = false;
            for (FieldModel fieldModel : fieldModels) {
                if (!importDate && fieldModel.type.equals("Date")) {
                    importDate = true;
                }
                if (!importOTable && fieldModel.type.equals("TableField")) {
                    importOTable = true;
                }
                if (!importDate && fieldModel.type.equals("Binary")) {
                    importBinary = true;
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
            writer.print("public ");
            if (isAbstract) {
                writer.print("abstract ");
            }
            writer.print("class " + className + "Model ");
            if (!extendClass.contentEquals("MTable")) {
                writer.print("extends " + extendClass + "Model ");
            }
            writer.println("{");
            writer.println();
            writer.println("    protected " + className + " " + className.toLowerCase() + ";");
            writer.println();
            writer.println("    public void setTable(" + className + " " + className.toLowerCase() + ") {");
            writer.println("        this." + className.toLowerCase() + " = " + className.toLowerCase() + ";");
            writer.println("    }");

            for (FieldModel fieldModel : fieldModels) {
                String s;
                if (fieldModel.type.contentEquals("TableField")) {
                    s = "ObjectId";
                } else {
                    s = fieldModel.type;
                }
                writer.println();
                writer.println("    public " + s + " get" + fieldModel.fieldName.substring(0, 1).toUpperCase() + fieldModel.fieldName.substring(1) + "() {");
                writer.println("        return " + className.toLowerCase() + ".field_" + fieldModel.fieldName + ".value();");
                writer.println("    }");
            }

            writer.println("}");
            writer.close();
        }
    }

    class IndexItem {
        boolean mDescending = true;
        String mKeyField;
        String mMasterKeyField;
        String mName;
        boolean mUnique = false;

        public IndexItem(String name, String keyField, String masterKeyField, boolean descending, boolean unique) {
            mName = name;
            mKeyField = keyField;
            mMasterKeyField = masterKeyField;
            mDescending = descending;
            mUnique = unique;
        }

        /* ************** */
        /* public methods */
        /* ************** */

        /**
         * descending
         *
         * @return boolean for descending index value
         */
        public boolean descending() {
            return mDescending;
        }

        /**
         * keyField
         *
         * @return String of key field
         */
        public String keyField() {
            return mKeyField;
        }

        /**
         * masterKeyField
         *
         * @return String of master key field
         */
        public String masterKeyField() {
            return mMasterKeyField;
        }

        /**
         * name
         *
         * @return String name of index
         */
        public String name() {
            return mName;
        }

        /**
         * unique
         *
         * @return boolean for unique index value
         */
        public boolean unique() {
            return mUnique;
        }
    }

}
