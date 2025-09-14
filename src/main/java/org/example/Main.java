package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ai.Gemini;
import org.example.method.MethodExecutionResult;
import org.example.method.caller.ReflectionCaller;
import org.example.method.caller.ReflectionInvocableMethod;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    /*PLAN:
    *
    * Call gemini
    * Get a plan from it to use tools
    * Tell it what you want to do
    * send it the reply from hitting the tools
    *
    *
    * Tell gemini to do something and it should respond with only json (from a class)
    * parse json into the class
    * */


    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<ReflectionInvocableMethod> invocableMethodList = Gemini.callWithToolsForPlan(
                "SooPure Lait hydratant moisturising lotion",
                "A product describer, that give description of products to be sold online"
        );

        String s = """
                ```json
                [
                  {
                    "className": "org.example.web.search.DuckDuckGo",
                    "methodName": "search",
                    "methodArguments": [
                      {
                        "type": "java.lang.String",
                        "value": "SooPure Lait hydratant moisturising lotion"
                      }
                    ]
                  }
                ]
                ```
                """;

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setVisibility(
                com.fasterxml.jackson.annotation.PropertyAccessor.FIELD,
                com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
        );

        List<ReflectionInvocableMethod> list =
                objectMapper.readValue(s.replace("```json", "")
                        .replace("```", ""), new TypeReference<>(){} );

        List<MethodExecutionResult> returnedResults = new ArrayList<>();
        try{
            for (ReflectionInvocableMethod method : list ){
                Object response = ReflectionCaller.invokeMethod(method);
                returnedResults.add(new MethodExecutionResult(method, response));
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }




        System.out.println("###############");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(list));
        System.out.println("###############");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(returnedResults));
    }
String listOfSearchResult = """
            [ {
                      "url" : "https://www.jumia.com.ng/generic-minimie-chinchin-snack-jar-393586579.html",
                      "title" : "Generic Minimie Chinchin Snack Jar",
                      "content" : "Product details Minimie Chinchin provides quality, nutritious and hygienically prepared Ready-to-Eat snacks for the mobile consumers. With Minimie Chinchin, families do not have to worry about healthy, tasty and nutritious lunch and snack packs for their school going children. Minimie Chin-chin comes in an attractive, consumer friendly and more distinctive gorgeous pack. The food-friendly packaging material helps to retain the delicious, fresh and crunchy taste over time. Minimie Chinchin is a great tasting, more flavoured, hygienically made and packaged product.Minimie Chinchin provides quality, nutritious and hygienically prepared Ready-to-Eat snacks for the mobile consumers. With Minimie Chinchin, families do not have to worry about healthy, tasty and nutritious lunch and snack packs fo..."
                    }, {
                      "url" : "https://www.jumia.com.ng/generic-minimie-chinchin-2rolls20pcs-393586650.html",
                      "title" : "Generic Minimie Chinchin--2rolls(20pcs) | Jumia Nigeria",
                      "content" : "Product details Minimie Chinchin provides quality, nutritious and hygienically prepared Ready-to-Eat snacks for the mobile consumers. With Minimie Chinchin, families do not have to worry about healthy, tasty and nutritious lunch and snack packs for their school going children. Minimie Chin-chin comes in an attractive, consumer friendly and more distinctive gorgeous pack. The food-friendly packaging material helps to retain the delicious, fresh and crunchy taste over time. Minimie Chinchin is a great tasting, more flavoured, hygienically made and packaged product.Minimie Chinchin provides quality, nutritious and hygienically prepared Ready-to-Eat snacks for the mobile consumers. With Minimie Chinchin, families do not have to worry about healthy, tasty and nutritious lunch and snack packs fo..."
                    }
            ]
        """;
}