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


    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        String userQuery = "Describe my top product";
//        String userQuery = "SooPure Lait hydratant moisturising lotion";
        String aiPersona = "A product describer, that give description of products to be sold online";
        List<ReflectionInvocableMethod> invocableMethodList = Gemini.callWithToolsForPlan(
                userQuery,
                aiPersona
        );

        List<MethodExecutionResult> methodExecutionResults = ReflectionCaller.executePipeline(invocableMethodList);
        System.out.println(ObjectMapperSingleton.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(methodExecutionResults));


        Response response = Gemini.callForFinalResponse(aiPersona, userQuery, methodExecutionResults);


        System.out.println("###############");
        System.out.println(ObjectMapperSingleton.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response));
        System.out.println("###############");
        System.out.println(ObjectMapperSingleton.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(methodExecutionResults));
    }

    String listOfInvocableMethodCalls = """
            [ {
              "className" : "org.example.MyService",
              "methodName" : "getCurrentUserId",
              "methodArguments" : [ ],
              "returnObjectKey" : "{{user_id}}"
            }, {
              "className" : "org.example.ProductService",
              "methodName" : "getUsersTopProductName",
              "methodArguments" : [ {
                "type" : "java.lang.String",
                "value" : "{{user_id}}"
              } ],
              "returnObjectKey" : "{{top_product_name}}"
            }, {
              "className" : "org.example.web.WebSearchProcessor",
              "methodName" : "search",
              "methodArguments" : [ {
                "type" : "java.lang.String",
                "value" : "{{top_product_name}}"
              } ],
              "returnObjectKey" : "{{product_description}}"
            } ]
            """;
    String listOfMethodExecutionResult = """
            [ {
              "request" : {
                "className" : "org.example.MyService",
                "methodName" : "getCurrentUserId",
                "methodArguments" : [ ],
                "returnObjectKey" : "{{user_id}}"
              },
              "response" : "User_@12"
            }, {
              "request" : {
                "className" : "org.example.ProductService",
                "methodName" : "getUsersTopProductName",
                "methodArguments" : [ {
                  "type" : "java.lang.String",
                  "value" : "{{user_id}}"
                } ],
                "returnObjectKey" : "{{top_product_name}}"
              },
              "response" : "Samsung galaxy s25 Ultra"
            }, {
              "request" : {
                "className" : "org.example.web.WebSearchProcessor",
                "methodName" : "search",
                "methodArguments" : [ {
                  "type" : "java.lang.String",
                  "value" : "{{top_product_name}}"
                } ],
                "returnObjectKey" : "{{product_description}}"
              },
              "response" : "The Samsung Galaxy S25 Ultra boasts a tough titanium frame and Gorilla® Armor 2 display glass for enhanced durability, with an IP68 rating for water and dust resistance, and integrated Galaxy AI features"
            } ]
            """;

    String response = """
            {
              "description" : "Samsung Galaxy S25 Ultra features a titanium frame, Gorilla Armor 2 display, IP68 rating, and Galaxy AI..",
              "toolsUsed" : [ "getCurrentUserId", "getUsersTopProductName", "search" ],
              "productName" : "Samsung galaxy s25 Ultra"
            }
            """;

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