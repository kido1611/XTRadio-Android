package id.xt.radio.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.xt.radio.LoginActivity;
import id.xt.radio.MainActivity;
import id.xt.radio.R;
import id.xt.radio.Utility.TwitterAPI;
import id.xt.radio.Utility.telephony.SimUtils;
import id.xt.radio.Utility.telephony.TelephonyInfo;
import id.xt.radio.XTApplication;
import twitter4j.DirectMessage;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Kido1611 on 07-May-16.
 */
public class FragmentRequest extends BaseFragment {

    public FragmentRequest(){

    }

    @BindView(R.id.btn_req_facebook)
    Button mBtnFacebook;

    @BindView(R.id.btn_req_sms)
    Button mBtnSMS;

    @BindView(R.id.btn_req_twitter)
    Button mBtnTwitter;

    @BindView(R.id.layout_input_message)
    TextInputLayout mTextLayoutMessage;

    @BindView(R.id.input_message)
    TextInputEditText mTextMessage;

//    @BindView(R.id.btn_req_sms_2)
//    Button mBtnSMS2;

    private TwitterSession session;
    private Profile mProfile;

    String twitterTo = "XT_FM";

    private com.twitter.sdk.android.core.models.User mCurrentUser = null;

//    private TelephonyInfo mTelephonyInfo = null;
//    private SmsManager mSmsManager = null;
	String mPhoneNumber = "081327760606";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mTelephonyInfo = TelephonyInfo.getInstance(getActivity());
//        mSmsManager = SmsManager.getDefault();

        session = Twitter.getSessionManager().getActiveSession();
        mProfile = Profile.getCurrentProfile();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);

        ButterKnife.bind(this, rootView);

        mTextMessage.addTextChangedListener(new MyPostWatcher(mTextMessage));

        mBtnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(mProfile==null || AccessToken.getCurrentAccessToken()==null){
//                    Intent i = new Intent(getActivity(), LoginActivity.class);
//                    getActivity().startActivity(i);
//                }else{
//                    sendRequest(1);
//                    //postFb("Hello world");
//                }
                showSnackBar(R.string.under_construction);
            }
        });
        mBtnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(3);
            }
        });
