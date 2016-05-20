package id.xt.radio;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Kido1611 on 08-May-16.
 */
public class XTApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static final String TWITTER_KEY = "hF2fUtzDKPpZ4oOGvy93yagMc";
    public static final String TWITTER_SECRET = "T1SPAIbjULFxzEIQW6K6XbRecSG84OvwsIHZ575ssTd8z3sCQP";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
