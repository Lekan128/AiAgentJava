package org.example.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.method.AiToolMethod;
import org.example.method.description.MethodDescription;
import org.example.method.description.MethodDescriptor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AiUtil {
    public static String getAiToolsAsJson(String fromPackage){
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(fromPackage))
                .setScanners(Scanners.SubTypes, Scanners.TypesAnnotated, Scanners.MethodsAnnotated));

        Set<Method> methods = reflections.getMethodsAnnotatedWith(AiToolMethod.class);
        List<MethodDescription> methodDescriptions = new ArrayList<>();


        for (Method m : methods) {
            MethodDescription desc = MethodDescriptor.describeMethod(m);
            if (desc != null) methodDescriptions.add(desc);
        }

        String json = null;
        try {
            json = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(methodDescriptions);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return json;
    }
}
