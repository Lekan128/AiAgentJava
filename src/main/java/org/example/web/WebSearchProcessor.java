package org.example.web;

import org.example.ObjectMapperSingleton;
import org.example.method.AiToolMethod;
import org.example.method.description.ArgDesc;
import org.example.web.search.Google;
import org.example.web.search.GoogleSearchResponse;
import org.example.web.text_extractor.ExtractedPageSummary;
import org.example.web.text_extractor.WebsiteTextExtractor;

import java.io.IOException;
import java.util.List;

public class WebSearchProcessor {

    @AiToolMethod("Search the web for information")
    public static List<ExtractedPageSummary> search(@ArgDesc("The search parameter") String searchParam) throws IOException, InterruptedException {
        List<GoogleSearchResponse> searchResponses = Google.search(searchParam);
        //Using only the first 2 search results.
        if (searchResponses == null) return null;
        if (searchResponses.size() >2) searchResponses = searchResponses.subList(0, 2);
        List<ExtractedPageSummary> extractedPages = WebsiteTextExtractor.extractAllNonNullSearchResponseSummary(searchResponses);
        return extractedPages;
//        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(extractedPages);
    }

    public static void main(String[] args) {
        try {
//            List<ExtractedPageSummary> search = search("SooPure Lait hydratant moisturising lotion");
            List<ExtractedPageSummary> search = search("Dr. Rashel Vitamin C Brightening & Anti-Aging Face Cream");
            String s = ObjectMapperSingleton.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(search);
            System.out.println(s);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
