package org.example.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.Response;
import org.example.Util;
import org.example.method.MethodExecutionResult;
import org.example.method.caller.ReflectionInvocableMethod;

import java.util.List;

public class Gemini {
    /**
    *
     * @param aiPersonality example = "A product describer, that give description of products to be sold online"
    * */
    public static List<ReflectionInvocableMethod> callWithToolsForPlan(String userQuery, String aiPersonality){
        Dotenv dotenv = Dotenv.load(); // Loads variables from .env in the current directory

        String completePrompt = getCompletePromptForPlan(userQuery, aiPersonality);

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

    private static String getCompletePromptForPlan(String userQuery, String aiPersonality) {
        String toolsJson = AiUtil.getAiToolsAsJson("org.example");
        String outputFormat = null;
        try {
            outputFormat = Util.convertToString(ReflectionInvocableMethod.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


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
        return completePrompt;
    }

    public static Response callForFinalResponse(String aiPersonality, String userQuery, List<MethodExecutionResult> executionResults) throws JsonProcessingException {
        Dotenv dotenv = Dotenv.load(); // Loads variables from .env in the current directory


        GenerateContentResponse generateContentResponse;

        String completePrompt = getPromptForFinalResult(aiPersonality, userQuery, executionResults);


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

    private static String getPromptForFinalResult(String aiPersonality, String userQuery, List<MethodExecutionResult> executionResults) throws JsonProcessingException {
        String toolResultsJson = new ObjectMapper().writeValueAsString(executionResults); // The JSON from your list of ToolExecutionResult
        String finalOutputFormat = Util.convertToString(Response.class); // e.g., {"productName": "...", "description": "..."}
        String chatHistoryJson = "";

        String synthesisPrompt = String.format("""
                [SYSTEM_INSTRUCTIONS]
                You are a %s.
                Your purpose is to synthesize a final answer by analyzing the user's query, the conversation history, and the results from any tools that were called.
                
                [RULES]
                1. Primary Goal: Your main goal is to answer the user's latest query in the `[TASK]` section.
                2. Use All Context: Use the `[CHAT_HISTORY]` to understand the flow of the conversation and the `[TOOL_RESULTS]` for factual data.
                3. Synthesize: Combine all relevant information to create a comprehensive, helpful response.
                4. Handle Missing Info: If the `[CHAT_HISTORY]` or `[TOOL_RESULTS]` are empty, unhelpful, or don't contain enough information, state that you were unable to find the details in the appropriate fields of the `[FINAL_OUTPUT_FORMAT]`, and make the other fields empty. Do not invent information.
                5. Strictly Adhere to Format: Your final output MUST be a single, valid JSON object that conforms to the `[FINAL_OUTPUT_FORMAT]`. Provide no other text.
                
                [CHAT_HISTORY]
                %s
                
                [TOOL_RESULTS]
                %s
                
                [EXAMPLE]
                1. User Query: "Tell me about the Google Pixel 8"
                    Tool Results: [{"request":{"className":"org.example.Search","methodName":"getProductSpecs","methodArguments":[{"type":"java.lang.String","value":"Google Pixel 8"}]},"response":{"cpu":"Tensor G3","screen":"6.2-inch Actua"}}]
                    Your Output:{"productName":"Google Pixel 8","description":"The Google Pixel 8 is powered by the Tensor G3 chip and features a 6.2-inch Actua display.", "toolsUsed" : [ {"methodName":"getProductSpecs"} ]}
                2. User Query: "What is AI"
                    Tool Results: [{"request":{"className":"org.example.google.Search","methodName":"search","methodArguments":[{"type":"java.lang.String","value":"Summary of AI"}]},"response":"AI (Artificial Intelligence) is the development of computer systems capable of performing tasks that typically require human intelligence."}]
                    Your Output:{"summary":"AI is the development of computer systems performing human-like tasks","researchAbout":"AI (Artificial Intelligence)"}
                3. User Query: "SoPure Cream"
                    Tool Results: [{"request":{"className":"org.example.ProductService","methodName":"getProduct","methodArguments":[{"type":"java.lang.String","value":"Mona Lisa"}]},"response":{"name":"Mona Lisa","price":"1200", "type": "replica"}}]
                    Your Output:{"productName":"Unable to find the details.","description":"Unable to find the details.", "toolsUsed" : [ ]}
                 
                [TASK]
                User Query: "<<<%s>>>"
                Final Output Format: %s
                Your Output:
                """, aiPersonality,
                chatHistoryJson, // A JSON representation of the conversation so far
                toolResultsJson, // The JSON from your ToolExecutionResult
                userQuery, // The user's most recent message
                finalOutputFormat
        );

        return synthesisPrompt;

    }
}
