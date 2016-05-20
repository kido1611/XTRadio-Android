package id.xt.radio.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flask.floatingactionmenu.FloatingActionButton;
import com.flask.floatingactionmenu.FloatingActionMenu;
import com.flask.floatingactionmenu.FloatingActionToggleButton;
import com.flask.floatingactionmenu.OnToggleListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.xt.radio.IRadioService;
import id.xt.radio.MainActivity;
import id.xt.radio.R;

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

//    @BindView(R.id.btn_playHQ)
//    Button mBtnPlayHQ;
//
//    @BindView(R.id.btn_playLQ)
//    Button mBtnPlayLQ;
//
//    @BindView(R.id.btn_stop)
//    Button mBtnStop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        ButterKnife.bind(this, rootView);

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
        mFabToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getService()!=null){
                    try {
                        if(getService().isPlaying()){
                            ((MainActivity)getActivity()).stopMusic();
                            mFabToggle.toggleOff();
                            mFabToggle.toggleOff();
                        }
//                        else{
//                            mFabToggle.toggle();
//                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//        mBtnPlayHQ.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((MainActivity)getActivity()).playMusic(true);
//            }
//        });
//        mBtnPlayLQ.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((MainActivity)getActivity()).playMusic(true);
//            }
//        });
//        mBtnStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((MainActivity)getActivity()).stopMusic();
//            }
//        });

        return rootView;
    }

    private void play(boolean high){
        mFabToggle.toggleOff();
        ((MainActivity)getActivity()).playMusic(high);
    }

    private IRadioService getService(){
        return ((MainActivity)getActivity()).getService();
    }
}
