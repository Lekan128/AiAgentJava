package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.ai.Agent;
import org.example.ai.Gemini;
import org.example.ai.LLM;
import org.example.method.MethodExecutionResult;
import org.example.method.caller.ReflectionCaller;
import org.example.method.caller.ReflectionInvocableMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Gemini gemini2 = new Gemini();
//        String userQuery = "Dr. Rashel Vitamin C Brightening & Anti-Aging Face Cream";
//        String userQuery = "Mobil 1 oil";
//        String aiPersona = "A product describer, that give description of products to be sold online";

        String userQuery = "I need startups that can hire me as a software engineer with experience using java.";
        String aiPersona = "An expert job finder. You help people from other countries look for remote jobs in USA, UK and UAE.";

        Startups response = useAgent(userQuery, aiPersona, gemini2, Startups.class);
        System.out.println("##########£££££££££££££££££££££££££££");
        System.out.println(ObjectMapperSingleton.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response));


    }


    public static <T> T useAgent(String userQuery, String aiPersona, LLM llm, Class<T> responseClass) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<ReflectionInvocableMethod> invocableMethodList = Agent.callWithToolsForPlan(
                userQuery, llm
        );

        List<MethodExecutionResult> methodExecutionResults = ReflectionCaller.executePipeline(invocableMethodList);
        System.out.println(ObjectMapperSingleton.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(methodExecutionResults));
        System.out.println("###############");

        T response = Agent.callForFinalResponse(aiPersona, userQuery, methodExecutionResults, llm, responseClass);
        return response;
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