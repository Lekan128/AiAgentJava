package org.example.method.caller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ObjectMapperSingleton;
import org.example.Util;
import org.example.method.MethodExecutionResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionCaller {

    public static void main(String[] args) throws Exception {
        String s = Util.convertToString(ReflectionInvocableMethod.class);
        System.out.println(s);
       /* String json = """
        {
          "description": "Get a greeting message for a user",
          "className": "org.example.MyService",
          "methodName": "greetUser",
          "methodArguments": [
            { "type": "java.lang.String", "value": "Alice" },
            {
                "type": "org.example.MyService$AgeAndLevel",
                "value":  {
                    "age": "27",
                    "level": "Three300Hundred"
                }
            }
          ]
        }
        """;

        Object o = invokeMethodFromJson(json);
        System.out.println(o);*/
    }

    public static Object invokeMethodFromJson(String json) throws Exception {
        ObjectMapper mapper = ObjectMapperSingleton.getObjectMapper();
        ReflectionInvocableMethod request = mapper.readValue(json, ReflectionInvocableMethod.class);

        return invokeMethod(request);
    }

    public static Object invokeMethod(ReflectionInvocableMethod request) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Object result = ReflectionCaller.callMethod(
                request.getClassName(),
                request.getMethodName(),
                request.getMethodArguments(),
                null
        );

        System.out.println("Result = " + result);
        return result;
    }

    /*
    * {
          "description": "Get a greeting message for a user",
          "className": "com.example.MyService",
          "methodName": "greetUser",
          "methodArguments": [
            {
              "type": "java.lang.String",
              "value": "Alice"
            },
            {
              "type": "int",
              "value": 5
            }
          ]
    *   }
    * */
    private static Object callMethod(
            String className,
            String methodName,
            List<MethodArgument> args,
            Map<String,Object> methodArgumentPlaceHolders
    ) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(className);
        Object instance = null;

        // Convert argument type names to Class objects
        Class<?>[] paramTypes = new Class<?>[args.size()];
        Object[] paramValues = new Object[args.size()];

        for (int i = 0; i < args.size(); i++) {
            String typeName = args.get(i).getType();
            Object rawValue = args.get(i).getValue();

            // Resolve placeholder if present
            Object resolvedValue =
                    methodArgumentPlaceHolders == null ? rawValue : resolvePlaceholders(rawValue, methodArgumentPlaceHolders);

            Class<?> paramType = getClassFromName(typeName);
            paramTypes[i] = paramType;
            paramValues[i] = convertValue(resolvedValue, paramType);

        }

        Method method = clazz.getMethod(methodName, paramTypes);

        // Check if static
        if (!Modifier.isStatic(method.getModifiers())) {
            instance = clazz.getDeclaredConstructor().newInstance();
        }

        return method.invoke(instance, paramValues);
    }

    public static List<MethodExecutionResult> executePipelineFromJsonList(String jsonList) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();
        List<ReflectionInvocableMethod> reflectionInvocableMethods = objectMapper.readValue(jsonList, new TypeReference<List<ReflectionInvocableMethod>>() {});
        return executePipeline(reflectionInvocableMethods);
    }

    public static List<MethodExecutionResult> executePipeline(List<ReflectionInvocableMethod> requests) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Map<String, Object> context = new HashMap<>();
        List<MethodExecutionResult> results = new ArrayList<>();

        for (ReflectionInvocableMethod req : requests) {
            Object result = callMethodWithContext(req, context);
            if (req.getReturnObjectKey() != null) {
                context.put(req.getReturnObjectKey(), result);
            }

            results.add(new MethodExecutionResult(req, result));
        }
        return results;
    }

    //context is the possible result substitution
    private static Object callMethodWithContext(ReflectionInvocableMethod request, Map<String, Object> context) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return callMethod(
                request.getClassName(),
                request.getMethodName(),
                request.getMethodArguments(),
                context
        );
    }

    private static Class<?> getClassFromName(String typeName) throws ClassNotFoundException {
        switch (typeName) {
            case "int": return int.class;
            case "long": return long.class;
            case "double": return double.class;
            case "boolean": return boolean.class;
            default: return Class.forName(typeName);
        }
    }
    private static Object resolvePlaceholders(Object rawValue, Map<String, Object> context) {
        if (rawValue instanceof String && ((String) rawValue).startsWith("{{")) {
            Object resolved = context.get(rawValue);
            if (resolved == null) {
                throw new IllegalArgumentException("Unresolved placeholder: " + rawValue);
            }
            return resolved;
        }
        return rawValue;
    }
    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType == int.class || targetType == Integer.class) {
            return ((Number) value).intValue();
        }
        if (targetType == long.class || targetType == Long.class) {
            return ((Number) value).longValue();
        }
        if (targetType == double.class || targetType == Double.class) {
            return ((Number) value).doubleValue();
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.valueOf(value.toString());
        }
        if (targetType == String.class) {
            return value.toString();
        }
        if (value instanceof Map) { //it gets automatically converted into a map
            // Re-use Jackson to map Map -> targetType
            return ObjectMapperSingleton.getObjectMapper().convertValue(value, targetType);
        }
        return value; // let Java handle Strings, objects, etc.
    }

}

