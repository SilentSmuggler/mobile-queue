
package com.silentlabs.android.mobilequeue.oauth;

public class NetflixAccess {

    // replace with correct key from netflix
    private static final String CONSUMER_KEY = "CONSUMER_KEY";
    // replace with correct secret from netflix
    private static final String CONSUMER_SECRET = "CONSUMER_SECRET";
    private static final String APPLICATION_NAME = "Mobile+Queue";

    private static final String REQUEST_TOKEN_URI = "http://api-public.netflix.com/oauth/request_token";
    private static final String ACCESS_TOKEN_URI = "http://api-public.netflix.com/oauth/access_token";
    private static final String AUTHORIZE_URI = "https://api-user.netflix.com/oauth/login";

    public static String getAccessTokenURL() {
        return ACCESS_TOKEN_URI;
    }

    public static String getApplicationName() {
        return APPLICATION_NAME;
    }

    public static String getAuthorizeURL() {
        return AUTHORIZE_URI;
    }

    public static String getConsumerKey() {
        return CONSUMER_KEY;
    }

    public static String getConsumerSecret() {
        return CONSUMER_SECRET;
    }

    public static String getRequestTokenURL() {
        return REQUEST_TOKEN_URI;
    }
}
