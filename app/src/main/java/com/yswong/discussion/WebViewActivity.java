package com.yswong.discussion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


//This activity is used to display the website
//when user clicked a link in the discussion forum
public class WebViewActivity extends AppCompatActivity {

    //Define Variables
    private WebView webView;
    private String Url;
    private ProgressBar progressBar;


    //Action on the activity create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        //Get the url link that passed from thread content activity
        Url = getIntent().getStringExtra("URL");

        //Link object to vars
        webView = findViewById(R.id.webviewaa);
        progressBar = findViewById(R.id.progressBar7);
        progressBar.setMax(100);



        // Set up Web Client
        webView.setWebViewClient(new WebViewClient());

        // Load the Url from the RecyclerView (MyAdapter)
        webView.loadUrl(Url);

        // Create a web setting
        WebSettings webSettings = webView.getSettings();

        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //Set progress bar progress
                progressBar.setProgress(newProgress);

                //Dismiss progressbar when website is fully loaded
                if(newProgress != 100)
                {
                    progressBar.setVisibility(View.VISIBLE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }

    //Set up the back button, to go back to the previous web page if exist
    @Override
    public void onBackPressed()
    {
        if(webView.canGoBack())
        {
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

}

