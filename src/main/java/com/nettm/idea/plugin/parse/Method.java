package com.nettm.idea.plugin.parse;

import java.util.List;

public class Method {

    private String name;

    private String returnType;

    private List<Pair> parameterList;

    private String parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<Pair> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<Pair> parameterList) {
        this.parameterList = parameterList;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
