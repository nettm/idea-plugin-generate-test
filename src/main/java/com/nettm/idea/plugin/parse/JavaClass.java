package com.nettm.idea.plugin.parse;

import java.util.List;

public class JavaClass {

    private String javaName;

    private String packageName;

    private List<String> importNames;

    private List<String> interfaceNames;

    private List<Pair> fields;

    private List<Method> methods;

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getImportNames() {
        return importNames;
    }

    public void setImportNames(List<String> importNames) {
        this.importNames = importNames;
    }

    public List<String> getInterfaceNames() {
        return interfaceNames;
    }

    public void setInterfaceNames(List<String> interfaceNames) {
        this.interfaceNames = interfaceNames;
    }

    public List<Pair> getFields() {
        return fields;
    }

    public void setFields(List<Pair> fields) {
        this.fields = fields;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }
}
