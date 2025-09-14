package org.example.web.search;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.CallApi2;
import org.example.method.AiToolMethod;
import org.example.method.description.ArgDesc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Google {
    //cx= Search Engine ID = 06c2c51a6ad804dfa
    //apiKey = AIzaSyAJ5kV5HFQV3fuNqlhP-a7f4p4PzRpN6rg

    public static List<GoogleSearchResponse> search(String searchParam)
            throws IOException, InterruptedException
    {
        Dotenv dotenv = Dotenv.load();
        String url = "https://www.googleapis.com/customsearch/v1";

        //Todo: take away
        String apiKey =  dotenv.get("SEARCH_API_KEY");
        String searchEngineId = dotenv.get("SEARCH_ENGINE_ID");

        Map<String, String> queryParams = Map.of(
                "q", searchParam,
                "key", apiKey,
                "cx", searchEngineId
        );
        HttpResponse<String> response1 = CallApi2.call(url, null, CallApi2.HttpMethod.GET, queryParams);
//        if (response1.statusCode() >199 && response1.statusCode() <301){
//
//        }
        String googleJsonApiResponse = response1.body();

        return getSearchResult(googleJsonApiResponse);
    }

    private static List<GoogleSearchResponse> getSearchResult(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Parse the JSON string into a JsonNode
        JsonNode fullSchema = mapper.readTree(jsonString);
        JsonNode jsonNodeItems = fullSchema.get("items");
        if (jsonNodeItems == null) return null;
        List<GoogleSearchResponse> googleSearchResponses = mapper.readValue(jsonNodeItems.traverse(), new TypeReference<>(){});
        Set<String> links = googleSearchResponses.stream().map(GoogleSearchResponse::getLink).collect(Collectors.toSet());
        return googleSearchResponses;
    }

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
//        search("Samsung S23");
       /* HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://www.amazon.com/SAMSUNG-Galaxy-S23-Factory-Unlocked/dp/B0C5B736X3"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        System.out.println(body);*/

        getSearchResult(s);

    }

    static String s =
            """
                            {
                              "kind": "customsearch#search",
                              "url": {
                                "type": "application/json",
                                "template": "https://www.googleapis.com/customsearch/v1?q={searchTerms}&num={count?}&start={startIndex?}&lr={language?}&safe={safe?}&cx={cx?}&sort={sort?}&filter={filter?}&gl={gl?}&cr={cr?}&googlehost={googleHost?}&c2coff={disableCnTwTranslation?}&hq={hq?}&hl={hl?}&siteSearch={siteSearch?}&siteSearchFilter={siteSearchFilter?}&exactTerms={exactTerms?}&excludeTerms={excludeTerms?}&linkSite={linkSite?}&orTerms={orTerms?}&dateRestrict={dateRestrict?}&lowRange={lowRange?}&highRange={highRange?}&searchType={searchType}&fileType={fileType?}&rights={rights?}&imgSize={imgSize?}&imgType={imgType?}&imgColorType={imgColorType?}&imgDominantColor={imgDominantColor?}&alt=json"
                              },
                              "queries": {
                                "request": [
                                  {
                                    "title": "Google Custom Search - Samsung S23",
                                    "totalResults": "423000",
                                    "searchTerms": "Samsung S23",
                                    "count": 10,
                                    "startIndex": 1,
                                    "inputEncoding": "utf8",
                                    "outputEncoding": "utf8",
                                    "safe": "off",
                                    "cx": "06c2c51a6ad804dfa"
                                  }
                                ],
                                "nextPage": [
                                  {
                                    "title": "Google Custom Search - Samsung S23",
                                    "totalResults": "423000",
                                    "searchTerms": "Samsung S23",
                                    "count": 10,
                                    "startIndex": 11,
                                    "inputEncoding": "utf8",
                                    "outputEncoding": "utf8",
                                    "safe": "off",
                                    "cx": "06c2c51a6ad804dfa"
                                  }
                                ]
                              },
                              "context": {
                                "title": "Test Search Engine"
                              },
                              "searchInformation": {
                                "searchTime": 0.337391,
                                "formattedSearchTime": "0.34",
                                "totalResults": "423000",
                                "formattedTotalResults": "423,000"
                              },
                              "items": [
                                {
                                  "kind": "customsearch#result",
                                  "title": "Samsung Galaxy S23 5G Factory Unlocked 128GB ... - Amazon.com",
                                  "htmlTitle": "Samsung Galaxy S23 5G Factory Unlocked 128GB ... - Amazon.com",
                                  "link": "https://www.amazon.com/SAMSUNG-Galaxy-S23-Factory-Unlocked/dp/B0C5B736X3",
                                  "displayLink": "www.amazon.com",
                                  "snippet": "Top highlights · Brand. Samsung · Operating System. Android · Ram Memory Installed Size. 8 GB · CPU Model. Snapdragon · CPU Speed. 3.2 GHz · Memory Storage ...",
                                  "htmlSnippet": "Top highlights &middot; Brand. \\u003cb\\u003eSamsung\\u003c/b\\u003e &middot; Operating System. Android &middot; Ram Memory Installed Size. 8 GB &middot; CPU Model. Snapdragon &middot; CPU Speed. 3.2 GHz &middot; Memory Storage&nbsp;...",
                                  "formattedUrl": "https://www.amazon.com/SAMSUNG-Galaxy-S23-Factory.../B0C5B736X3",
                                  "htmlFormattedUrl": "https://www.amazon.com/\\u003cb\\u003eSAMSUNG\\u003c/b\\u003e-Galaxy-\\u003cb\\u003eS23\\u003c/b\\u003e-Factory.../B0C5B736X3",
                                  "pagemap": {
                                    "cse_thumbnail": [
                                      {
                                        "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTIapPAVblrxXg22cEJoM2AvguGozQVswjp-Wjt9zaXfDD0DXTyBPutgdod&s",
                                        "width": "310",
                                        "height": "162"
                                      }
                                    ],
                                    "metatags": [
                                      {
                                        "og:image": "https://m.media-amazon.com/images/I/51ngAkKqflL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg",
                                        "theme-color": "#131921",
                                        "og:type": "product",
                                        "og:image:width": "1910",
                                        "og:title": "Samsung Galaxy S23 5G Factory Unlocked 128GB - Phantom Black (Renewed)",
                                        "og:image:height": "1000",
                                        "flow-closure-id": "1757790328",
                                        "title": "Amazon.com: Samsung Galaxy S23 5G Factory Unlocked 128GB - Phantom Black (Renewed) : Cell Phones & Accessories",
                                        "og:description": "PRODUCT OVERVIEWMeet Galaxy S23, the phone takes you out of the everyday and into the epic. Life doesn’t wait for the perfect lighting, but with Nightography, you are always ready to seize the moment and snap memories like a pro. See your content no matter the time of day on a display with a refr...",
                                        "encrypted-slate-token": "AnYxpJK5MrbMpVdCbgCu5lU/Ss7zTuobfeTI6uuz0vxHr0eO/SX6Z2OwijcTjrH4xlPdckZOXDKYD74FwAnxU3UhQn4h2VN/a0ttmUiEt+6yOg2cm0k7oznIDM15SpMA0zLWOJerx3xWJHXIo4Ffn54xTVZTGyrAKFWzVBVFxdkM28bUih7S7WDFkBAA7vi3w5k2hFWHScU1gdFGSWhrPs7+6Hp+6b6NhKXsalObHHqZrsox3YfFqT/fcMcXF/B4/KHPlC6sMnPuACgb8ZVC5zes6wucMtrRR9R73nhn6DNW4Qc=",
                                        "viewport": "width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no",
                                        "og:sitename": "Amazon.com",
                                        "og:url": "https://www.amazon.com/dp/B0C5B736X3"
                                      }
                                    ],
                                    "cse_image": [
                                      {
                                        "src": "https://m.media-amazon.com/images/I/51ngAkKqflL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg"
                                      }
                                    ]
                                  }
                                },
                                {
                                  "kind": "customsearch#result",
                                  "title": "SAMSUNG Galaxy S23 5G (128GB, 8GB) 6.1 ... - Amazon.com",
                                  "htmlTitle": "SAMSUNG Galaxy S23 5G (128GB, 8GB) 6.1 ... - Amazon.com",
                                  "link": "https://www.amazon.com/SAMSUNG-International-Unlocked-Verizon-T-Mobile/dp/B0BQ4YCB92",
                                  "displayLink": "www.amazon.com",
                                  "snippet": "Top highlights · Brand. Samsung · Operating System. Android 13.0 · Ram Memory Installed Size. 8 GB · CPU Model. Qualcomm Snapdragon MSM8260A · CPU Speed. 3.2E+3 ...",
                                  "htmlSnippet": "Top highlights &middot; Brand. \\u003cb\\u003eSamsung\\u003c/b\\u003e &middot; Operating System. Android 13.0 &middot; Ram Memory Installed Size. 8 GB &middot; CPU Model. Qualcomm Snapdragon MSM8260A &middot; CPU Speed. 3.2E+3&nbsp;...",
                                  "formattedUrl": "https://www.amazon.com/SAMSUNG-International...T.../B0BQ4YCB92",
                                  "htmlFormattedUrl": "https://www.amazon.com/\\u003cb\\u003eSAMSUNG\\u003c/b\\u003e-International...T.../B0BQ4YCB92",
                                  "pagemap": {
                                    "cse_thumbnail": [
                                      {
                                        "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQukewuNeyW8K1zSFNg7kq1UguoPr9zwblN87pM2D60Itwsc_7xrpcwF84&s",
                                        "width": "310",
                                        "height": "162"
                                      }
                                    ],
                                    "metatags": [
                                      {
                                        "og:image": "https://m.media-amazon.com/images/I/61ORjHcVaQL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg",
                                        "theme-color": "#131921",
                                        "og:type": "product",
                                        "og:image:width": "1910",
                                        "og:title": "SAMSUNG Galaxy S23 5G (128GB, 8GB) 6.1\\" AMOLED, 50MP 8K Camera, Global Volte (International Model Fully Unlocked for AT&T, Verizon, T-Mobile, Global 5G) S911W (w/ 25W Fast Charger, Phantom Black)",
                                        "og:image:height": "1000",
                                        "flow-closure-id": "1757790328",
                                        "title": "Amazon.com: SAMSUNG Galaxy S23 5G (128GB, 8GB) 6.1\\" AMOLED, 50MP 8K Camera, Global Volte (International Model Fully Unlocked for AT&T, Verizon, T-Mobile, Global 5G) S911W (w/ 25W Fast Charger, Phantom Black) : Cell Phones & Accessories",
                                        "og:description": "Galaxy S23 UItra is more than the next big step in mobile tech. With the highest camera resolution on a Galaxy smartphone and stunning Night Mode powered by Nightography, you can share those big moments no matter the lighting. Plus, with the fastest Snapdragon processor yet, juggle high-intensity...",
                                        "encrypted-slate-token": "AnYxSWE61M/fv4tKqjQgXvTD8eCj9SWZ8aPNkw6b/cB+WTm7TXpxDFOgncGC8ynNWWWKzazyRD7Ko1tRcejnEzi8GlFL5bsubFUG0FUVUOQZ3gp+1fzk97U6LDkmS/+ZPM9WevtxnJ2MkMwIOubeilhtSApQBi7D976xWKBHAefMCxCeo9LNkp+FMDijcEWp4/+STJq/abm+8n8tVF6qNcJ1rYwBMYMQscFCMMihrJehVsigT5T8nKkgnPL27OYB55ZCMwxQLqv4InkYutE66e+/LVbHlmb1+mzQmcO8eFJDzfk=",
                                        "viewport": "width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no",
                                        "og:sitename": "Amazon.com",
                                        "og:url": "https://www.amazon.com/dp/B0BQ4YCB92"
                                      }
                                    ],
                                    "cse_image": [
                                      {
                                        "src": "https://m.media-amazon.com/images/I/61ORjHcVaQL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg"
                                      }
                                    ]
                                  }
                                },
                                {
                                  "kind": "customsearch#result",
                                  "title": "SAMSUNG Galaxy S23 Ultra 5G, US Version, 256GB ... - Amazon.com",
                                  "htmlTitle": "SAMSUNG Galaxy S23 Ultra 5G, US Version, 256GB ... - Amazon.com",
                                  "link": "https://www.amazon.com/SAMSUNG-Galaxy-S23-Ultra-Unlocked/dp/B0C5443MCX",
                                  "displayLink": "www.amazon.com",
                                  "snippet": "Top highlights · Brand. SAMSUNG · Operating System. Android · Ram Memory Installed Size. 8 GB · CPU Model. Snapdragon · CPU Speed. 8 GHz · Memory Storage ...",
                                  "htmlSnippet": "Top highlights &middot; Brand. \\u003cb\\u003eSAMSUNG\\u003c/b\\u003e &middot; Operating System. Android &middot; Ram Memory Installed Size. 8 GB &middot; CPU Model. Snapdragon &middot; CPU Speed. 8 GHz &middot; Memory Storage&nbsp;...",
                                  "formattedUrl": "https://www.amazon.com/SAMSUNG-Galaxy-S23-Ultra.../B0C5443MCX",
                                  "htmlFormattedUrl": "https://www.amazon.com/\\u003cb\\u003eSAMSUNG\\u003c/b\\u003e-Galaxy-\\u003cb\\u003eS23\\u003c/b\\u003e-Ultra.../B0C5443MCX",
                                  "pagemap": {
                                    "cse_thumbnail": [
                                      {
                                        "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRg7jR5IfHJ2Ac9v8cRGhrybsFu2yogsUhBHp7-Nav1hP0QpQniv2LlQco&s",
                                        "width": "310",
                                        "height": "162"
                                      }
                                    ],
                                    "metatags": [
                                      {
                                        "og:image": "https://m.media-amazon.com/images/I/51MpKU9XowL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg",
                                        "theme-color": "#131921",
                                        "og:type": "product",
                                        "og:image:width": "1910",
                                        "og:title": "SAMSUNG Galaxy S23 Ultra 5G, US Version, 256GB, Green - Unlocked (Renewed)",
                                        "og:image:height": "1000",
                                        "flow-closure-id": "1757790328",
                                        "title": "Amazon.com: SAMSUNG Galaxy S23 Ultra 5G, US Version, 256GB, Green - Unlocked (Renewed) : Cell Phones & Accessories",
                                        "og:description": "PRODUCT OVERVIEWThe Samsung Galaxy S23 Ultra pushes the smartphone boundaries with an epic 200MP camera to capture every moment day or night with advanced Nightography. Write, draw, create with the built in S-pen. The S23 Ultra boasts powerful processing performance, automatic adaptive 6.8” displ...",
                                        "encrypted-slate-token": "AnYxexYXfIL2uaLrIljHducGepLTt3Y9nfwmH9umvtuQaGDI9oLBCb9vTiaG6s7Y/cxMCdglJoHIeAixZ4tnXhNL6YClnoPkwh4B6UTlQK6y0TaypPUB69ZPf8yIBpxFO7cxPVfLVbMZpkIzAZPCqn50Ec9z1xLQoYlxism7sPMju+YDPTBjJKXOsvH3rbdfne7Hxen9GZVOl/djqvY6Dgvs3K90eGmpEQYW5OrMpHHRUJwEloc1Bb00fZBz1XGq8LCAbP2kp+0lxIAVEI7+8XbL7pcVhBU6I1sTyWBu89OVvg==",
                                        "viewport": "width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no",
                                        "og:sitename": "Amazon.com",
                                        "og:url": "https://www.amazon.com/dp/B0C5443MCX"
                                      }
                                    ],
                                    "cse_image": [
                                      {
                                        "src": "https://m.media-amazon.com/images/I/51MpKU9XowL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg"
                                      }
                                    ]
                                  }
                                },
                                {
                                  "kind": "customsearch#result",
                                  "title": "SAMSUNG Galaxy S23 Ultra Series AI Phone ... - Amazon.com",
                                  "htmlTitle": "SAMSUNG Galaxy S23 Ultra Series AI Phone ... - Amazon.com",
                                  "link": "https://www.amazon.com/SAMSUNG-Factory-Unlocked-Android-Smartphone/dp/B0BLP2G96N",
                                  "displayLink": "www.amazon.com",
                                  "snippet": "Top highlights · Brand. SAMSUNG · Operating System. Android 13.0 · Ram Memory Installed Size. 12 Gigabytes Per Second · CPU Model. Snapdragon · CPU Speed. 3.36 ...",
                                  "htmlSnippet": "Top highlights &middot; Brand. \\u003cb\\u003eSAMSUNG\\u003c/b\\u003e &middot; Operating System. Android 13.0 &middot; Ram Memory Installed Size. 12 Gigabytes Per Second &middot; CPU Model. Snapdragon &middot; CPU Speed. 3.36&nbsp;...",
                                  "formattedUrl": "https://www.amazon.com/SAMSUNG-Factory-Unlocked.../B0BLP2G96N",
                                  "htmlFormattedUrl": "https://www.amazon.com/\\u003cb\\u003eSAMSUNG\\u003c/b\\u003e-Factory-Unlocked.../B0BLP2G96N",
                                  "pagemap": {
                                    "cse_thumbnail": [
                                      {
                                        "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRYkz31uTpfMxL7LWbREM7xIdgU1wiaDm2WEqYAEDTFLrJeRfYRXwC2KW8&s",
                                        "width": "310",
                                        "height": "162"
                                      }
                                    ],
                                    "metatags": [
                                      {
                                        "og:image": "https://m.media-amazon.com/images/I/71nZ4-uixuL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg",
                                        "theme-color": "#131921",
                                        "og:type": "product",
                                        "og:image:width": "1910",
                                        "viewport": "width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no",
                                        "og:sitename": "Amazon.com",
                                        "og:title": "SAMSUNG Galaxy S23 Ultra Series AI Phone, Unlocked Android Smartphone, 512GB Storage, 12GB RAM, 200MP Camera, Night Mode, Long Battery Life, S Pen, US Version, 2023, Cream",
                                        "og:image:height": "1000",
                                        "flow-closure-id": "1757764051",
                                        "title": "Amazon.com: SAMSUNG Galaxy S23 Ultra Series AI Phone, Unlocked Android Smartphone, 512GB Storage, 12GB RAM, 200MP Camera, Night Mode, Long Battery Life, S Pen, US Version, 2023, Cream : Cell Phones & Accessories",
                                        "og:url": "https://www.amazon.com/dp/B0BLP2G96N",
                                        "encrypted-slate-token": "AnYxh/dNO3TTJSAXIOcayuRug/MDt8XFi1jBSIgkKIf9cvG+i+8kH4gFgX2p5GE5wZVKvOuydxrDEDeU91mgtvY2sUtANf2JSqrxm/fLpUyCA2EmEGHjPKbQygJ/2LtB3WFAXeMsXHiS3UBTaq2jBG6iGdX7Z88DRHI9+kviQH57Y0wW13VO+Vbovu39ypzNV7aKyatHAMdzFPqC2B4zvS4HHaIFZDnLLCZR7pkMN1xhpIWyi2AH2P/Of+urmw35E4hSvE/PG9yoilD2i29WxuRnjA/Cb8ISPSq/g50I4UlkJ7rq"
                                      }
                                    ],
                                    "cse_image": [
                                      {
                                        "src": "https://m.media-amazon.com/images/I/71nZ4-uixuL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg"
                                      }
                                    ]
                                  }
                                },
                                {
                                  "kind": "customsearch#result",
                                  "title": "USB C Headphone for Samsung S23 FE S22 S21 ... - Amazon.com",
                                  "htmlTitle": "USB C Headphone for Samsung S23 FE S22 S21 ... - Amazon.com",
                                  "link": "https://www.amazon.com/TITACUTE-Headphone-Magnetic-Microphone-Canceling/dp/B09BFFGQ5N",
                                  "displayLink": "www.amazon.com",
                                  "snippet": "Product description ... Latest DAC chip: Support digital input signals, high fidelity sound quality. Broad compatibility: Fits all devices with USB C port on the ...",
                                  "htmlSnippet": "Product description ... Latest DAC chip: Support digital input signals, high fidelity sound quality. Broad compatibility: Fits all devices with USB C port on the&nbsp;...",
                                  "formattedUrl": "https://www.amazon.com/TITACUTE-Headphone.../dp/B09BFFGQ5N",
                                  "htmlFormattedUrl": "https://www.amazon.com/TITACUTE-Headphone.../dp/B09BFFGQ5N",
                                  "pagemap": {
                                    "cse_thumbnail": [
                                      {
                                        "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKxC5libF2V9O4W6OAEIfHi72K_Xo-TGIRcmZbUwx0q_w3p2hEWdhSVRP2&s",
                                        "width": "310",
                                        "height": "162"
                                      }
                                    ],
                                    "metatags": [
                                      {
                                        "og:image": "https://m.media-amazon.com/images/I/61HJAUYKfPL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg",
                                        "theme-color": "#131921",
                                        "og:type": "product",
                                        "og:image:width": "1910",
                                        "og:title": "USB C Headphone for Samsung S23 FE S22 S21 S20 A53 A54 Wired Earbuds Magnetic in-Ear Type C Earphone with Microphone Volume Control Bass Stereo Noise Canceling for iPhone 15 Pro Max Pixel 6 6a 7a 8 5",
                                        "og:image:height": "1000",
                                        "flow-closure-id": "1757763100",
                                        "title": "Amazon.com: USB C Headphone for Samsung S23 FE S22 S21 S20 A53 A54 Wired Earbuds Magnetic in-Ear Type C Earphone with Microphone Volume Control Bass Stereo Noise Canceling for iPhone 15 Pro Max Pixel 6 6a 7a 8 5 : Electronics",
                                        "og:description": "TITACUTE USB C Headphone Type C Earphones Wired Earbud with Microphone Volume Control. Specification: Item: USB C Headphones, Material: ABS+TPE, Earbuds Type: In-Ear, Plug: Type-C, Features;Broad compatibility: Fits almost all usb-c devices on the market,Latest DAC chip: Support digital input sig...",
                                        "encrypted-slate-token": "AnYxtRRtixlu62aMCPle1XVp1znbcR8TdbAXYI6K438sJ2jOPX1tFkLqCyayWqynGGvExYC/QmY98QuK6vETgfIPqRa15p0AbMAsxkpOIs62IEE65aIb0VOqLyvyV1yIQKa2ahPHQvs6FAdLkQod5BOCZ+RME3U/7tCQEh8d3byn9c2CQ2vgV1iTTP3KLMMNOhn3CBplMUTcgXXtLJPamqvCsYa8XMlAmyW//aqAHQCcRfmOFr/MVa2+jHwf8hmLAiEvr4tUEKCHO46l7oBmOJyOjQNrWSHQhfc6ee/9BsVipRI=",
                                        "viewport": "width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no",
                                        "og:sitename": "Amazon.com",
                                        "og:url": "https://www.amazon.com/dp/B09BFFGQ5N"
                                      }
                                    ],
                                    "cse_image": [
                                      {
                                        "src": "https://m.media-amazon.com/images/I/61HJAUYKfPL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg"
                                      }
                                    ]
                                  }
                                },
                                {
                                  "kind": "customsearch#result",
                                  "title": "SAMSUNG Galaxy S23 FE 5G, US Version, 128GB ... - Amazon.com",
                                  "htmlTitle": "SAMSUNG Galaxy S23 FE 5G, US Version, 128GB ... - Amazon.com",
                                  "link": "https://www.amazon.com/SAMSUNG-Unlocked-Android-Smartphone-Processor/dp/B0CRPQ8W8X",
                                  "displayLink": "www.amazon.com",
                                  "snippet": "Top highlights · 6.4 FHD+ Dynamic AMOLED 2X, Infinity-O Display (2400x1080), 403ppi, HDR10+ certified, 120Hz refresh rate · 128GB ROM, 8GB RAM, Qualcomm SM8450 ...",
                                  "htmlSnippet": "Top highlights &middot; 6.4 FHD+ Dynamic AMOLED 2X, Infinity-O Display (2400x1080), 403ppi, HDR10+ certified, 120Hz refresh rate &middot; 128GB ROM, 8GB RAM, Qualcomm SM8450&nbsp;...",
                                  "formattedUrl": "https://www.amazon.com/SAMSUNG-Unlocked-Android.../B0CRPQ8W8X",
                                  "htmlFormattedUrl": "https://www.amazon.com/\\u003cb\\u003eSAMSUNG\\u003c/b\\u003e-Unlocked-Android.../B0CRPQ8W8X",
                                  "pagemap": {
                                    "cse_thumbnail": [
                                      {
                                        "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR5qtTd8N1WkG2_ZzQeus7i1GxeCnFDHBzuqlwGG4cflP4uyASCL8L0WV8&s",
                                        "width": "310",
                                        "height": "162"
                                      }
                                    ],
                                    "metatags": [
                                      {
                                        "og:image": "https://m.media-amazon.com/images/I/51l6YHB2x4L.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg",
                                        "theme-color": "#131921",
                                        "og:type": "product",
                                        "og:image:width": "1910",
                                        "og:title": "SAMSUNG Galaxy S23 FE 5G, US Version, 128GB, Black - Unlocked (Renewed)",
                                        "og:image:height": "1000",
                                        "flow-closure-id": "1757773760",
                                        "title": "Amazon.com: SAMSUNG Galaxy S23 FE 5G, US Version, 128GB, Black - Unlocked (Renewed) : Cell Phones & Accessories",
                                        "og:description": "Get more out of your passions with the phone that’s designed to elevate your everyday. Whether you’re binge-watching shows or capturing shots for social, Galaxy S23 FE is jam-packed with features that help you get more out of whatever you’re into including a long-lasting battery, a premium proces...",
                                        "encrypted-slate-token": "AnYx/aXMH73T6xHH4I4fa/Acm/wqzKqIFzUS0nIW95jFs4vJyBxUD4HaBrGplYPy7xyuzVw5E/yAhaE74myzwX9CqMlXBr7OiArWSGCCQNW+N+3XdKXl/HvwbeL6GNkbpDTCeQ0jHkHAZuguGxcLN9AJSfBQtyDc1NdEikNB2Oz1eL1Rxl/vsT3hDcxicgnFAmlZkEPALeQ27wbUtNwtU1RQVcq7Bhnc5mauB0OVslpGkDFOyeV7WS6GWTP4J7IVcy+dsNX0xbCXM3uRs6ts3W1QURnsD39Fb3V3n1s6/OWuALI=",
                                        "viewport": "width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no",
                                        "og:sitename": "Amazon.com",
                                        "og:url": "https://www.amazon.com/dp/B0CRPQ8W8X"
                                      }
                                    ],
                                    "cse_image": [
                                      {
                                        "src": "https://m.media-amazon.com/images/I/51l6YHB2x4L.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg"
                                      }
                                    ]
                                  }
                                },
                                {
                                  "kind": "customsearch#result",
                                  "title": "MAGIC JOHN 2 Pack Screen Protector for Samsung ... - Amazon.com",
                                  "htmlTitle": "MAGIC JOHN 2 Pack Screen Protector for Samsung ... - Amazon.com",
                                  "link": "https://www.amazon.com/MAGIC-JOHN-S23-Ultra-Shock-Resistant/dp/B0C9CG7DKW",
                                  "displayLink": "www.amazon.com",
                                  "snippet": "Amazon.com: MAGIC JOHN 2 Pack Screen Protector for Samsung Galaxy S23 Ultra - Ceramic Film, Fingerprint ID Compatible, Easy Installation, Shock-Resistant, ...",
                                  "htmlSnippet": "Amazon.com: MAGIC JOHN 2 Pack Screen Protector for \\u003cb\\u003eSamsung\\u003c/b\\u003e Galaxy \\u003cb\\u003eS23\\u003c/b\\u003e Ultra - Ceramic Film, Fingerprint ID Compatible, Easy Installation, Shock-Resistant,&nbsp;...",
                                  "formattedUrl": "https://www.amazon.com/MAGIC-JOHN-S23-Ultra.../B0C9CG7DKW",
                                  "htmlFormattedUrl": "https://www.amazon.com/MAGIC-JOHN-\\u003cb\\u003eS23\\u003c/b\\u003e-Ultra.../B0C9CG7DKW",
                                  "pagemap": {
                                    "cse_thumbnail": [
                                      {
                                        "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTR7VtIGDqEkjTWrsF9l0sg2DJizZWBJIJOdG58q-S1ICzBSitWmV_WeqD0&s",
                                        "width": "310",
                                        "height": "162"
                                      }
                                    ],
                                    "metatags": [
                                      {
                                        "og:image": "https://m.media-amazon.com/images/I/719N86Sq3-L.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg",
                                        "theme-color": "#131921",
                                        "og:type": "product",
                                        "og:image:width": "1910",
                                        "og:title": "MAGIC JOHN 2 Pack Screen Protector for Samsung Galaxy S23 Ultra - Ceramic Film, Fingerprint ID Compatible, Easy Installation, Shock-Resistant, 3D Curved, Bubble Free",
                                        "og:image:height": "1000",
                                        "flow-closure-id": "1757789228",
                                        "title": "Amazon.com: MAGIC JOHN 2 Pack Screen Protector for Samsung Galaxy S23 Ultra - Ceramic Film, Fingerprint ID Compatible, Easy Installation, Shock-Resistant, 3D Curved, Bubble Free : Cell Phones & Accessories",
                                        "og:description": "S23 Ultra Screen Protector",
                                        "encrypted-slate-token": "AnYxZOpJAf8PigIeXTg4WAReF3Tjs4fucLE8Z7NFBhZpG23lVVutapst3cLX76qqI4FY/z5NYBUjYFkPxOsghpFvEBkLUiwQfxoaGH4zpYMV+HmpbBab8R/aM3FXrXVRLNBbGxQEO28YYdhVK0ydIOQA2pWnm57ZGbpD/Cd065tponFsXZSXjJQMKBrTO2DHYxaNcn4xkdszf+oZHiazl3emxyjFsTX4UkKfnaI2FmxnNRGcL5YoTkJWk+sCLz1yj0gGkoi8Gha3J6oAi7wtDaVa9FePpgrc7fU5n5C9qwWAgMw=",
                                        "viewport": "width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no",
                                        "og:sitename": "Amazon.com",
                                        "og:url": "https://www.amazon.com/dp/B0C9CG7DKW"
                                      }
                                    ],
                                    "cse_image": [
                                      {
                                        "src": "https://m.media-amazon.com/images/I/719N86Sq3-L.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg"
                                      }
                                    ]
                                  }
                                },
                                {
                                  "kind": "customsearch#result",
                                  "title": "USB C Headphones for Samsung Galaxy S24 S23 ... - Amazon.com",
                                  "htmlTitle": "USB C Headphones for Samsung Galaxy S24 S23 ... - Amazon.com",
                                  "link": "https://www.amazon.com/Headphones-Samsung-Earphones-Earbuds-OnePlus/dp/B0B93YM4PC",
                                  "displayLink": "www.amazon.com",
                                  "snippet": "The in-ear design of the usb type c headphones features a secure and comfortable fit for different types of ears, also effectively blocks out the ambient noise.",
                                  "htmlSnippet": "The in-ear design of the usb type c headphones features a secure and comfortable fit for different types of ears, also effectively blocks out the ambient noise.",
                                  "formattedUrl": "https://www.amazon.com/Headphones-Samsung...Earbuds.../B0B93YM4PC",
                                  "htmlFormattedUrl": "https://www.amazon.com/Headphones-\\u003cb\\u003eSamsung\\u003c/b\\u003e...Earbuds.../B0B93YM4PC",
                                  "pagemap": {
                                    "cse_thumbnail": [
                                      {
                                        "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTp2g7UJG-51mnXWSNc_eL9cz3A4nrgwLxj3muo12_-Au5ElkI0NGEUV-g&s",
                                        "width": "224",
                                        "height": "224"
                                      }
                                    ],
                                    "metatags": [
                                      {
                                        "og:image": "https://m.media-amazon.com/images/I/6125osc2MQL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg",
                                        "theme-color": "#131921",
                                        "og:type": "product",
                                        "og:image:width": "1910",
                                        "og:title": "USB C Headphones for Samsung Galaxy S24 S23 Ultra S22 S21 FE S20 A54 A53 USB C Earphones with Mic Volume Control Wired Earbuds USB Type C Headphones for iPhone 16 15 Pro Max Plus iPad Pro Pixel 8 7 6a",
                                        "og:image:height": "1000",
                                        "flow-closure-id": "1757769840",
                                        "title": "Amazon.com: USB C Headphones for Samsung Galaxy S24 S23 Ultra S22 S21 FE S20 A54 A53 USB C Earphones with Mic Volume Control Wired Earbuds USB Type C Headphones for iPhone 16 15 Pro Max Plus iPad Pro Pixel 8 7 6a : Electronics",
                                        "og:description": "USB C Headphones Type C Earphones for Samsung Galaxy S23 S22 S24 S21 Ultra 5G USB C Earbuds HiFi Stereo Earphone with Microphone Volume Control Noise Isotiolan Magnetic Wired Headphone USB C for iPhone 16 15 Pro Max Galaxy S20 FE Note 20 Z Flip 3 2 A54 A53 Google Pixel 9 8 7 6A 6 Pro 5 Oneplus 9 ...",
                                        "encrypted-slate-token": "AnYxY2t2Vo96bBGgXpiF3fZRinzi3ZksAJ+r+630cJxiPX8bsAWrfl6FDXvX8DQ8plTQYUzEEuHUDP2+Ms+A7Xt27J1JQ6m90X6zR8bRTxvLWrkmO+mlQRi4i1xca9ota4wDNO2huuJm1HKOlLscPLlPGxyhzTK1gft092Pi2lRqgxoYELYrPGLFJnVI+qJp35nVABt9+QwUJA09LToSuXT9fWbx33ZWZK5kilPaJC0NLluGSLkuGMArynbDpAKK42f7OFcii07NgB+jgUS0m+JWwODF9bdX5fEjktjQSZZFxg==",
                                        "viewport": "width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no",
                                        "og:sitename": "Amazon.com",
                                        "og:url": "https://www.amazon.com/dp/B0B93YM4PC"
                                      }
                                    ],
                                    "cse_image": [
                                      {
                                        "src": "https://m.media-amazon.com/images/I/6125osc2MQL._UF894,1000_QL80_.jpg"
                                      }
                                    ]
                                  }
                                },
                                {
                                  "kind": "customsearch#result",
                                  "title": "SAMSUNG Galaxy S23 Ultra 5G, US Version, 512GB ... - Amazon.com",
                                  "htmlTitle": "SAMSUNG Galaxy S23 Ultra 5G, US Version, 512GB ... - Amazon.com",
                                  "link": "https://www.amazon.com/SAMSUNG-Galaxy-S23-Ultra-Smartphone/dp/B0C3X9NLM5",
                                  "displayLink": "www.amazon.com",
                                  "snippet": "Display. Screen Size, 6.8 Inches. Resolution · Connectivity. Wireless Provider, Unlocked for All Carriers · Camera. Front Photo Sensor Resolution, 12 MP.",
                                  "htmlSnippet": "Display. Screen Size, 6.8 Inches. Resolution &middot; Connectivity. Wireless Provider, Unlocked for All Carriers &middot; Camera. Front Photo Sensor Resolution, 12 MP.",
                                  "formattedUrl": "https://www.amazon.com/SAMSUNG-Galaxy-S23-Ultra.../B0C3X9NLM5",
                                  "htmlFormattedUrl": "https://www.amazon.com/\\u003cb\\u003eSAMSUNG\\u003c/b\\u003e-Galaxy-\\u003cb\\u003eS23\\u003c/b\\u003e-Ultra.../B0C3X9NLM5",
                                  "pagemap": {
                                    "cse_thumbnail": [
                                      {
                                        "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRg7jR5IfHJ2Ac9v8cRGhrybsFu2yogsUhBHp7-Nav1hP0QpQniv2LlQco&s",
                                        "width": "310",
                                        "height": "162"
                                      }
                                    ],
                                    "metatags": [
                                      {
                                        "og:image": "https://m.media-amazon.com/images/I/51MpKU9XowL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg",
                                        "theme-color": "#131921",
                                        "og:type": "product",
                                        "og:image:width": "1910",
                                        "og:title": "SAMSUNG Galaxy S23 Ultra 5G, US Version, 512GB, Green - Unlocked (Renewed)",
                                        "og:image:height": "1000",
                                        "flow-closure-id": "1757790328",
                                        "title": "Amazon.com: SAMSUNG Galaxy S23 Ultra 5G, US Version, 512GB, Green - Unlocked (Renewed) : Cell Phones & Accessories",
                                        "og:description": "Capture the night, even in low light. Ready to own the night? Whether you’re headed to a concert or romantic night out, there’s no such thing as bad lighting with Night Mode. Galaxy S23 Ultra lets you capture epic content in any setting with stunning Nightography. Capture scenes in stunning detai...",
                                        "encrypted-slate-token": "AnYxd6tMuAExyyo8Z4W8RDukdz/9LXXPZdc7WYztdZJjanvDqr/WlJDDuNircS5NTn8LwJdOl03hE2H808wo8rjh5h+W6RgcQgRW9/37j2j/z467zZHx3VPoreKL08Ms9sQ3yKZMU797cmXqyY9fZD1s5l7iD/Ib73PCQuj1pSXQH3ECLhiHcBB+zZxiHLwEA5wjbn7a4gB1gLXyP7kq+w4Hucmd4L9KunYmVgtZF/Gs91fFshW2c2OKtqeJZUhw68vo11v/LYzBEDQDu07/4s+4zvSrb2OmtXzYydMzn33Tag==",
                                        "viewport": "width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no",
                                        "og:sitename": "Amazon.com",
                                        "og:url": "https://www.amazon.com/dp/B0C3X9NLM5"
                                      }
                                    ],
                                    "cse_image": [
                                      {
                                        "src": "https://m.media-amazon.com/images/I/51MpKU9XowL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg"
                                      }
                                    ]
                                  }
                                },
                                {
                                  "kind": "customsearch#result",
                                  "title": "SAMSUNG Galaxy S23 Ultra 5G Factory Unlocked ... - Amazon.com",
                                  "htmlTitle": "SAMSUNG Galaxy S23 Ultra 5G Factory Unlocked ... - Amazon.com",
                                  "link": "https://www.amazon.com/SAMSUNG-Galaxy-Ultra-Factory-Unlocked/dp/B0C51Q5Z9K",
                                  "displayLink": "www.amazon.com",
                                  "snippet": "Top highlights · Brand. Samsung · Operating System. Android · Ram Memory Installed Size. 12 GB · CPU Model. Snapdragon · CPU Speed. 3.36 GHz · Memory Storage ...",
                                  "htmlSnippet": "Top highlights &middot; Brand. \\u003cb\\u003eSamsung\\u003c/b\\u003e &middot; Operating System. Android &middot; Ram Memory Installed Size. 12 GB &middot; CPU Model. Snapdragon &middot; CPU Speed. 3.36 GHz &middot; Memory Storage&nbsp;...",
                                  "formattedUrl": "https://www.amazon.com/SAMSUNG-Galaxy-Ultra.../dp/B0C51Q5Z9K",
                                  "htmlFormattedUrl": "https://www.amazon.com/\\u003cb\\u003eSAMSUNG\\u003c/b\\u003e-Galaxy-Ultra.../dp/B0C51Q5Z9K",
                                  "pagemap": {
                                    "cse_thumbnail": [
                                      {
                                        "src": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSI_R6W2nrIhFRdg8asjUSeG-nZkRJsfm7E4nIV4SCeIXUrGELNWYK5OpjF&s",
                                        "width": "310",
                                        "height": "162"
                                      }
                                    ],
                                    "metatags": [
                                      {
                                        "og:image": "https://m.media-amazon.com/images/I/51ZZO2wp8EL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg",
                                        "theme-color": "#131921",
                                        "og:type": "product",
                                        "og:image:width": "1910",
                                        "og:title": "SAMSUNG Galaxy S23 Ultra 5G Factory Unlocked 512GB - Phantom Black (Renewed)",
                                        "og:image:height": "1000",
                                        "flow-closure-id": "1757790328",
                                        "title": "Amazon.com: SAMSUNG Galaxy S23 Ultra 5G Factory Unlocked 512GB - Phantom Black (Renewed) : Cell Phones & Accessories",
                                        "og:description": "PRODUCT OVERVIEW The Samsung Galaxy S23 Series is a high-end smartphone known for its large, vibrant display, powerful camera system, and included S Pen. It features a 6.8-inch Dynamic AMOLED 2X display, a quad-camera setup with a 200MP main sensor, and the Snapdragon 8 Gen 2 processor. The devic...",
                                        "encrypted-slate-token": "AnYxsRAiCO27OItxr+RQOUw3dwzNv+aHftm13YYg5Su4EYbf1+i/mXO3AYn2bmSO5fuMOlKf2p7SzQVKHz06xcYlz5nKFGknTlG8+Vv6pT4LCTElXXASFrVPceIzfym4tx1AS0dLes9SKFC9PFM4Bt3v6y25o01of20SvUrGOa3iKVrcOAPOkxhMdVqyE0sKktJ43rPHA2xwK931ohQWAV8cWege+cQfLSsj4wBeJxHFqhJcsO292gu1JCEo9UPh9ChSOxaCgr3LV+c21rBL/wtq2N8WdDA8I6MMdP9r8tJ5zL2C",
                                        "viewport": "width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no",
                                        "og:sitename": "Amazon.com",
                                        "og:url": "https://www.amazon.com/dp/B0C51Q5Z9K"
                                      }
                                    ],
                                    "cse_image": [
                                      {
                                        "src": "https://m.media-amazon.com/images/I/51ZZO2wp8EL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg"
                                      }
                                    ]
                                  }
                                }
                              ]
                            }
                                        
                    """;
}
