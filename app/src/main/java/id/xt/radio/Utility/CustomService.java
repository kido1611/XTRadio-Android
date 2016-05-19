package id.xt.radio.Utility;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Kido1611 on 08-May-16.
 */
public interface CustomService {
    @GET("/1.1/users/show.json")
    void show(@Query("user_id") long id, Callback<User> cb);

    @GET("/1.1/users/show.json")
    void show(@Query("screen_name") String screenName, Callback<User> cb);

}
