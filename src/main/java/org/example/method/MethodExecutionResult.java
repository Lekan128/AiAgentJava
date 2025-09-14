package org.example.method;

import org.example.method.caller.ReflectionInvocableMethod;

public class MethodExecutionResult {
    private ReflectionInvocableMethod request;
    private Object response;

    public MethodExecutionResult() {
    }

    public MethodExecutionResult(ReflectionInvocableMethod request, Object response) {
        this.request = request;
        this.response = response;
    }

    public ReflectionInvocableMethod getRequest() {
        return request;
    }

    public void setRequest(ReflectionInvocableMethod request) {
        this.request = request;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
