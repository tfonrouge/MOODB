package tech.fonrouge.daemon.build;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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

    private List<FieldModel> fieldModels;
    private List<IndexModel> indexModels;
    private List<FieldFilterModel> fieldFilterModels;

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

    private void buildCalculatedField(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, FieldModel fieldModel) {
        MethodDeclaration md = buildMethodDeclaration(classOrInterfaceDeclaration, fieldModel.type, "calcField_" + fieldModel.fieldName, null);
        md.setModifiers(Modifier.Keyword.PRIVATE);
    }

    private void buildDataDescriptor(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {

        BlockStmt body;
        ConstructorDeclaration constructorDeclaration = getCtor(classOrInterfaceDeclaration);

        if (constructorDeclaration == null) {
            constructorDeclaration = new ConstructorDeclaration();
            constructorDeclaration.setName(className + "Data");
            classOrInterfaceDeclaration.getMembers().add(constructorDeclaration);
        }

        Type type = new ClassOrInterfaceType(null, "T");
        constructorDeclaration.getParameters().clear();
        constructorDeclaration.addParameter(type, className.toLowerCase());

        constructorDeclaration.setModifiers(Modifier.Keyword.PUBLIC);
        body = new BlockStmt();
        body.addStatement("super(" + className.toLowerCase() + ");");
        setNodeAnnotation(constructorDeclaration, "AutoGenerated");
        constructorDeclaration.setBody(body);

        for (FieldModel fieldModel : fieldModels) {
            String methodType;
            if ("TableField".equals(fieldModel.type)) {
                methodType = "ObjectId";
            } else {
                methodType = fieldModel.type;
            }
            String methodName = "get" + fieldModel.fieldName.substring(0, 1).toUpperCase();
            methodName += fieldModel.fieldName.substring(1);
            body = new BlockStmt();
            body.addStatement("return tableState.getFieldValue(table.field_" + fieldModel.fieldName + ", " + methodType + ".class);");
            buildMethodDeclaration(classOrInterfaceDeclaration, methodType, methodName, body);
        }
    }

    private void buildFieldDeclaration(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, String fieldType, String fieldName, String initializeString) {
        AtomicReference<FieldDeclaration> fda = new AtomicReference<>();

        classOrInterfaceDeclaration.findAll(FieldDeclaration.class)
                .forEach(fd -> {
                    if (fda.get() == null) {
                        fd.getVariables().forEach(variableDeclarator -> {
                            if (variableDeclarator.getNameAsString().contentEquals(fieldName)) {
                                fda.set(fd);
                            }
                        });
                    }
                });

        FieldDeclaration fieldDeclaration = fda.get();

        VariableDeclarator vd;

        if (fieldDeclaration == null) {

            Type type = new ClassOrInterfaceType(null, fieldType);
            vd = new VariableDeclarator(type, fieldName);

            fieldDeclaration = new FieldDeclaration().addVariable(vd);

            classOrInterfaceDeclaration.getMembers().add(fieldDeclaration);
        } else {
            vd = fieldDeclaration.getVariable(0);
        }

        setNodeAnnotation(fieldDeclaration, "AutoGenerated");

        vd.setInitializer(initializeString);

        fieldDeclaration.setModifiers(Modifier.Keyword.PUBLIC, Modifier.Keyword.FINAL);

        setClassImportForType(classOrInterfaceDeclaration, fieldType);
    }

    private MethodDeclaration buildMethodDeclaration(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, String methodType, String methodName, BlockStmt body) {

        AtomicReference<MethodDeclaration> mda = new AtomicReference<>();

        classOrInterfaceDeclaration.findAll(MethodDeclaration.class)
                .forEach(md -> {
                    if (mda.get() == null) {
                        if (md.getType().asString().contentEquals(methodType) && md.getNameAsString().contentEquals(methodName)) {
                            mda.set(md);
                        }
                    }
                });

        MethodDeclaration methodDeclaration = mda.get();

        if (methodDeclaration == null) {
            methodDeclaration = new MethodDeclaration();
            Type type = new ClassOrInterfaceType(null, methodType);
            methodDeclaration.setType(type);
            if (body == null) {
                if (!methodType.contentEquals("void")) {
                    body = new BlockStmt();
                    body.addStatement("return null;");
                }
            }
            classOrInterfaceDeclaration.addMember(methodDeclaration);
        }

        methodDeclaration.setName(methodName);

        if (body != null) {
            methodDeclaration.setBody(body);
        }

        methodDeclaration.setModifiers(Modifier.Keyword.PUBLIC);

        setNodeAnnotation(methodDeclaration, "AutoGenerated");

        setClassImportForType(classOrInterfaceDeclaration, methodType);

        return methodDeclaration;
    }

    private void buildTableDescriptor(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {

        for (FieldModel fieldModel : fieldModels) {

            buildFieldDeclaration(classOrInterfaceDeclaration, "MField" + fieldModel.type + fieldModel.getCast(), "field_" + fieldModel.fieldName, getInitializeString(fieldModel));

        }

        /*
         * generate index method's
         */
        for (IndexModel indexModel : indexModels) {

            StringBuilder builder = new StringBuilder();
            builder.
                    append("new MIndex(").
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
                builder.append(");");
            }
            builder.append("        }\n");
            builder.append("    }");

            buildFieldDeclaration(classOrInterfaceDeclaration, "MIndex", "index_" + indexModel.getName(), builder.toString());

        }

        /* masterSource */
        if (masterSourceClass != null) {

            BlockStmt body;

            /*
             * build constructor on table with masterSource
             */
            ConstructorDeclaration constructorDeclaration = getCtor(classOrInterfaceDeclaration);

            if (constructorDeclaration == null) {
                constructorDeclaration = new ConstructorDeclaration();
                constructorDeclaration.setName(className);
                classOrInterfaceDeclaration.getMembers().add(constructorDeclaration);
            }

            Type type = new ClassOrInterfaceType(null, masterSourceClass);
            constructorDeclaration.getParameters().clear();
            constructorDeclaration.addParameter(type, "masterSource");

            constructorDeclaration.setModifiers(Modifier.Keyword.PUBLIC);
            body = new BlockStmt();
            body.addStatement("setMasterSource(masterSource, field_" + masterSourceField + ");");
            setNodeAnnotation(constructorDeclaration, "AutoGenerated");
            constructorDeclaration.setBody(body);

            /*
             * build getMasterSource method
             */
            body = new BlockStmt();
            body.addStatement("return (" + masterSourceClass + ") super.getMasterSource();");
            buildMethodDeclaration(classOrInterfaceDeclaration, masterSourceClass, "getMasterSource", body);
        }

        /* FieldFilters */
        if (fieldFilterModels.size() > 0) {
            BlockStmt body = new BlockStmt();
            fieldFilterModels.forEach(fieldFilterModel -> body.addStatement("field_" + fieldFilterModel.fieldName + ".setFilterValue(" + fieldFilterModel.filterValue + ");"));
            buildMethodDeclaration(classOrInterfaceDeclaration, "void", "setFieldFilters", body);
        }

        /* getTableName */
        if (tableName != null) {
            BlockStmt body = new BlockStmt();
            body.addStatement("return \"" + tableName + "\";");
            MethodDeclaration md = buildMethodDeclaration(classOrInterfaceDeclaration, "String", "getTableName", body);
            md.addModifier(Modifier.Keyword.FINAL);
            setNodeAnnotation(md, "Override");
        }

        /* getGenre */
        if (genre != null) {
            BlockStmt body = new BlockStmt();
            body.addStatement("return \"" + genre + "\";");
            MethodDeclaration md = buildMethodDeclaration(classOrInterfaceDeclaration, "String", "getGenre", body);
            setNodeAnnotation(md, "Override");
        }

        /* getGenres */
        if (genre != null) {
            BlockStmt body = new BlockStmt();
            body.addStatement("return \"" + genres + "\";");
            MethodDeclaration md = buildMethodDeclaration(classOrInterfaceDeclaration, "String", "getGenres", body);
            setNodeAnnotation(md, "Override");
        }

        /* getMDatabaseClass */
        if (database != null) {
            BlockStmt body = new BlockStmt();
            body.addStatement("return " + database + ".class;");
            MethodDeclaration md = buildMethodDeclaration(classOrInterfaceDeclaration, "Class", "getMDatabaseClass", body);
            setNodeAnnotation(md, "Override");
        }

        if (!isAbstract) {
            BlockStmt body = new BlockStmt();
            body.addStatement("return new " + className + "Data<>(this);");
            MethodDeclaration md = buildMethodDeclaration(classOrInterfaceDeclaration, className + "Data", "getData", body);
            setNodeAnnotation(md, "Override");
        }
    }

    private void buildValidateField(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, FieldModel fieldModel) {
        MethodDeclaration md = buildMethodDeclaration(classOrInterfaceDeclaration, "boolean", "onValidate_" + fieldModel.fieldName, null);
        md.setModifiers(Modifier.Keyword.PRIVATE);
    }

    private void clearAutoGeneratedAnnotation(CompilationUnit compilationUnit) {

        Consumer<? super BodyDeclaration<?>> consumer = nodeWithAnnotations -> {
            int i = 0;
            while (nodeWithAnnotations.getAnnotations().size() > 0 && i < nodeWithAnnotations.getAnnotations().size()) {
                if (nodeWithAnnotations.getAnnotation(i).getNameAsString().contentEquals("AutoGenerated")) {
                    nodeWithAnnotations.getAnnotations().remove(i);
                    i = 0;
                }
                ++i;
            }
        };
        compilationUnit.findAll(ConstructorDeclaration.class)
                .forEach(consumer);
        compilationUnit.findAll(FieldDeclaration.class)
                .forEach(consumer);
        compilationUnit.findAll(MethodDeclaration.class)
                .forEach(consumer);
    }

    private ConstructorDeclaration getCtor(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        AtomicReference<ConstructorDeclaration> cda = new AtomicReference<>();
        classOrInterfaceDeclaration.findAll(ConstructorDeclaration.class)
                .forEach(cd -> {
                    if (cda.get() == null && cd.getParameters().size() == 1) {
                        cda.set(cd);
                    }
                });
        return cda.get();
    }

    private String getInitializeString(FieldModel fieldModel) {
        StringBuilder builder = new StringBuilder();

        builder.
                append("new MField").append(fieldModel.type).
                append(fieldModel.getCast()).
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

        if (fieldModel.newFinal) {
            initializeString += "            newFinal = true;\n";
        }

        if (fieldModel.validate) {
            initializeString += "            onValidate = () -> onValidate_" + fieldModel.fieldName + "();\n";
        }

        if (fieldModel.required) {
            initializeString += "            required = true;\n";
        }

        if (fieldModel.description != null) {
            initializeString += "            description = \"" + fieldModel.description + "\";\n";
        }

        if (fieldModel.label != null) {
            initializeString += "            label = \"" + fieldModel.label + "\";\n";
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
            initializeString += "\n            valueItems = new ValueItems<>();\n";
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

        builder.append("    }");
        return builder.toString();
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

    void run() {

        updateFileTable();

        updateDataModel();

    }

    private void setClassImportForType(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, final String methodType) {

        Optional<com.github.javaparser.ast.Node> o = classOrInterfaceDeclaration.getParentNode();
        o.ifPresent(node -> {
            CompilationUnit compilationUnit = (CompilationUnit) node;
            String methodBaseType = methodType.contains("<") ? methodType.substring(0, methodType.indexOf("<")) : methodType;
            switch (methodBaseType) {
                case "Date":
                    compilationUnit.addImport("java.util.Date");
                    break;
                case "ObjectId":
                    compilationUnit.addImport("org.bson.types.ObjectId");
                    break;
                case "MFieldBinary":
                case "MFieldBoolean":
                case "MFieldDate":
                case "MFieldDouble":
                case "MFieldInteger":
                case "MFieldLong":
                case "MFieldObject":
                case "MFieldString":
                case "MFieldTableField":
                    compilationUnit.addImport("tech.fonrouge.MOODB.*");
                    break;
            }
        });
    }

    private <T extends BodyDeclaration<?>> void setNodeAnnotation(BodyDeclaration<T> node, String annotation) {
        AtomicReference<Boolean> hasOurAnnotation = new AtomicReference<>(false);
        node.getAnnotations().forEach(annotationExpr -> {
            if (!hasOurAnnotation.get() && annotationExpr.getNameAsString().contentEquals(annotation)) {
                hasOurAnnotation.set(true);
            }
        });

        if (!hasOurAnnotation.get()) {
            node.addAnnotation(annotation);
        }
    }

    private void updateDataModel() {

        try {
            if (Files.notExists(pathModel)) {
                Files.createFile(pathModel);
            }

            String dataClassName = className + "Data";

            CompilationUnit compilationUnit = StaticJavaParser.parse(pathModel.toFile());
            Optional<ClassOrInterfaceDeclaration> optional = compilationUnit.getClassByName(dataClassName);
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

            if (optional.isPresent()) {
                classOrInterfaceDeclaration = optional.get();
            } else {
                compilationUnit = new CompilationUnit();
                compilationUnit.setPackageDeclaration(getPackageName());
                String typedParam = "<T extends " + className + ">";
                classOrInterfaceDeclaration = compilationUnit.addClass(dataClassName + typedParam);
            }

            clearAutoGeneratedAnnotation(compilationUnit);

            compilationUnit.addImport("tech.fonrouge.MOODB.Annotations.AutoGenerated");

            setNodeAnnotation(classOrInterfaceDeclaration, "AutoGenerated");

            classOrInterfaceDeclaration.setModifier(Modifier.Keyword.PUBLIC, true);
            classOrInterfaceDeclaration.setAbstract(isAbstract);
            classOrInterfaceDeclaration.getExtendedTypes().clear();

            String extend = extendClass.contentEquals("MTable") ? "MBaseData" : extendClass + "Data";
            classOrInterfaceDeclaration.addExtendedType(extend + "<T>");

            buildDataDescriptor(classOrInterfaceDeclaration);

            Files.write(pathModel, compilationUnit.toString().getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFileTable() {

        try {
            if (Files.notExists(pathTable)) {
                Files.createFile(pathTable);
            }

            CompilationUnit compilationUnit = StaticJavaParser.parse(pathTable.toFile());
            Optional<ClassOrInterfaceDeclaration> optional = compilationUnit.getClassByName(className);
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

            if (optional.isPresent()) {
                classOrInterfaceDeclaration = optional.get();
            } else {
                compilationUnit = new CompilationUnit();
                compilationUnit.setPackageDeclaration(getPackageName());
                classOrInterfaceDeclaration = compilationUnit.addClass(className);
            }

            clearAutoGeneratedAnnotation(compilationUnit);

            compilationUnit.addImport("tech.fonrouge.MOODB.Annotations.AutoGenerated");

            setNodeAnnotation(classOrInterfaceDeclaration, "AutoGenerated");
            classOrInterfaceDeclaration.setModifier(Modifier.Keyword.PUBLIC, true);
            classOrInterfaceDeclaration.setAbstract(isAbstract);
            classOrInterfaceDeclaration.getExtendedTypes().clear();
            classOrInterfaceDeclaration.addExtendedType(extendClass);

            buildTableDescriptor(classOrInterfaceDeclaration);

            for (FieldModel fieldModel : fieldModels) {
                if (fieldModel.calculated) {
                    buildCalculatedField(classOrInterfaceDeclaration, fieldModel);
                }
                if (fieldModel.validate) {
                    buildValidateField(classOrInterfaceDeclaration, fieldModel);
                }
            }

            Files.write(pathTable, compilationUnit.toString().getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
