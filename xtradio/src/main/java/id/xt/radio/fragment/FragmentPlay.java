package id.xt.radio.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flask.floatingactionmenu.FloatingActionButton;
import com.flask.floatingactionmenu.FloatingActionMenu;
import com.flask.floatingactionmenu.FloatingActionToggleButton;
import com.flask.floatingactionmenu.OnToggleListener;
import com.flask.floatingactionmenu.RevealBackgroundView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.xt.radio.IRadioService;
import id.xt.radio.IRadioServiceCallback;
import id.xt.radio.MainActivity;
import id.xt.radio.R;
import id.xt.radio.XTService;

/**
 * Created by Kido1611 on 07-May-16.
 */
public class FragmentPlay extends BaseFragment {

    public FragmentPlay(){

    }

    @BindView(R.id.playLQ)
    FloatingActionButton mPlayLQ;

    @BindView(R.id.playHQ)
    FloatingActionButton mPlayHQ;

    @BindView(R.id.fab_toggle)
    FloatingActionToggleButton mFabToggle;

    @BindView(R.id.play_menu)
    FloatingActionMenu mFabMenu;

    @BindView(R.id.fab_fading)
    RevealBackgroundView mFABFading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        ButterKnife.bind(this, rootView);


        mFabMenu.setFadingBackgroundView(mFABFading);
        mPlayLQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(false);
            }
        });
        mPlayHQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(true);
            }
        });
        mFabToggle.cancelDefaultToggleBehavior();
        mFabToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getService()!=null){
                    try {
                        if(getService().isPlaying()){
                            ((MainActivity)getActivity()).stopMusic();
                        }else{
                            mFabToggle.toggle();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        updateState();

        IntentFilter intentFilter = new IntentFilter(XTService.XT_INTENT_STATE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    int state;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(XTService.XT_INTENT_STATE)){
                state = intent.getIntExtra(XTService.XT_STATE, -1);
                if(state==XTService.MP_STATE_BUFFER){
                    mFabToggle.setEnabled(false);
                }else
                    mFabToggle.setEnabled(true);

                updateState();
            }
        }
    };

    private void updateState(){
        if(getService()!=null){
            try {
                if(getService().isPlaying()){
                    mFabToggle.setToggleIconDrawable(new IconicsDrawable(getActivity())
                            .icon(FontAwesome.Icon.faw_stop)
                            .color(Color.WHITE)
                            .sizeDp(36));
                }else{
                    mFabToggle.setToggleIconDrawable(new IconicsDrawable(getActivity())
                            .icon(FontAwesome.Icon.faw_play)
                            .color(Color.WHITE)
                            .sizeDp(36));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            mFabToggle.setToggleIconDrawable(new IconicsDrawable(getActivity())
                    .icon(FontAwesome.Icon.faw_play)
                    .color(Color.WHITE)
                    .sizeDp(36));
        }
    }

    private void play(boolean high){
        mFabToggle.toggleOff();
        ((MainActivity)getActivity()).playMusic(high);
    }

    private IRadioService getService(){
        return ((MainActivity)getActivity()).getService();
    }

}
