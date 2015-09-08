package com.klarna.ondemand;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jockeyjs.Jockey;
import com.jockeyjs.JockeyHandler;
import com.jockeyjs.JockeyImpl;

import java.util.Map;

abstract class WebViewActivity extends Activity {

    private ProgressDialog progressDialog;
    private WebViewClient webViewClient;
    private Jockey jockey;
    private WebView webView;

    private static final String USER_READY_EVENT_IDENTIFIER = "userReady";
    private static final String USER_READY_RESPONSE_EVENT_IDENTIFIER = "userReadyResponse";
    private static final String USER_ERROR_EVENT_IDENTIFIER = "userError";
    public static final int RESULT_ERROR = 1;

    private class UserReadyResponsePayload {
        private String event;
        private String status;

        protected UserReadyResponsePayload(String event, String status) {
            this.event = event;
            this.status = status;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        addSpinner();

        initializeActionBar();

        initializeWebView();

        registerJockeyEvents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            setResult(homeButtonResultCode());
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        jockey.off(USER_READY_EVENT_IDENTIFIER);
        jockey.off(USER_ERROR_EVENT_IDENTIFIER);

        super.onDestroy();
    }

    protected abstract int homeButtonResultCode();

    protected abstract void handleUserReadyEvent(Map<Object, Object> payload);

    protected void handleUserErrorEvent() {
        setResult(RESULT_ERROR);
        finish();
    }

    protected WebView getWebView() {
        if (webView == null) {
            webView = (WebView) findViewById(R.id.webView);
        }

        return webView;
    }

    private void initializeWebView() {
        WebView webView = getWebView();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.setWebViewClient(webViewClient = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressDialog.dismiss();

                setResult(RESULT_ERROR);
                finish();
            }
        });
    }

    private void addSpinner() {
        progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setMessage(getString(R.string.LOADING_SPINNER));
        progressDialog.show();
    }

    private void initializeActionBar() {
        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void registerJockeyEvents() {
        jockey = JockeyImpl.getDefault();
        jockey.configure(getWebView());
        jockey.setWebViewClient(webViewClient);

        jockey.on(USER_READY_EVENT_IDENTIFIER, new JockeyHandler() {
            @Override
            protected void doPerform(final Map<Object, Object> payload) {
                Handler handler = new Handler();
                Runnable r=new Runnable() {
                    public void run() {
                        handleUserReadyEvent(payload);
                    }
                };
                handler.postDelayed(r, 1000);

                jockey.send(USER_READY_RESPONSE_EVENT_IDENTIFIER,
                            getWebView(),
                            new UserReadyResponsePayload(payload.get("event").toString(), "success"));
            }
        });

        jockey.on(USER_ERROR_EVENT_IDENTIFIER, new JockeyHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                Handler handler = new Handler();
                Runnable r=new Runnable() {
                    public void run() {
                        handleUserErrorEvent();
                    }
                };
                handler.postDelayed(r, 1000);
            }
        });
    }
}
