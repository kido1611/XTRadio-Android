package id.xt.radio.Utility;

import android.content.Context;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Kido1611 on 08-May-16.
 */
public class TwitterAPI extends TwitterApiClient {
    /**
     * Must be instantiated after {@link TwitterCore} has been
     * initialized via {@link Fabric#with(Context, Kit[])}.
     *
     * @param session Session to be used to create the API calls.
     * @throws IllegalArgumentException if TwitterSession argument is null
     */
    public TwitterAPI(Session session) {
        super(session);
    }

    public CustomService getCustomService() {
        return getService(CustomService.class);
    }

    // example users/show service endpoint

}
