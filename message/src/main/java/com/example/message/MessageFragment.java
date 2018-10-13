package com.example.message;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Administrator on 2018/3/28.
 */

public class MessageFragment extends Fragment{
    private View messageView;
    private WebView webView;
    private WebSettings settings;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        messageView = inflater.inflate(R.layout.fragment_message, null);
        webView = messageView.findViewById(R.id.webview);
//        webView.setScrollContainer(false);
//        webView.setVerticalScrollBarEnabled(false);
//        webView.setHorizontalScrollBarEnabled(false);
        WebSettings settings = webView.getSettings();
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setBuiltInZoomControls(true);//support zoom
        settings.setDisplayZoomControls(false);
        //webSettings.setPluginsEnabled(true);
        webView.loadUrl("http://www.zgfeipin.cn/Article_5_1_0/");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        return messageView;
    }
}
