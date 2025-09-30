package org.example.ai;

public abstract class LLM {
    public abstract String getModelName();
    public abstract String call(String prompt);
}
