package org.example.web.text_extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;
import org.example.summerize.SimpleSummariser;
import org.example.web.search.GoogleSearchResponse;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class WebsiteTextExtractor {

    public static class ExtractedPageSummary {
        private final String url;
        private final String title;
        private final String content;

        public static ExtractedPageSummary newInstance(String url, String title, String content){
            return new ExtractedPageSummary(url, title, content);
        }

        public ExtractedPageSummary(String url, String title, String content) {
            this.url = url;
            this.title = title;
            this.content = content;
        }

        public String getUrl() { return url; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
    }

    //.
    public static ExtractedPageSummary extractPageHtmlAndSummary(String url) throws IOException {
        // 1. Fetch HTML
        String html = Jsoup.connect(url)
                .userAgent("Mozilla/5.0") // Pretend to be a browser
                .timeout(10000)
                .get()
                .html();

        // 2. Parse with Readability4J
        Readability4J readability = new Readability4J(url, html);
        Article article = readability.parse();

        // 3. Return cleaned content
        return new ExtractedPageSummary(url, article.getTitle(), SimpleSummariser.summarise(article.getTextContent()));
    }

    public static List<ExtractedPageSummary> extractAll(List<String> urls) {
        return urls.parallelStream()
                .map(url -> {
                    try {
                        return extractPageHtmlAndSummary(url);
                    } catch (IOException e) {
                        System.err.println("Failed to extract from " + url + ": " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ExtractedPageSummary> extractAllFromSearchResponse(List<GoogleSearchResponse> googleSearchResponses) {
        return googleSearchResponses.parallelStream()
                .map(response -> {
                    String url = response.getLink();
                    try {
                        return extractPageHtmlAndSummary(url);
                    } catch (IOException e) {
                        System.err.println("Failed to extract from " + url + ": " + e.getMessage());
                        return new ExtractedPageSummary(url, response.getTitle(), response.getLink());
                    }
                })
                .toList();
    }

    public static void main(String[] args) throws Exception {
        String url = "https://en.wikipedia.org/wiki/Artificial_intelligence";

        ExtractedPageSummary page = extractPageHtmlAndSummary(url);

        System.out.println("Title: " + page.getTitle());
        System.out.println("\nurl:\n" + page.getUrl());
        System.out.println("\nContent:\n" + page.getContent());

        //..............

        List<String> urls = List.of(
                "https://en.wikipedia.org/wiki/Artificial_intelligence",
                "https://example.com"
        );

        List<ExtractedPageSummary> pages = extractAll(urls);

        ObjectMapper mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        String json = mapper.writeValueAsString(pages);
        System.out.println(json);
    }

}
