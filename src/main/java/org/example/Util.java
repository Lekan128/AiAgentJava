package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Util {

    public static String convertToString(Type target) throws JsonProcessingException {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
                SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);

        SchemaGenerator generator = new SchemaGenerator(configBuilder.build());
        JsonNode fullSchema = generator.generateSchema(target);
        JsonNode reducedSchema = fullSchema.get("properties");

//        String schema = generator.generateSchema(Response.class).toPrettyString();
        Map<String, Object> flatSchema = flattenSchema(reducedSchema);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flatSchema);

        System.out.println(json);
        return json;
    }
    private static Map<String, Object> flattenSchema(JsonNode propertiesNode) {
        Map<String, Object> result = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = propertiesNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode fieldDef = field.getValue();

            String type = fieldDef.get("type").asText();

            switch (type) {
                case "object":
                    // Nested object → recurse
                    if (fieldDef.has("properties")) {
                        result.put(fieldName, flattenSchema(fieldDef.get("properties")));
                    } else {
                        // For maps: additionalProperties defines the type of values
                        if (fieldDef.has("additionalProperties")) {
                            JsonNode valType = fieldDef.get("additionalProperties");
                            result.put(fieldName, Map.of("key", valType.get("type").asText()));
                        } else {
                            result.put(fieldName, "{}");
                        }
                    }
                    break;

                case "array":
                    JsonNode items = fieldDef.get("items");
                    if (items.get("type").asText().equals("object")) {
                        // Array of objects → recurse
                        if (items.has("properties")) {
                            result.put(fieldName,
                                    new Object[]{flattenSchema(items.get("properties"))});
                        } else {
                            result.put(fieldName, new Object[]{"{}"});
                        }
                    } else {
                        // Array of scalars
                        result.put(fieldName, new String[]{items.get("type").asText()});
                    }
                    break;

                default:
                    // Scalars (string, integer, boolean, etc.)
                    result.put(fieldName, type);
            }
        }
        return result;
    }


    public static void callAMethod(String className, String methodName, String... methodParameterTypeNames) throws ClassNotFoundException {
        /*
        {
            [
                {
                    "description": "String"
                    "className": "String",
                    "methodName": "String",
                    "methodArguments": [
                        {},

                    ]
                }
            ]

        }
        */
        Class<?> clazz = Class.forName(className);
//        Class<?>... methodParameterTypes = ;

        Class<?> [] classes= new Class [methodParameterTypeNames.length];
        for (int i = 0; i < methodParameterTypeNames.length; ++i){
            classes[i] = Class.forName(methodParameterTypeNames[i]);
        }


//        java.lang.reflect.Method method = clazz.getMethod(methodName,  classes);
//        Object invoke = method.invoke(null, n);

    }

    /*private static Map<String, Object> flattenProperties(JsonNode propertiesNode) {
        Map<String, Object> result = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = propertiesNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode fieldDef = field.getValue();

            String type = fieldDef.get("type").asText();

            switch (type) {
                case "object":
                    if (fieldDef.has("properties")) {
                        // Nested object
                        result.put(fieldName, flattenProperties(fieldDef.get("properties")));
                    } else if (fieldDef.has("additionalProperties")) {
                        // Map type: "map<string, ...>"
                        JsonNode valType = fieldDef.get("additionalProperties");
                        String valueType = valType.get("type").asText();
                        result.put(fieldName, "map<string," + valueType + ">");
                    } else {
                        result.put(fieldName, "{}");
                    }
                    break;

                case "array":
                    JsonNode items = fieldDef.get("items");
                    if ("object".equals(items.get("type").asText())) {
                        if (items.has("properties")) {
                            result.put(fieldName,
                                    new Object[]{flattenProperties(items.get("properties"))});
                        } else {
                            result.put(fieldName, new Object[]{"{}"});
                        }
                    } else {
                        result.put(fieldName, new String[]{items.get("type").asText()});
                    }
                    break;

                default:
                    result.put(fieldName, type);
            }
        }
        return result;
    }*/
}
