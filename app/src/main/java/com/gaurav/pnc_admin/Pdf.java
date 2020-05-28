package com.gaurav.pnc_admin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Pdf extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private String urlget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        getSupportActionBar().setTitle(getIntent().getStringExtra("filename"));
        urlget = getIntent().getStringExtra("fileurl");

        webView = findViewById(R.id.WV);
        webView.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.pb);
        progressBar.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
            }
        });
        String url = "";
        try {
            url = URLEncoder.encode(urlget, "UTF-8");
            progressBar.setVisibility(View.GONE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + url);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
