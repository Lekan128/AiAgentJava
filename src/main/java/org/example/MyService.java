package org.example;

import org.example.method.AiToolMethod;
import org.example.method.description.ArgDesc;

public class MyService {
    public static class AgeAndLevel{
        int age;
        String level;
    }

//    @AiToolMethod("Used to greet the user")
    public String greetUser(@ArgDesc("user's name") String name, @ArgDesc("user's age and level") AgeAndLevel a){
        return "Hi, " + name + " you are " + a.age + " years old. " + " level " + a.level;
    }
}

