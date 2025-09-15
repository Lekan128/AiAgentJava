package org.example.web.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CallApi2;
import org.example.method.AiToolMethod;
import org.example.method.description.ArgDesc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.http.HttpResponse;
import java.util.Map;

public class DuckDuckGo {

    public static String search(String searchParam)
            throws IOException, InterruptedException
    {
        String url = String.format("https://api.duckduckgo.com/?q=%s&format=json&pretty=1&no_html=1&skip_disambig=1", searchParam);
        Map<String, String> queryParams = Map.of(
                "q", searchParam,
                "format", "json",
                "pretty", "1",
                "no_html", "1",
                "skip_disambig", "1"
        );
        HttpResponse<String> response1 = CallApi2.call(url, null, CallApi2.HttpMethod.GET, queryParams, "user-agent", InetAddress.getLocalHost().getHostAddress());
        ObjectMapper objectMapper = new ObjectMapper();
        DuckDuckGoResponse duckDuckGoResponse = objectMapper.readValue(response1.body(), DuckDuckGoResponse.class);
        return objectMapper.writeValueAsString(duckDuckGoResponse);
    }
}
