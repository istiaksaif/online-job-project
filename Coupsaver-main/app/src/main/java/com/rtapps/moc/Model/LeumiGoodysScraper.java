package com.rtapps.moc.Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;


public class LeumiGoodysScraper {
    private final String baseUrl;

    LeumiGoodysScraper(String siteUrl) {
        this.baseUrl = siteUrl + "/OrderSummary?k=";
    }

    public LeumiGoodysScraper() {
        this("https://Leumigoodys.co.il");
    }

    public OrderInfo scrape(String orderID) throws IOException {
        Document doc = Jsoup.connect(this.baseUrl + orderID).get();

        //Extract message
        Element e_msgText = doc.select(".message div").first();
        String msgText = e_msgText == null ? "" : e_msgText.text();
        String msg = msgText.replace("איזה כיף שקנית ב", "").trim();

        //Extract title
        Element e_gifcardTitle = doc.select("#gifcardBox .giftcard-title").first();
        String gifcardTitle = e_gifcardTitle == null ? "" : e_gifcardTitle.text();

        //Extract date
        Element e_gifcardDate = doc.select("#gifcardBox .giftcard-content div span").get(1);
        String gifcardDate = e_gifcardDate == null ? "" : e_gifcardDate.text();

        //Extract barcode
        Element e_barcode = doc.select(".barcodeItem svg").first();
        String barcode = e_barcode == null ? "" : e_barcode.attr("jsbarcode-value");


        return new OrderInfo(gifcardTitle, msg, gifcardDate, barcode);
    }

}