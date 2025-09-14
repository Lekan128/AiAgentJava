package org.example.method.caller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public class ReflectionCaller {
    /*
    * To test:
    * String className = AnyObject.class.getName();
    * String methodName = "anyMethodInTheClass";
    * Class<?> clazz = Class.forName(className);
    * java.lang.reflect.Method method = clazz.getMethod(methodName, Type.class);
    * Object invoke = method.invoke(null, Response.class);
    * */

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
        ObjectMapper mapper = new ObjectMapper();
//        Map<String, Object> map = mapper.readValue(json, Map.class);
        ReflectionInvocableMethod request = mapper.readValue(json, ReflectionInvocableMethod.class);

        return invokeMethod(request);
    }

    public static Object invokeMethod(ReflectionInvocableMethod request) throws Exception {
        Object result = ReflectionCaller.callMethod(
                request.getClassName(),
                request.getMethodName(),
                request.getMethodArguments()
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
    public static Object callMethod(
            String className,
            String methodName,
            List<Map<String, Object>> args
    ) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = Class.forName(className);
        Object instance = null;

        // Convert argument type names to Class objects
        Class<?>[] paramTypes = new Class<?>[args.size()];
        Object[] paramValues = new Object[args.size()];

        for (int i = 0; i < args.size(); i++) {
            String typeName = (String) args.get(i).get("type");
            Object value = args.get(i).get("value");

            Class<?> paramType = getClassFromName(typeName);
            paramTypes[i] = paramType;
            paramValues[i] = convertValue(value, paramType);
        }

        Method method = clazz.getMethod(methodName, paramTypes);

        // Check if static
        if (!Modifier.isStatic(method.getModifiers())) {
            instance = clazz.getDeclaredConstructor().newInstance();
        }

        return method.invoke(instance, paramValues);
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
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(
                    com.fasterxml.jackson.annotation.PropertyAccessor.FIELD,
                    com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
            );

            // Re-use Jackson to map Map -> targetType
            return mapper.convertValue(value, targetType);
        }
        return value; // let Java handle Strings, objects, etc.
    }


    //To convert to reflection method:
    // Annotation on the method (annotation has variable description),
    // it checks that package for every method with that annotation
    //
}

