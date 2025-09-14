package org.example.method.caller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class ReflectionInvocableMethod {
    @JsonProperty
    private String className;
    @JsonProperty
    private String methodName;
    @JsonProperty
    private List<Map<String, Object>> methodArguments;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<Map<String, Object>> getMethodArguments() {
        return methodArguments;
    }

    public void setMethodArguments(List<Map<String, Object>> methodArguments) {
        this.methodArguments = methodArguments;
    }
}
