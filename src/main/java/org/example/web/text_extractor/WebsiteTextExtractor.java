package org.example.web.text_extractor;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;
import org.example.CallApi2;
import org.example.summerize.SimpleSummariser;
import org.example.web.search.GoogleSearchResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WebsiteTextExtractor {

    //.
    /*public static ExtractedPageSummary extractPageHtmlAndSummary(String url) throws IOException {
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
    }*/

    public static ExtractedPageSummary extractReadableTextSummary(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );

            Page page = browser.newPage();
            page.navigate(url);
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);//   or LoadState.LOAD


            // Get the fully rendered HTML
            String content = page.content();

            browser.close();

            // Remove noisy tags before feeding to Readability
            Document doc = Jsoup.parse(content);
            doc.select("script, style, noscript, footer, nav").remove();

            // Run through Readability4J
            Readability4J readability = new Readability4J(url, doc.html());
            Article article = readability.parse();

            //Returning null since new ExtractedPageSummary(url, article.getTitle(), null) is not useful for the llm
            if (article.getContent() == null) return null;;

            // Convert Readability’s cleaned HTML into plain text
            String readableText = Jsoup.parse(article.getContent()).text();
            return new ExtractedPageSummary(url, article.getTitle(), SimpleSummariser.summarise(readableText));
        }
    }

    public static List<ExtractedPageSummary> extractAll(List<String> urls) {
        return urls.parallelStream()
                .map(url -> {
                    return extractReadableTextSummary(url);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<ExtractedPageSummary> extractAllNonNullSearchResponseSummary(List<GoogleSearchResponse> googleSearchResponses) {
        return googleSearchResponses.parallelStream()
                .map(response -> {
                    String url = response.getLink();
                    return extractReadableTextSummary(url);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static void main(String[] args) throws Exception {
        /*String url = "https://en.wikipedia.org/wiki/Artificial_intelligence";

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
        System.out.println(json);*/


        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)); // set to false if you want to see the browser

            Page page = browser.newPage();
            page.navigate("https://www.amazon.com/Brightening-Hyperpigmentation-Treatment-Remover-Armipts/dp/B018RFA3QK");

            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            // Option 2: Extra buffer for async JS (optional)
            page.waitForTimeout(2000);

            // Get the fully rendered page content (after JS runs)
            String content = page.content();

            System.out.println(content);

            Readability4J readability = new Readability4J("https://www.amazon.com/Brightening-Hyperpigmentation-Treatment-Remover-Armipts/dp/B018RFA3QK", content);
            Article article = readability.parse();
            String raw = article.getContent();
            String scrappedText = Jsoup.parse(raw).text();

            browser.close();
        }
//        System.out.println(pageSource);


        String html = Jsoup.connect("https://www.amazon.com/Brightening-Hyperpigmentation-Treatment-Remover-Armipts/dp/B018RFA3QK")
                .userAgent("Mozilla/5.0") // Pretend to be a browser
                .timeout(10000)
                .get()
                .html();
        HttpResponse<String> response = CallApi2.call("https://www.amazon.com/Brightening-Hyperpigmentation-Treatment-Remover-Armipts/dp/B018RFA3QK", null, CallApi2.HttpMethod.GET, Map.of("user-agent", "Mozilla/5.0"));
        System.out.println(html);
        System.out.println(response.body());

//        Readability4J readability = new Readability4J("https://www.amazon.com/Brightening-Hyperpigmentation-Treatment-Remover-Armipts/dp/B018RFA3QK", websiteHtml);
//        Article article = readability.parse();
//
//        System.out.println(article.getTextContent());

    }


}
