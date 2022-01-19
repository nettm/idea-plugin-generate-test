package com.nettm.idea.plugin.parse;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParseJava {

    public static JavaClass parse(String javaName, String filePath) {
        JavaClass javaClass = new JavaClass();
        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(new File(filePath));
            List<MethodDeclaration> methods = compilationUnit.findAll(MethodDeclaration.class);
            List<FieldDeclaration> fields = compilationUnit.findAll(FieldDeclaration.class);
            List<ClassOrInterfaceDeclaration> interfaces = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);

            List<ImportDeclaration> importDeclarations = compilationUnit.findAll(ImportDeclaration.class);
            String packageName = compilationUnit.findFirst(PackageDeclaration.class).isPresent() ?
                    compilationUnit.findFirst(PackageDeclaration.class).get().getNameAsString() : null;

            List<String> importNames = new ArrayList<>();
            for (ImportDeclaration declaration : importDeclarations) {
                String importName = declaration.getNameAsString();
                if (declaration.toString().contains(".*")) {
                    importName += ".*";
                }

                importNames.add(importName);
            }

            List<String> interfaceNames = new ArrayList<>();
            for (ClassOrInterfaceDeclaration anInterface : interfaces) {
                anInterface.getImplementedTypes().forEach(type -> {
                    String interfaceName = type.getNameAsString();
                    interfaceNames.add(interfaceName);
                });
            }

            List<Pair> fieldList = new ArrayList<>();
            for (FieldDeclaration field : fields) {
                Pair pair = new Pair();
                pair.setKey(field.getVariable(0).getNameAsString());
                pair.setValue(field.getVariable(0).getTypeAsString());
                fieldList.add(pair);
            }

            List<Method> methodList = new ArrayList<>();
            for (MethodDeclaration method : methods) {
                if (!isPublic(method)) {
                    continue;
                }

                List<Pair> parameters = new ArrayList<>();
                NodeList<Parameter> paramList = method.getParameters();
                for (Parameter parameter : paramList) {
                    Pair pair = new Pair();
                    pair.setKey(parameter.getNameAsString());
                    pair.setValue(parameter.getTypeAsString());
                    parameters.add(pair);
                }

                List<String> parameterList = parameters.stream().map(Pair::getKey).collect(Collectors.toList());
                Method m = new Method();
                m.setName(method.getNameAsString());
                m.setReturnType(method.getTypeAsString());
                m.setParameterList(parameters);
                m.setParameters(StringUtils.join(parameterList, ", "));
                methodList.add(m);
            }

            javaClass.setJavaName(javaName);
            javaClass.setPackageName(packageName);
            javaClass.setImportNames(importNames);
            javaClass.setInterfaceNames(interfaceNames);
            javaClass.setFields(fieldList);
            javaClass.setMethods(methodList);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return javaClass;
    }

    private static boolean isPublic(MethodDeclaration method) {
        NodeList<Modifier> modifiers = method.getModifiers();
        if (modifiers.isEmpty()) {
            return false;
        }
        for (Modifier modifier : modifiers) {
            if (modifier.getKeyword().asString().equalsIgnoreCase("public")) {
                return true;
            }
        }
        return false;
    }
}
