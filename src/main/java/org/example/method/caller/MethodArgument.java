package org.example.method.caller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MethodArgument {
    @JsonProperty
    private String type;

    @JsonProperty
    private Object value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}