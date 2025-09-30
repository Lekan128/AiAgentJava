package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ObjectMapperSingleton;
import org.example.method.AiToolMethod;
import org.example.method.description.ArgDesc;
import org.example.web.search.Google;
import org.example.web.search.GoogleSearchResponse;
import org.example.web.text_extractor.WebsiteTextExtractor;

import java.io.IOException;
import java.util.List;

public class WebSearchProcessor {

    @AiToolMethod("Search the web for information")
    public static List<WebsiteTextExtractor.ExtractedPageSummary> search(@ArgDesc("The search parameter") String searchParam) throws IOException, InterruptedException {
        List<GoogleSearchResponse> searchResponses = Google.search(searchParam);
        //Using only the first 2 search results.
        if (searchResponses.size() >2) searchResponses = searchResponses.subList(0, 2);
        List<WebsiteTextExtractor.ExtractedPageSummary> extractedPages = WebsiteTextExtractor.extractAllNonNullSearchResponseSummary(searchResponses);
        return extractedPages;
//        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(extractedPages);
    }

    public static void main(String[] args) {
        try {
            List<WebsiteTextExtractor.ExtractedPageSummary> search = search("Dr. Rashel Vitamin C Brightening & Anti-Aging Face Cream");
            String s = ObjectMapperSingleton.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(search);
            System.out.println(s);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
