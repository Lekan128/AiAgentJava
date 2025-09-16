package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.method.AiToolMethod;
import org.example.method.description.ArgDesc;
import org.example.web.search.Google;
import org.example.web.search.GoogleSearchResponse;
import org.example.web.text_extractor.WebsiteTextExtractor;

import java.io.IOException;
import java.util.List;

public class WebSearchProcessor {

    @AiToolMethod("Search the web for information")
    public static String search(@ArgDesc("The search parameter") String searchParam) throws IOException, InterruptedException {
        List<GoogleSearchResponse> searchResponses = Google.search(searchParam);
        //Using only the first 2 search results.
        if (searchResponses.size() >2) searchResponses = searchResponses.subList(0, 2);
        List<WebsiteTextExtractor.ExtractedPageSummary> extractedPages = WebsiteTextExtractor.extractAllFromSearchResponse(searchResponses);
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(extractedPages);
    }


    String response = """
            [ {
              "url" : "https://www.amazon.com/Brightening-Hyperpigmentation-Treatment-Remover-Armipts/dp/B018RFA3QK",
              "title" : "Amazon.com",
              "content" : "Click the button below to continue shopping Conditions of Use Privacy Policy © 1996-2025, Amazon.com, Inc. or its affiliates"
            } ]
            """;
    String searchResponseJson = """
            [{"kind":"customsearch#result","title":"FAIR & WHITE So White, Skin Brightening Lotion ... - Amazon.com","htmlTitle":"FAIR & WHITE So White, Skin Brightening Lotion ... - Amazon.com","link":"https://www.amazon.com/Brightening-Hyperpigmentation-Treatment-Remover-Armipts/dp/B018RFA3QK","displayLink":"www.amazon.com","snippet":"Amazon.com : FAIR & WHITE So White, Skin Brightening Lotion - 500 ml / 17.6 Fl oz - Daily Moisturizing Body Lotion : Beauty & Personal Care.","htmlSnippet":"Amazon.com : FAIR &amp; WHITE So White, Skin Brightening Lotion - 500 ml / 17.6 Fl oz - Daily Moisturizing <b>Body Lotion</b> : Beauty &amp; Personal Care.","formattedUrl":"https://www.amazon.com/Brightening-Hyperpigmentation.../B018RFA3QK","htmlFormattedUrl":"https://www.amazon.com/Brightening-Hyperpigmentation.../B018RFA3QK","pagemap":{"cse_thumbnail":[{"src":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSpw2X1gYCY4bHGDLZbDhBEZale_Bv-8z8WEzpfdI7vFDUlLkDkmK593nxP&s","width":"310","height":"162"}],"metatags":[{"og:image":"https://m.media-amazon.com/images/I/71m+KRzJHZL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg","theme-color":"#131921","og:type":"product","og:image:width":"1910","og:title":"FAIR & WHITE So White, Skin Brightening Lotion - 500 ml / 17.6 Fl oz - Daily Moisturizing Body Lotion","og:image:height":"1000","flow-closure-id":"1757959652","title":"Amazon.com : FAIR & WHITE So White, Skin Brightening Lotion - 500 ml / 17.6 Fl oz - Daily Moisturizing Body Lotion : Beauty & Personal Care","og:description":"Luxurious clarifying and moisturizing body lotion formulated to lighten dark spots and even out skin tone. Skin Perfecting Lotion promotes skin rejuvenation with potent skin brightening complex, restoring beautiful luminous, silky, soft skin. Directions:Apply to uneven skin tone twice a day on cl...","encrypted-slate-token":"AnYxv4ReYgJKnfZ0MJm955vTen/woOrCJUvv4dW4nv4WxXkwUUBQqQhLA5Ut+v2UtY9S3oiA2kFJ+TsX6TYButNygjD7KmaFExtRMFtXzUyy64Ivs4Xv5fRGGGmfWcK70Bidh9xr3SQzmU2cHRpDoWthAcsmERqbAOFQtjoa1PXcquG/+b8uVyHnt+dIHqbmiphqQC+itQ4DXHyXHsxhXIiI0Q1DQ/7WBZxnTf+Uqf8WC5YPffOUYfZnx96ZU3F14xmBzX2dirqOj9CSJ1Zg4OmPfYcK4zfDBz12hvCqgxX3678l","viewport":"width=device-width, maximum-scale=2, minimum-scale=1, initial-scale=1, shrink-to-fit=no","og:sitename":"Amazon.com","og:url":"https://www.amazon.com/dp/B018RFA3QK"}],"cse_image":[{"src":"https://m.media-amazon.com/images/I/71m+KRzJHZL.jpg_BO30,255,255,255_UF900,850_SR1910,1000,0,C_QL100_.jpg"}]}}]
            
            """;
    //https://www.amazon.com/Brightening-Hyperpigmentation-Treatment-Remover-Armipts/dp/B018RFA3QK
    String websiteHtml = """
            <!doctype html>
            <!--[if lt IE 7]> <html lang="en-us" class="a-no-js a-lt-ie9 a-lt-ie8 a-lt-ie7"> <![endif]-->
            <!--[if IE 7]>    <html lang="en-us" class="a-no-js a-lt-ie9 a-lt-ie8"> <![endif]-->
            <!--[if IE 8]>    <html lang="en-us" class="a-no-js a-lt-ie9"> <![endif]-->
            <!--[if gt IE 8]><!-->
            <html class="a-no-js" lang="en-us">
             <!--<![endif]-->
             <head>
              <meta http-equiv="content-type" content="text/html; charset=UTF-8">
              <meta charset="utf-8">
              <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
              <title dir="ltr">Amazon.com</title>
              <meta name="viewport" content="width=device-width">
              <link rel="stylesheet" href="https://images-na.ssl-images-amazon.com/images/G/01/AUIClients/AmazonUI-3c913031596ca78a3768f4e934b1cc02ce238101.secure.min._V1_.css">
              <script>
                        
            if (true === true) {
                var ue_t0 = (+ new Date()),
                    ue_csm = window,
                    ue = { t0: ue_t0, d: function() { return (+new Date() - ue_t0); } },
                    ue_furl = "fls-na.amazon.com",
                    ue_mid = "ATVPDKIKX0DER",
                    ue_sid = (document.cookie.match(/session-id=([0-9-]+)/) || [])[1],
                    ue_sn = "opfcaptcha.amazon.com",
                    ue_id = 'Q6284G5XQK6ZB9W6YJS1';
            }
            </script>
             </head>
             <body>
              <!--
                    To discuss automated access to Amazon data please contact api-services-support@amazon.com.
                    For information about migrating to our APIs refer to our Marketplace APIs at https://developer.amazonservices.com/ref=rm_c_sv, or our Product Advertising API at https://affiliate-program.amazon.com/gp/advertising/api/detail/main.html/ref=rm_c_ac for advertising use cases.
            -->
              <!--
            Correios.DoNotSend
            -->
              <div class="a-container a-padding-double-large" style="min-width:350px;padding:44px 0 !important">
               <div class="a-row a-spacing-double-large" style="width: 350px; margin: 0 auto">
                <div class="a-row a-spacing-medium a-text-center">
                 <i class="a-icon a-logo" alt="Amazon logo"></i>
                </div>
                <div class="a-box a-alert a-alert-info a-spacing-base">
                 <div class="a-box-inner">
                  <i class="a-icon a-icon-alert" alt="Alert icon"></i>
                  <h4>Click the button below to continue shopping</h4>
                 </div>
                </div>
                <div class="a-section">
                 <div class="a-box a-color-offset-background">
                  <div class="a-box-inner a-padding-extra-large">
                   <form method="get" action="/errors/validateCaptcha" name="">
                    <input type="hidden" name="amzn" value="jDbCpXHJNV4SljZjA+W8fA=="><input type="hidden" name="amzn-r" value="/Brightening-Hyperpigmentation-Treatment-Remover-Armipts/dp/B018RFA3QK"> <input type="hidden" name="field-keywords" value="JPRPJX">
                    <div class="a-section a-spacing-extra-large">
                     <div class="a-row">
                      <span class="a-button a-button-primary a-span12"> <span class="a-button-inner">\s
                        <button type="submit" class="a-button-text" alt="Continue shopping">Continue shopping</button>\s
                       </span> </span>
                     </div>
                    </div>
                   </form>
                  </div>
                 </div>
                </div>
               </div>
               <div class="a-divider a-divider-section">
                <div class="a-divider-inner"></div>
               </div>
               <div class="a-text-center a-spacing-small a-size-mini">
                <a href="https://www.amazon.com/gp/help/customer/display.html/ref=footer_cou?ie=UTF8&amp;nodeId=508088">Conditions of Use</a> <span class="a-letter-space"></span> <span class="a-letter-space"></span> <span class="a-letter-space"></span> <span class="a-letter-space"></span> <a href="https://www.amazon.com/gp/help/customer/display.html/ref=footer_privacy?ie=UTF8&amp;nodeId=468496">Privacy Policy</a>
               </div>
               <div class="a-text-center a-size-mini a-color-base">
                © 1996-2025, Amazon.com, Inc. or its affiliates
                <script>
                       if (true === true) {
                         document.write('<img src="https://fls-na.amaz'+'on.com/'+'1/oc-csi/1/OP/requestId=Q6284G5XQK6ZB9W6YJS1&js=1" alt=""/>');
                       };
                      </script>
                <noscript>
                 <img src="https://fls-na.amazon.com/1/oc-csi/1/OP/requestId=Q6284G5XQK6ZB9W6YJS1&amp;js=0" alt="">
                </noscript>
               </div>
              </div>
              <script>
                if (true === true) {
                    var head = document.getElementsByTagName('head')[0],
                        prefix = "https://images-na.ssl-images-amazon.com/images/G/01/csminstrumentation/",
                        elem = document.createElement("script");
                    elem.src = prefix + "csm-captcha-instrumentation.min.js";
                    head.appendChild(elem);
                        
                    elem = document.createElement("script");
                    elem.src = prefix + "rd-script-6d68177fa6061598e9509dc4b5bdd08d.js";
                    head.appendChild(elem);
                }
                </script>
             </body>
            </html>
            """;

    public static void main(String[] args) {
        try {
            System.out.println(search("SooPure Lait hydratant moisturising lotion"));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
