package com.zlfcapp.poster.mvp.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.king.base.util.LogUtils;
import com.king.base.util.SystemUtils;
import com.zlfcapp.poster.Constants;
import com.zlfcapp.poster.R;
import com.zlfcapp.poster.mvp.activity.ContentActivity;

import butterknife.BindView;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/21
 */

public class WebFragment extends SimpleFragment implements ContentActivity.OnBackPressLinstener {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.vError)
    LinearLayout vError;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    private String url;
    private String title;

    protected boolean isError;

    private boolean isShowError;

    public static WebFragment newInstance(String url, String title) {

        Bundle args = new Bundle();

        WebFragment fragment = new WebFragment();
        fragment.url = url;
        fragment.title = title;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_webwiew;
    }

    @Override
    public void initUI() {

        setSupportActionBarBackgroup(mToolBar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar.setMax(100);

        isShowError = addErrorView(vError);
        setWeb();
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setDownloadListener(new MyWebViewDownLoadListener());
//        WebSettings ws = webView.getSettings();
//        //是否允许脚本支持
//        ws.setJavaScriptEnabled(true);
//        ws.setDomStorageEnabled(true);
//
//        ws.setJavaScriptCanOpenWindowsAutomatically(true);

//        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);

//        webView.setHorizontalScrollBarEnabled(false);

//        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        String str = getIntent().getStringExtra(Constants.KEY_URL);
        if (!TextUtils.isEmpty(str)) {
            this.url = str;
        }


        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                updateProgressBar(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }
        });
        webView.setWebViewClient(getWebViewClient());

        load(webView, url);
    }

    @Override
    public void onBackPress() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();   //这是退出方法
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void setWeb() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setDomStorageEnabled(true);

        webSettings.setAppCacheEnabled(false);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);// 设置缓冲大小，我设的是8M
        String appCacheDir = getContext().getDir("cache", Context.MODE_PRIVATE)
                .getPath();
        webSettings.setDatabasePath(appCacheDir);
        webSettings.setPluginState(WebSettings.PluginState.ON);
    }

    @Override
    public void initData() {
        ((ContentActivity) getActivity()).setOnBackPressLinstener(this);
    }


    public WebViewClient getWebViewClient() {
        return new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtils.d("startUrl:" + url);
                isError = false;
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.d("url:" + url);
                if (!url.startsWith("http")) {
                    try {
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        getContext().startActivity(intent);
                    } catch (Exception e) {
                        return true;
                    }
                }

                return super.shouldOverrideUrlLoading(view, url);
            }


            @Override
            public void onReceivedError(WebView view, WebResourceRequest
                    request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                updateProgressBar(100);
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String
                    failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                LogUtils.w("errorCode:" + errorCode + "|failingUrl:" + failingUrl);
                showReceiveError();
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError
                    error) {
                super.onReceivedSslError(view, handler, error);
                handler.cancel();
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideReceiveError();
            }
        }

                ;
    }

    /**
     * @param group
     * @return true表示已添加ErrorView并显示ErrorView/false表示不处理
     */

    public boolean addErrorView(ViewGroup group) {
        group.addView(LayoutInflater.from(context).inflate(R.layout.layout_error, null));
        return true;
    }

    private void showReceiveError() {
        isError = true;
        if (SystemUtils.isNetWorkActive(context)) {
            LogUtils.w("Page loading failed.");
        } else {
            LogUtils.w("Network unavailable.");
        }

        if (isShowError) {
            webView.setVisibility(View.GONE);
            vError.setVisibility(View.VISIBLE);

        }


    }

    private void hideReceiveError() {
        if (isError) {
            showReceiveError();
        } else {
            if (webView != null) {
                webView.setVisibility(View.VISIBLE);
                vError.setVisibility(View.GONE);
            }
        }

    }

    /**
     * 加载url
     *
     * @param webView
     * @param url
     */
    private void load(WebView webView, String url) {
        LogUtils.d("url:" + url);
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }

    }

    private boolean isGoBack() {
        return webView != null && webView.canGoBack();
    }


    private void updateProgressBar(int progress) {
        updateProgressBar(true, progress);
    }


    private void updateProgressBar(boolean isVisibility, int progress) {
        if (progressBar != null) {
            progressBar.setVisibility((isVisibility && progress < 100) ? View.VISIBLE : View.GONE);
            progressBar.setProgress(progress);
        }
    }
}