//        mBtnSMS2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendRequest(4);
//            }
//        });
        mBtnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(session==null){
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(i);
                }else{
                    sendRequest(2);
                    //postTwitter(mTextMessage.getText().toString(), false);
                }
            }
        });

        if(mProfile!=null && AccessToken.getCurrentAccessToken()!=null){
            mBtnFacebook.setText("{faw-facebook} "+mProfile.getName());
        }

        if(session!=null){
            final String username = session.getUserName();
            mBtnTwitter.setText("{faw-twitter} @"+username);

            mCurrentUser = ((MainActivity)getActivity()).mCurrentUser;

        }
        return rootView;
    }

    /**
     *
     * @param from
     * 1 = facebook
     * 2 = twitter
     * 3 = sms
     */
    private void sendRequest(int from){
        if(!validateText()){
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        showSnackBar(R.string.sending, Snackbar.LENGTH_INDEFINITE);

        String message = mTextMessage.getText().toString();

        if(from==1){
            showSnackBar(R.string.under_construction);
            //postFb(message);
        }else if(from==2){
            postTwitter(message);
        }else if(from==3){
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setType("vnd.android-dir/mms-sms");

            i.putExtra("address", mPhoneNumber);
            i.putExtra("sms_body", message);

            startActivity(i);
            messageInfo(true);
        }
//        else if(from==3){
//            if(mTelephonyInfo.isDualSIM() && Build.VERSION.SDK_INT < 21 )
//                new sendSMSDualSIM().execute(new SMSDualInfo(0, message, mPhoneNumber));
//            else
//                new sendSMS().execute(mPhoneNumber, message);
//
//            //showSnackBar("Under construction");
//        }else if(from==4){
//            new sendSMSDualSIM().execute(new SMSDualInfo(1, message, mPhoneNumber));
//        }
        else{
            showSnackBar(R.string.error);
        }

    }

    private void postTwitter(String message){

        if(mCurrentUser==null){
            messageInfo(false);
            return;
        }

        if(mCurrentUser.protectedUser){
            showSnackBar(R.string.warning_sending_twitter_protected, Snackbar.LENGTH_INDEFINITE);
            new sendDM().execute(message);
        }else{
            Twitter.getApiClient().getStatusesService().update("@"+twitterTo+" "+message, null, false, null, null, null, false, true, null, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    messageInfo(true);
                }

                @Override
                public void failure(TwitterException exception) {
                    messageInfo(false);
                }
            });
        }
    }

    private void postFb(String message){
        Bundle params = new Bundle();
        params.putString("message", message);
        //new GraphRequest(AccessToken.getCurrentAccessToken(), "/171807839549642/feed", params, HttpMethod.POST, new GraphRequest.Callback(){
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/1551105098435552/feed", params, HttpMethod.POST, new GraphRequest.Callback(){
            @Override
            public void onCompleted(GraphResponse response) {
                messageInfo(response.getError()==null);
            }
        }).executeAsync();
    }

    private void messageInfo(boolean succcess){
        if(succcess)
            showSnackBar(R.string.success);
        else
            showSnackBar(R.string.failure);
    }

    class sendDM extends AsyncTask<String, Void, DirectMessage>{

        @Override
        protected DirectMessage doInBackground(String... params) {
            if(params.length<1) return null;

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(XTApplication.TWITTER_KEY);
            builder.setOAuthConsumerSecret(XTApplication.TWITTER_SECRET);
            // Access Token
            String access_token = Twitter.getSessionManager().getActiveSession().getAuthToken().token;
            // Access Token Secret
            String access_token_secret =Twitter.getSessionManager().getActiveSession().getAuthToken().secret;

            twitter4j.auth.AccessToken accessToken = new twitter4j.auth.AccessToken(access_token, access_token_secret);
            twitter4j.Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

            try {
                twitter4j.DirectMessage response = twitter.sendDirectMessage(twitterTo, params[0]);
                return response;
            } catch (TwitterException e) {
                e.printStackTrace();
            } catch (twitter4j.TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DirectMessage directMessage) {
            messageInfo(!(directMessage==null));
        }
    }

//    class sendSMS extends AsyncTask<String, Integer, Void>{
//
//
//		// String... params == params[] // array
//		@Override
//		protected Void doInBackground(String... params) {
//			// TODO Auto-generated method stub
//			// Send sms, (no hp, ...., pesan, ...., ....)
//			mSmsManager.sendTextMessage(params[0], null, params[1], null, null);
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//            showSnackBar(R.string.sms_request_sent);
//		}
//
//	}
//
//	class sendSMSDualSIM extends AsyncTask<SMSDualInfo, Integer, Void>{
//
//
//		// String... params == params[] // array
//		@Override
//		protected Void doInBackground(SMSDualInfo... params) {
//			// TODO Auto-generated method stub
//			// Send sms, (no hp, ...., pesan, ...., ....)
//			SimUtils.sendSMS(getActivity(), params[0].getSIMId(), params[0].getPhoneNumber(), null, params[0].getMessage(), null, null);
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//            showSnackBar(R.string.sms_request_sent);
//		}
//
//	}
//
//	class SMSDualInfo{
//		int simID;
//		String message;
//		String phoneNumber;
//
//		public SMSDualInfo(int simId, String message, String phoneNumber){
//			this.simID = simId;
//			this.message = message;
//			this.phoneNumber = phoneNumber;
//		}
//
//		public String getMessage(){
//			return this.message;
//		}
//
//		public String getPhoneNumber(){
//			return this.phoneNumber;
//		}
//
//		public int getSIMId(){
//			return this.simID;
//		}
//	}

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateText(){
        if(mTextMessage.getText().toString().isEmpty()){
            mTextLayoutMessage.setError(getString(R.string.hint_error_empty));
            requestFocus(mTextMessage);
            return false;
        }else{
            mTextLayoutMessage.setErrorEnabled(false);
        }
        return true;
    }

    private class MyPostWatcher implements TextWatcher {

        private View mView;

        private MyPostWatcher(View view){
            mView = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(mView.getId()==R.id.input_message){
                validateText();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
    }
}
