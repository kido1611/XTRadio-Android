package id.xt.radio.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.xt.radio.R;

/**
 * Created by Kido1611 on 3/24/2016.
 */
public class FragmentWeb extends BaseFragment {

    public static FragmentWeb newInstance(String url) {

        Bundle args = new Bundle();
        args.putString("url", url);
        FragmentWeb fragment = new FragmentWeb();
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentWeb(){

    }

    @BindView(R.id.webview)
    WebView mWebView;
    //private SwipeRefreshLayout mSwipeRefresh;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web, container, false);

        ButterKnife.bind(this, rootView);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(R.string.app_name);
        mProgressDialog.setMessage(getActivity().getResources().getString(R.string.connecting));
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                mWebView.stopLoading();
            }
        });

        mWebView.setWebViewClient(new XTWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.loadUrl(getArguments().getString("url"));
        return rootView;
    }

    class XTWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            //mSwipeRefresh.setRefreshing(true);
//			if(!Utils.isJaringanOK(getActivity())){
//				mSwipeRefresh.setRefreshing(false);
//				return;
//			}
            mProgressDialog.show();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            //mSwipeRefresh.setRefreshing(false);
            mProgressDialog.dismiss();
        }
    }
}
