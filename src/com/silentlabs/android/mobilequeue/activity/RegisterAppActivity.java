
package com.silentlabs.android.mobilequeue.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.silentlabs.android.mobilequeue.R;
import com.silentlabs.android.mobilequeue.oauth.NetflixAccess;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.NetflixApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterAppActivity extends Activity {

    private final static String ACCESS = "MobileQueueAccess";
    private final static String callbackUrl = "nfcb:///";

    private static final Map<String, String> KEYS = new HashMap<String, String>() {
        private static final long serialVersionUID = 6900576601563996953L;

        {
            put("oauth_consumer_key", NetflixAccess.getConsumerKey());
        }
    };

    private WebView mWebView;

    private OAuthService mOAuthservice;
    private Token mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mWebView = (WebView) findViewById(R.id.webview);

        // ((TextView) findViewById(R.id.title_text)).setText(R.string.app_name);

        mOAuthservice = new ServiceBuilder()
        .provider(NetflixApi.class)
        .apiKey(NetflixAccess.getConsumerKey())
        .apiSecret(NetflixAccess.getConsumerSecret())
        .callback(callbackUrl)
        .build();

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        mWebView.clearCache(true);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Uri uri = Uri.parse(url);
                if (uri != null && uri.toString().startsWith(callbackUrl.substring(0, 4))) {
                    String verifier = uri.getQueryParameter("oauth_verifier"); // OAuth
                    // 1.0a
                    String token = uri.getQueryParameter("oauth_token"); // OAuth
                    // 1.0
                    if (verifier != null) {
                        getOAuthAccess(verifier);

                    } else if (token != null) {
                        getOAuthAccess(token);
                    }
                }
                return true;
            }
        });

        doOAuth();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void doOAuth() {
        new RequestRequestTokenTask().execute();
    }

    private void getOAuthAccess(final String verifierString) {
        new RequestAccessTokenTask().execute(verifierString);
    }

    private class RequestRequestTokenTask extends AsyncTask<Void, Void, Void> {

        private String message = null;

        @Override
        protected Void doInBackground(Void... parms) {
            try {
                mRequestToken = mOAuthservice.getRequestToken();

            } catch (NullPointerException e) {
                message = e.getMessage();
                cancel(true);

            } catch (RuntimeException e) {
                message = e.getMessage();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            String authUrl = null;
            if (mRequestToken != null) {
                authUrl = mOAuthservice.getAuthorizationUrl(mRequestToken, KEYS);
            } else {
                Toast.makeText(RegisterAppActivity.this, message, Toast.LENGTH_LONG).show();
            }

            if (authUrl != null) {
                mWebView.loadUrl(authUrl);
            }
        }
    }

    private class RequestAccessTokenTask extends AsyncTask<String, Void, Void> {

        private Token accessToken = null;
        private String message = null;

        @Override
        protected Void doInBackground(String... parms) {
            try {
                Verifier verifier = new Verifier(parms[0]);
                accessToken = mOAuthservice.getAccessToken(mRequestToken, verifier);

            } catch (RuntimeException e) {
                message = e.getMessage();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (accessToken != null) {

                // Create a pattern to match breaks
                Pattern p = Pattern.compile("user_id=([^&]+)");

                String accessKey = accessToken.getToken();
                String accessSecret = accessToken.getSecret();

                String accessRaw = accessToken.getRawResponse();
                Matcher matcher = p.matcher(accessRaw);
                String userId = null;
                if (matcher.find() && matcher.groupCount() >= 1) {
                    try {
                        userId = URLDecoder.decode(matcher.group(1), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                SharedPreferences access = getSharedPreferences(ACCESS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = access.edit();
                editor.putString("ACCESS_KEY", accessKey);
                editor.putString("ACCESS_SECRET", accessSecret);
                editor.putString("USER_ID", userId);
                editor.commit();

                finish();

            } else {
                Toast.makeText(RegisterAppActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
