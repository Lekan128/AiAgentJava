package org.example.web.text_extractor;

public record ExtractedPageSummary(String url, String title, String content) {
    public static ExtractedPageSummary newInstance(String url, String title, String content) {
        return new ExtractedPageSummary(url, title, content);
    }

}
