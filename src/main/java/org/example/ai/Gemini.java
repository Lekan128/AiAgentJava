package org.example.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.Response;
import org.example.Util;
import org.example.method.caller.ReflectionInvocableMethod;

import java.lang.reflect.Type;
import java.util.List;

public class Gemini {
    public static Response call(String prompt, Type responseClass){
        Dotenv dotenv = Dotenv.load(); // Loads variables from .env in the current directory

        GenerateContentResponse generateContentResponse;

        try (Client client = Client.builder().apiKey(dotenv.get("GEMINI_API_KEY")).build()) {

            generateContentResponse = client.models.generateContent(
                    "gemini-2.5-flash",
                    "Explain how AI as a product works. Your generateContentResponse should only be a json in the format" + Util.convertToString(responseClass),
                    null);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error getting to gemini\n" + e);
        }


        Response response;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = objectMapper.readValue(generateContentResponse.text().replace("```json", "").replace("```", ""), Response.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert Gemini generateContentResponse to POJO\n"+e);
        }
        return response;
    }




    /*
    * query = <<<>>>
    *  You are a product researcher. You give descriptions on products to be put online for sale. -aiPersonality
    * You are to come up with a plan to answer the user's query using the tools you are provided.
    * Tools: (tools)
    * The query is delimited by <<< and >>>
    * Wrap the output in the provided format and provide no other text.
    * Output format: (format)
    *
    *
    *   Tools:
    *       com.google.g
    *
    *
    * */



    /*
    *
    * Give the AI a prompt, it should come up with a plan
    * Take the plan, execute it, send the result back to the ai to give result
    *
    *
    *
    * [..//.gemini.call(params...)]
    * className, methodName, [String.class, int.class]
    *
    *
    * give me list of steps to carry out the query.
    * Each step should be the string method call form of a method call
    *
    * */


    /*
     * query = <<<>>>
     *  You are a product researcher. You give descriptions on products to be put online for sale. -aiPersonality
     * Answer the user's query and use necessary tools.
     * The query is delimited by <<< and >>>
     * Wrap the output in the provided format and provide no other text.
     * Output format: (format)
     *
     * */

