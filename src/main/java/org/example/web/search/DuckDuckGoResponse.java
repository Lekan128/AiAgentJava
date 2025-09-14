package org.example.web.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DuckDuckGoResponse {
    @JsonProperty("AbstractText")
    String abstractText;
    @JsonProperty("AbstractSource")
    String abstractSource;
    @JsonProperty("AbstractURL")
    String abstractURL;

    @JsonProperty("RelatedTopics")
    List<RelatedTopic> relatedTopics;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RelatedTopic {
        @JsonProperty("Text")
        String text;
    }
}
