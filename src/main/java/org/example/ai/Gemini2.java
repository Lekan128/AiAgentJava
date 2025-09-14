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

public class Gemini2 {
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

        GenerateContentResponse generateContentResponse;

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
                """, aiPersonality, toolsJson, userQuery, outputFormat);
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
        String userQuery = "Samsung galaxy A53";
        String outputFormat = Util.convertToString(Response.class);

        String incompletePrompt = String.format("""
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
                //... undecided
                            
                [EXAMPLE]
                //... depends REPLY/GIVEN OBJECTS FROM PREV CHAT         
                            
                [TASK]
                User Query: "<<<%s>>>"
                Your Output: [%s]
                """, aiPersonality, userQuery, outputFormat);
        System.out.println(incompletePrompt);
    }

    public static void llm() throws JsonProcessingException {
        // Assuming these are your variables
        String aiPersonality = "You are a product describer, you give description of products to be sold online";
        String originalUserQuery = "Samsung galaxy A53";
        String toolResultsJson = Util.convertToString(executionResults); // The JSON from your list of ToolExecutionResult
        String finalOutputFormat = Util.convertToString(Response.class); // e.g., {"productName": "...", "description": "..."}

        String synthesisPrompt = String.format("""
                [SYSTEM_INSTRUCTIONS]
                You are a %s.
                Your purpose is to synthesize a final answer to the user's original query by analyzing the results from the tools you previously decided to call.
                
                [RULES]
                1.  **Understand the Goal**: Your main goal is to answer the user's `[ORIGINAL_QUERY]` completely and accurately.
                2.  **Analyze Tool Results**: Carefully review the information provided in the `[TOOL_RESULTS]` section. Each item shows the tool you requested and the data that was returned.
                3.  **Synthesize the Answer**: Combine the relevant information from the tool results to create a comprehensive product description.
                4.  **Handle Missing Info**: If the tool results are empty, unhelpful, or don't contain enough information, state that you were unable to find the details. Do not invent information.
                5.  **Strictly Adhere to Format**: Your final output MUST be a single, valid JSON object that conforms to the `[FINAL_OUTPUT_FORMAT]`. Provide no other text or explanation.
                
                [TOOL_RESULTS]
                %s
                
                [EXAMPLE]
                User Query: "Tell me about the Google Pixel 8"
                Tool Results: [{"toolRequest":{"className":"org.example.Search","methodName":"getProductSpecs","methodArguments":[{"type":"java.lang.String","value":"Google Pixel 8"}]},"toolResponse":{"cpu":"Tensor G3","screen":"6.2-inch Actua"}}]
                Your Output:
                {"productName":"Google Pixel 8","description":"The Google Pixel 8 is powered by the Tensor G3 chip and features a 6.2-inch Actua display."}
                
                [TASK]
                Original User Query: "<<<%s>>>"
                Final Output Format: %s
                Your Output:
                """, aiPersonality, toolResultsJson, originalUserQuery, finalOutputFormat);
        System.out.println(synthesisPrompt);

        String synthesisPrompt = String.format("""
                [SYSTEM_INSTRUCTIONS]
                You are a %s.
                Your purpose is to synthesize a final answer by analyzing the user's query, the conversation history, and the results from any tools that were called.
                
                [RULES]
                1.  **Primary Goal**: Your main goal is to answer the user's latest query in the `[TASK]` section.
                2.  **Use All Context**: Use the `[CHAT_HISTORY]` to understand the flow of the conversation and the `[TOOL_RESULTS]` for factual data.
                3.  **Synthesize**: Combine all relevant information to create a comprehensive, helpful response.
                4.  **Handle Missing Info**: If the `[CHAT_HISTORY]` or `[TOOL_RESULTS]` are empty, unhelpful, or don't contain enough information, state that you were unable to find the details in the appropriate fields of the `[FINAL_OUTPUT_FORMAT]`, and make the other fields empty. Do not invent information.
                5.  **Strictly Adhere to Format**: Your final output MUST be a single, valid JSON object that conforms to the `[FINAL_OUTPUT_FORMAT]`. Provide no other text.
                
                [CHAT_HISTORY]
                %s
                
                [TOOL_RESULTS]
                %s
                
                [EXAMPLE]
                User Query: "Tell me about the Google Pixel 8"
                Tool Results: [{"toolRequest":{"className":"org.example.Search","methodName":"getProductSpecs","methodArguments":[{"type":"java.lang.String","value":"Google Pixel 8"}]},"toolResponse":{"cpu":"Tensor G3","screen":"6.2-inch Actua"}}]
                Your Output:
                {"productName":"Google Pixel 8","description":"The Google Pixel 8 is powered by the Tensor G3 chip and features a 6.2-inch Actua display."}
                
                
                [TASK]
                User Query: "<<<%s>>>"
                Final Output Format: %s
                Your Output:
                """, aiPersonality,
                chatHistoryJson, // A JSON representation of the conversation so far
                toolResultsJson, // The JSON from your ToolExecutionResult
                currentUserQuery, // The user's most recent message
                finalOutputFormat
        );

    }
}
