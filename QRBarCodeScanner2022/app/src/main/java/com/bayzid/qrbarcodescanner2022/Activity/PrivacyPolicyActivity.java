package com.bayzid.qrbarcodescanner2022.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.bayzid.qrbarcodescanner2022.R;
import com.bayzid.qrbarcodescanner2022.utils.CheckInternet;

public class PrivacyPolicyActivity extends AppCompatActivity {
    private WebView webView;
    private TextView checkInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privay_policy);
        checkInternet = findViewById(R.id.networkcheck);

//        PDFView pdfView = findViewById(R.id.pdfView);
//        pdfView.fromAsset("privacypolicy.pdf")
//                .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
//                .enableSwipe(true) // allows to block changing pages using swipe
//                .swipeHorizontal(false)
//                .enableDoubletap(true)
//                .defaultPage(0)
//                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//                .password(null)
//                .scrollHandle(null)
//                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
//                // spacing between pages in dp. To define spacing color, set view background
//                .spacing(0)
//                .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
//                .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
//                .pageSnap(false) // snap pages to screen boundaries
//                .pageFling(false) // make a fling change only a single page like ViewPager
//                .nightMode(false) // toggle night mode
//                .load();
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new Callback());
        webView.loadUrl("https://docs.google.com/document/d/1Q3Dnk6jQs0WfXmclHRuP0sMzBPFKhG0j/edit?usp=sharing&ouid=118254028426288750144&rtpof=true&sd=true");
    }
    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(!CheckInternet.isConnectedToInternet(PrivacyPolicyActivity.this)){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!CheckInternet.isConnectedToInternet(PrivacyPolicyActivity.this)){
            checkInternet.setVisibility(View.VISIBLE);
        }else {
            checkInternet.setVisibility(View.GONE);
        }
    }
}