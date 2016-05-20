package id.xt.radio.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.xt.radio.R;
import id.xt.radio.Utility.JSONParser;
import id.xt.radio.model.FacebookFeed;
import id.xt.radio.model.FacebookFeedAdapter;
import id.xt.radio.model.FacebookFrom;
import id.xt.radio.model.FacebookLikes;
import id.xt.radio.widget.ClickListener;
import id.xt.radio.widget.RecyclerTouchListener;

/**
 * Created by Kido1611 on 11-May-16.
 */
public class FragmentFacebook extends BaseFragment {

    String pagingNext = null;
    String paginngBefore = null;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipyRefresh)
    SwipyRefreshLayout mSwipyRefresh;

    FacebookFeedAdapter mAdapter;
    private List<FacebookFeed> mItems = new ArrayList<>();

    private JSONParser mJSONParser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new FacebookFeedAdapter(getActivity(), mItems);

        mJSONParser = new JSONParser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_facebook_timeline, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                openFacebookFeed(mItems.get(position).getId());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        mSwipyRefresh.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(direction==SwipyRefreshLayoutDirection.BOTTOM){
                    loadNextMessage();
                }
            }
        });

        loadMessage();

        return rootView;
    }

    private void openFacebookFeed(String id){
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://facebook.com/"+ id));
        startActivity(intent);
    }

    private void loadMessage(){
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,created_time,message,type,shares,likes,id,picture,full_picture,link,caption,description,updated_time,application,from");
        parameters.putString("limit", "15");
        mItems.clear();
        mAdapter.notifyDataSetChanged();

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/1551105098435552/feed/", parameters, HttpMethod.GET,
            new GraphRequest.Callback(){

                @Override
                public void onCompleted(GraphResponse response) {
                    try {
                        if(response==null) return;
                        JSONObject responseObject = response.getJSONObject();
                        if(responseObject==null) return;
                        JSONArray objectArray = responseObject.getJSONArray("data");
                        for(int i=0;i<objectArray.length();i++){
                            addFacebookFeed(objectArray.getJSONObject(i));
                        }
                        if(objectArray.length()>0)
                            fetchPaging(responseObject.getJSONObject("paging"));

                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).executeAsync();
    }

    private void loadNextMessage(){
        new loadNextFeed().execute();
    }

    class loadNextFeed extends AsyncTask<Void, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipyRefresh.setRefreshing(true);
        }

        @Override
        protected JSONObject doInBackground(Void... strings) {
            if(pagingNext==null || pagingNext.isEmpty() || pagingNext.equals("")){
                return null;
            }

            Uri buildUri = Uri.parse(pagingNext).buildUpon().build();

            String jsonString = mJSONParser.makeHTTPRequest(buildUri, "GET");

            try {
                JSONObject obj = new JSONObject(jsonString);
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject responseObject) {
            mSwipyRefresh.setRefreshing(false);
            if(responseObject==null){
                return;
            }
            JSONArray objectArray = null;
            try {
                objectArray = responseObject.getJSONArray("data");
                for(int i=0;i<objectArray.length();i++){
                    addFacebookFeed(objectArray.getJSONObject(i));
                }

                if(objectArray.length()>0)
                    fetchPaging(responseObject.getJSONObject("paging"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    private void fetchPaging(JSONObject object){
        try {
            pagingNext = object.getString("next");
            paginngBefore = object.getString("previous");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addFacebookFeed(JSONObject object){
        FacebookFeed feed = new FacebookFeed();

        FacebookFrom from = new FacebookFrom();
        Log.v("Facebook XT", object.toString());

        try {
            if(object.has("from")) {
                JSONObject fromObject = object.getJSONObject("from");
                from.setId(fromObject.getString("id"));
                from.setName(fromObject.getString("name"));
                feed.setFrom(from);
            }

            if(object.has("created_time"))
                feed.setCreated_time(object.getString("created_time"));

            if(object.has("message"))
                feed.setMessage(object.getString("message"));

            if(object.has("type"))
                feed.setType(object.getString("type"));

            if(object.has("id"))
                feed.setId(object.getString("id"));

            if(object.has("updated_time"))
                feed.setUpdate_time(object.getString("updated_time"));

            if(object.has("likes")){
                JSONObject likesObject = object.getJSONObject("likes");
                JSONArray dataLikes = likesObject.getJSONArray("data");
                feed.getLikes().clear();
                for(int i=0;i<dataLikes.length();i++){
                    JSONObject likeItem = dataLikes.getJSONObject(i);

                    FacebookLikes like = new FacebookLikes();
                    like.setId(likeItem.getString("id"));
                    like.setName(likeItem.getString("name"));

                    feed.addLikes(like);
                }
            }

            if(object.has("name"))
                feed.setName(object.getString("name"));

            if(object.has("picture"))
                feed.setPicture(object.getString("picture"));
            if(object.has("full_picture"))
                feed.setFull_picture(object.getString("full_picture"));
            if(object.has("link"))
                feed.setLink(object.getString("link"));
            if(object.has("caption"))
                feed.setCaption(object.getString("caption"));
            if(object.has("shares")){
                JSONObject shareObject = object.getJSONObject("shares");
                feed.setShares(shareObject.getInt("count"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mItems.add(feed);
    }
}
