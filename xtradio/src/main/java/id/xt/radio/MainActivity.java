package id.xt.radio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.crossfader.util.UIUtils;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.pheelicks.visualizer.VisualizerView;
import com.pheelicks.visualizer.renderer.TileBarGraphRenderer;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import id.xt.radio.Utility.CrossfadeWrapper;
import id.xt.radio.Utility.TwitterAPI;
import id.xt.radio.fragment.FragmentFacebook;
import id.xt.radio.fragment.FragmentJadwal;
import id.xt.radio.fragment.FragmentPlay;
import id.xt.radio.fragment.FragmentRequest;
import id.xt.radio.fragment.FragmentTwitter;
import id.xt.radio.fragment.FragmentWeb;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private Drawer mDrawer;
    private MiniDrawer mMiniDrawer;
    private Crossfader crossFader;
    private DrawerArrowDrawable drawerArrowDrawable;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.visualizer)
    VisualizerView mVisualzer;

    private AudioManager mAudioManager;

    IRadioService mService;
    private boolean mBound = false;

    public User mCurrentUser = null;

    public void addFBLogAnalytics(String message){
        AppEventsLogger.newLogger(this).logEvent(message);
    }

    View headerView;
    ImageView imageHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);

        AppEventsLogger.activateApp(this);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putCustomAttribute("XT", "Open Application"));
        addFBLogAnalytics("Open Application");

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        headerView = getLayoutInflater().inflate(R.layout.header, null, false);
        imageHeader = (ImageView) headerView.findViewById(R.id.headerImage);

        Picasso.with(this).load("http://kido1611.id/xt/allcrew.jpg").placeholder(R.drawable.header).into(imageHeader);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerArrowDrawable = new DrawerArrowDrawable(this);
        drawerArrowDrawable.setColor(Color.WHITE);
        getSupportActionBar().setHomeAsUpIndicator(drawerArrowDrawable);

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withHeader(headerView)
                //.withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_title_play).withIdentifier(1).withIcon(FontAwesome.Icon.faw_play),
                        new PrimaryDrawerItem().withName(R.string.drawer_title_jadwal).withIdentifier(2).withIcon(FontAwesome.Icon.faw_calendar),
                        new PrimaryDrawerItem().withName(R.string.drawer_title_request).withIdentifier(3).withIcon(FontAwesome.Icon.faw_envelope),
                        new SectionDrawerItem().withName(R.string.drawer_title_social_media),
                        new SecondaryDrawerItem().withName(R.string.drawer_title_facebook).withIdentifier(4).withIcon(FontAwesome.Icon.faw_facebook),
                        new SecondaryDrawerItem().withName(R.string.drawer_title_twitter).withIdentifier(5).withIcon(FontAwesome.Icon.faw_twitter)
                )
                .withGenerateMiniDrawer(true)
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        openDisplay(drawerItem);
                        return false;
                    }
                })
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.drawer_title_social).withIdentifier(6).withIcon(FontAwesome.Icon.faw_cog)
                )
                .buildView();
        mMiniDrawer = mDrawer.getMiniDrawer();
        int firstWidth = (int) UIUtils.convertDpToPixel(300, this);
        int secondWidth = (int) UIUtils.convertDpToPixel(72, this);

        crossFader = new Crossfader()
                .withContent(findViewById(R.id.drawer_container))
                .withFirst(mDrawer.getSlider(), firstWidth)
                .withSecond(mMiniDrawer.build(this), secondWidth)
                .withGmailStyleSwiping()
                .withSavedInstance(savedInstanceState)
                .build();
        crossFader.withPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset >= .995) {
                    //drawerArrowDrawable.setProgress(slideOffset);
                    drawerArrowDrawable.setVerticalMirror(true);
                } else if (slideOffset <= .005) {
                    drawerArrowDrawable.setVerticalMirror(false);
                }

                drawerArrowDrawable.setProgress(slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {

            }

            @Override
            public void onPanelClosed(View panel) {

            }
        });

        mMiniDrawer.withCrossFader(new CrossfadeWrapper(crossFader));
        crossFader.getCrossFadeSlidingPaneLayout().setShadowResourceLeft(R.drawable.material_drawer_shadow_left);

        mDrawer.setSelection(1, true);

        bindService();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(Twitter.getSessionManager().getActiveSession()!=null){
                    new TwitterAPI(Twitter.getSessionManager().getActiveSession()).getCustomService().show(Twitter.getSessionManager().getActiveSession().getUserName(), new Callback<com.twitter.sdk.android.core.models.User>() {
                        @Override
                        public void success(Result<com.twitter.sdk.android.core.models.User> result) {
                            mCurrentUser = result.data;
                        }

                        @Override
                        public void failure(com.twitter.sdk.android.core.TwitterException exception) {

                        }
                    });
                }

                setupVisualizer();
            }
        }).start();
    }

    private void setupVisualizer(){
        mAudioManager.isMusicActive();
        mVisualzer.link(0);

        Paint mVisualizerPaint;
        TileBarGraphRenderer mTileBar;

        mVisualizerPaint = new Paint();
        mVisualizerPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.visualizer_path_stroke_width));
        mVisualizerPaint.setAntiAlias(true);
        mVisualizerPaint.setColor(getResources().getColor(R.color.visualizer_fill_color));
        mVisualizerPaint.setPathEffect(new android.graphics.DashPathEffect(new float[]{
                getResources().getDimensionPixelSize(R.dimen.visualizer_path_effect_1),
                getResources().getDimensionPixelSize(R.dimen.visualizer_path_effect_2)
        }, 0));
        mTileBar = new TileBarGraphRenderer (
                getResources().getInteger(R.integer.visualizer_divisions),
                mVisualizerPaint,
                getResources().getInteger(R.integer.visualizer_db_fuzz),
                getResources().getInteger(R.integer.visualizer_db_fuzz_factor));

        mVisualzer.addRenderer(mTileBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppEventsLogger.deactivateApp(this);
        unBindService();
        mVisualzer.release();
    }

    private void openDisplay(IDrawerItem item){
        String appName = getString(R.string.app_name);
        String openTitle = ((Nameable)item).getName().getText(MainActivity.this);
        getSupportActionBar().setTitle(appName+" - "+openTitle);

        long identifier = item.getIdentifier();

        Fragment fragment = null;
        if(identifier==1){
            fragment = new FragmentPlay();
        }else if(identifier==2){
            //fragment = FragmentWeb.newInstance("http://kido-serv.hol.es/xt");
            fragment = new FragmentJadwal();
        }else if(identifier==4){
            if(Profile.getCurrentProfile()!=null && AccessToken.getCurrentAccessToken()!=null){
                fragment = new FragmentFacebook();
            }else
                fragment = FragmentWeb.newInstance("https://www.facebook.com/XTFM-1551105098435552/");
        }else if(identifier==5){
            if(mCurrentUser==null)
                fragment = FragmentWeb.newInstance("https://twitter.com/XT_FM");
            else{
                fragment = new FragmentTwitter();
            }
        }else if(identifier==3){
            fragment = new FragmentRequest();
        }else if(identifier==6){
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
//        else{
//            fragment = new MainActivityFragment();
//        }

        if(fragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    private void unBindService(){
        unbindService(mConnection);
        try {
            printLog("Play : "+mService.isPlaying()+" service start : "+mService.isServiceStart());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void printLog(String message){
        Log.v(getString(R.string.app_name), message);
    }

    public void updateMusic(){
        try {
            printLog("Play : "+mService.isPlaying()+" service start : "+mService.isServiceStart());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void playMusic(boolean high){
        if(mService!=null){
            try {
                if(!mService.isServiceStart()){
                    startMusicService();
                }
                if(!mService.isPlaying()) {
                    mService.startMusic(high);
                }else{
                    showSnackbar("Stop first");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putCustomAttribute("XT", "Play Music"));
            addFBLogAnalytics("Play Music");
        }
    }

    public IRadioService getService(){
        return mService;
    }

    public void stopMusic(){
        if(mService!=null){
            try {
                mService.stopMusic();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMusicService(){
        try {
            if(mService.isServiceStart()) return;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(this, XTService.class);
        i.setAction(XTService.class.getName());
        startService(i);
    }

    public void stopMusicService(){
        try {
            if(!mService.isServiceStart()) return;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(this, XTService.class);
        i.setAction(XTService.class.getName());
        stopService(i);
    }

    private void bindService(){
        if(mBound) return;
        Intent i = new Intent(this, XTService.class);
        i.setAction(XTService.class.getName());
        //start Service(i);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        //bindService(i, mConnection, 0);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = IRadioService.Stub.asInterface(iBinder);
            mBound = true;
            try {
                printLog("Play : "+mService.isPlaying()+" service start : "+mService.isServiceStart());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
            Toast.makeText(MainActivity.this, "Unbind service", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = mDrawer.saveInstanceState(outState);
        outState = crossFader.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if(crossFader.isCrossFaded() && crossFader!=null){
            crossFader.crossFade();
        }else{
            super.onBackPressed();
        }
    }

    public void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }
    public void showSnackbar(String message, int length){
        Snackbar.make(mCoordinatorLayout, message, length).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                crossFader.crossFade();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