    /**
    * Response response =
     *  Gemini.call("Samsung S23",
     *  "You are a phone researcher that provide information on phones to know if they are a good buy or not",
     *  Response.class);
     *         System.out.println(response);
     * @param aiPersonality Should tell what the ai is.
    * */
    public static Response call(String prompt, String aiPersonality, Type responseClass){
        Dotenv dotenv = Dotenv.load(); // Loads variables from .env in the current directory


        GenerateContentResponse generateContentResponse;

        String completePrompt;
        try {
            completePrompt = String.format("""
                    query = <<<%s>>>
                    %s
                    Answer the user's query and use necessary tools.
                    The query is delimited by <<< and >>>
                    Wrap the output in the provided format and provide no other text.
                    Output format: %s
                    """, prompt, aiPersonality, Util.convertToString(responseClass));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        try (Client client = Client.builder().apiKey(dotenv.get("GEMINI_API_KEY")).build()) {
            generateContentResponse = client.models.generateContent(
                    "gemini-2.5-flash",
                    completePrompt,
                    null);
        }


        Response response;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = objectMapper.readValue(generateContentResponse.text().replace("```json", "").replace("```", ""), Response.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert Gemini generateContentResponse to POJO\n"+e);
        }
        return response;
    }

    /**
    *
     * @param userQuery example = "A product describer, that give description of products to be sold online"
    * */
    public static List<ReflectionInvocableMethod> callWithToolsForPlan(String userQuery, String aiPersonality){
        Dotenv dotenv = Dotenv.load(); // Loads variables from .env in the current directory

        String completePrompt = getCompletePrompt(userQuery, aiPersonality);

        GenerateContentResponse generateContentResponse;

        try (Client client = Client.builder().apiKey(dotenv.get("GEMINI_API_KEY")).build()) {
            generateContentResponse = client.models.generateContent(
                    "gemini-2.5-flash",
                    completePrompt,
                    null);
        }

        List<ReflectionInvocableMethod> response;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            objectMapper.setVisibility(
                    com.fasterxml.jackson.annotation.PropertyAccessor.FIELD,
                    com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
            );
            response = objectMapper.readValue(generateContentResponse.text().replace("```json", "").replace("```", ""), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert Gemini generateContentResponse to POJO\n"+e);
        }
        return response;
    }

    private static String getCompletePrompt(String userQuery, String aiPersonality) {
        String toolsJson = AiUtil.getAiToolsAsJson("org.example");
        String outputFormat = null;
        try {
            outputFormat = Util.convertToString(ReflectionInvocableMethod.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//        GenerateContentResponse generateContentResponse;

        String completePrompt = String.format("""
                [SYSTEM INSTRUCTIONS]
                You are an expert AI assistant that functions as a tool-use planner helping a "%s".
                Your sole purpose is to analyze a user's query and generate a JSON plan of tool calls required to fulfill it.
                            
                [RULES]
                1. Analyze the Query: Carefully examine the user's query to understand their intent.
                2. Select Tools: From the list of available tools, choose the most appropriate tool(s) to call.
                3. Generate Arguments: For each tool call, determine the most effective arguments based on the user's query. Do NOT use the entire query as an argument unless it is the most logical. Extract the key entities.
                4. Format Output: Your output MUST be a valid JSON array of method calls.
                5. Empty Plan: If no tools are required to answer the query, you MUST return an empty array `[]`.
                6. No Extra Text: Do not provide any explanation or text outside of the final JSON array.
                            
                [TOOLS AVAILABLE]
                %s
                            
                [EXAMPLE]
                Example tools: [
                    {"description" : "Search the web for information","className" : "org.example.web.search.DuckDuckGo","methodName" : "search","methodArguments" : [ {"name" : "arg0","description" : "The search parameter","type" : "java.lang.String","required" : true,"fields" : { }} ],"returnType" : "java.lang.String"},
                    {"description":"Finds products based on a structured filter.","className":"org.example.ProductService","methodName":"findProducts","methodArguments":[{"name":"arg0","description":null,"type":"org.example.ProductService$SearchFilter","required":true,"fields":{"fields":{"searchWord":{"type":"java.lang.String","required":true},"maxPrice":{"type":"java.lang.Double","required":false},"inStock":{"type":"boolean","required":true}}}}],"returnType":"java.lang.String"}
                    ]
                1. User Query: "Can you find me some information on the new Apple M4 chip?"
                 Your Output: [{"className":"org.example.web.search.DuckDuckGo","methodName":"search","methodArguments":[{"type":"java.lang.String","value":"Apple M4 chip specifications"}]}]
                2. User Query: "I need to find a Nivea brand anti-perspirant for under 15 dollars. Only show me stuff that's in stock."
                 Your Output:[{"className": "org.example.ProductService","methodName": "findProducts","methodArguments": [{"type": "org.example.ProductService$SearchFilter","value": {"searchWord": "anti-perspirant","maxPrice": "15.0","inStockOnly": "true"}}]}]
                            
                            
                [TASK]
                User Query: "<<<%s>>>"
                Your Output: [%s]
                """, aiPersonality, toolsJson, userQuery, outputFormat);
        System.out.println(completePrompt);
        return completePrompt;
    }

    public static Response callForFinalResponse(String prompt, String aiPersonality, Type responseClass){
        Dotenv dotenv = Dotenv.load(); // Loads variables from .env in the current directory


        GenerateContentResponse generateContentResponse;

        String completePrompt;
        try {
            completePrompt = String.format("""
                    [SYSTEM INSTRUCTIONS]
                    You are an expert AI assistant that functions as a tool-use planner helping a "%s".
                    Your sole purpose is to analyze a user's query and generate a JSON plan of tool calls required to fulfill it.
                    
                    """, prompt, aiPersonality, Util.convertToString(responseClass));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        try (Client client = Client.builder().apiKey(dotenv.get("GEMINI_API_KEY")).build()) {
            generateContentResponse = client.models.generateContent(
                    "gemini-2.5-flash",
                    completePrompt,
                    null);
        }


        Response response;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = objectMapper.readValue(generateContentResponse.text().replace("```json", "").replace("```", ""), Response.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert Gemini generateContentResponse to POJO\n"+e);
        }
        return response;
    }


    public static void main(String[] args) throws JsonProcessingException {
        String aiPersonality = "A product describer, that give description of products to be sold online";


        String completePrompt = String.format("""
                [SYSTEM INSTRUCTIONS]
                You are "%s".
                Your sole purpose is to analyze a user's query and use the information provided to generate a JSON in the output format provided for you.
                            
                [RULES]
                1. Analyze the Query: Carefully examine the user's query to understand their intent.
                2. Use Reply/Given objects from prev chat: Use the reply/given objects to give the most appropriate response to the user's query.
                3. Format Output: Your output MUST be a valid JSON in the form of the output format provided.
                4. Unhelpful Reply/Given objects from prev chat: If the reply/given objects are unhelpful, give the json formatted response in the best of you ability.
                5. No Extra Text: Do not provide any explanation or text outside of the final JSON array.
                            
                [REPLY/GIVEN OBJECTS FROM PREV CHAT]
                %s
                            
                [EXAMPLE]
                //... depends on the reply I've not decided on how this should look           
                            
                [TASK]
                User Query: "<<<%s>>>"
                Your Output: [%s]
                """, aiPersonality, userQuery, outputFormat);
    }

    public static void llm() throws JsonProcessingException {
        // Your original variables
        String userQuery = "Nivea Men dry impact anti-perspirant";
        String toolsJson = AiUtil.getAiToolsAsJson("org.example");
        String outputFormat = Util.convertToString(ReflectionInvocableMethod.class);
        String aIPersona = "A product describer, that give description of products to be sold online";
        // Assuming this produces the JSON structure

        // The new, improved prompt template
        String completePrompt = String.format("""
                [SYSTEM INSTRUCTIONS]
                You are an expert AI assistant that functions as a tool-use planner helping a "%s".
                Your sole purpose is to analyze a user's query and generate a JSON plan of tool calls required to fulfill it.
                            
                [RULES]
                1. Analyze the Query: Carefully examine the user's query to understand their intent.
                2. Select Tools: From the list of available tools, choose the most appropriate tool(s) to call.
                3. Generate Arguments: For each tool call, determine the most effective arguments based on the user's query. Do NOT use the entire query as an argument unless it is the most logical. Extract the key entities.
                4. Format Output: Your output MUST be a valid JSON array of method calls.
                5. Empty Plan: If no tools are required to answer the query, you MUST return an empty array `[]`.
                6. No Extra Text: Do not provide any explanation or text outside of the final JSON array.
                            
                [TOOLS AVAILABLE]
                %s
                            
                [EXAMPLE]
                Example tools: [
                    {"description" : "Search the web for information","className" : "org.example.web.search.DuckDuckGo","methodName" : "search","methodArguments" : [ {"name" : "arg0","description" : "The search parameter","type" : "java.lang.String","required" : true,"fields" : { }} ],"returnType" : "java.lang.String"},
                    {"description":"Finds products based on a structured filter.","className":"org.example.ProductService","methodName":"findProducts","methodArguments":[{"name":"arg0","description":null,"type":"org.example.ProductService$SearchFilter","required":true,"fields":{"fields":{"searchWord":{"type":"java.lang.String","required":true},"maxPrice":{"type":"java.lang.Double","required":false},"inStock":{"type":"boolean","required":true}}}}],"returnType":"java.lang.String"}
                    ]
                1. User Query: "Can you find me some information on the new Apple M4 chip?"
                 Your Output: [{"className":"org.example.web.search.DuckDuckGo","methodName":"search","methodArguments":[{"type":"java.lang.String","value":"Apple M4 chip specifications"}]}]
                2. User Query: "I need to find a Nivea brand anti-perspirant for under 15 dollars. Only show me stuff that's in stock."
                 Your Output:[{"className": "org.example.ProductService","methodName": "findProducts","methodArguments": [{"type": "org.example.ProductService$SearchFilter","value": {"searchWord": "anti-perspirant","maxPrice": "15.0","inStockOnly": "true"}}]}]
                            
                            
                [TASK]
                User Query: "<<<%s>>>"
                Your Output: %s
                """, aIPersona, toolsJson, userQuery, outputFormat);
        System.out.println(completePrompt);

    }
}
