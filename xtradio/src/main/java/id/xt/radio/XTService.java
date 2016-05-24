package id.xt.radio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

/**
 * Created by Kido1611 on 09-May-16.
 */
public class XTService extends Service
        implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener{

    private boolean play = false;
    private boolean playHigh = false;
    private boolean serviceStart = false;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotif;

    public static String XT_INTENT_STATE = "id.xt.radio.STATE";

    public static int currentState = 2;
    public static String XT_STATE = "state";
    public static int MP_STATE_BUFFER = 0;
    public static int MP_STATE_PLAY = 1;
    public static int MP_STATE_STOP = 2;
    public static int MP_STATE_ERROR = 3;
    public static int MP_STATE_SERVICE_START = 4;
    public static int MP_STATE_SERVICE_STOP = 5;
    public static int MP_STATE_COMPLETE = 6;

    public static final String HIGH_URL = "http://112.78.45.66:8000/xtfmhigh";
    public static final String LOW_URL = "http://93.188.162.189:8000/xtfmtesting";

    private IRadioServiceCallback mCb;

    private MediaPlayer mMediaPlayer;
    private WifiManager.WifiLock wifiLock;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotif = new NotificationCompat.Builder(this);

        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotif.setContentIntent(resultPendingIntent);
        mNotif.setContentTitle(getString(R.string.app_name));
        mNotif.setSmallIcon(R.mipmap.ic_launcher);

        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceStart = true;
        //sendState(MP_STATE_SERVICE_START);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceStart = false;
        //sendState(MP_STATE_SERVICE_STOP);

        if(mMediaPlayer!=null) {
            //mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void startRadio(boolean high){
        playHigh = high;
        sendState(MP_STATE_BUFFER);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setDataSource(high?HIGH_URL:LOW_URL);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IRadioService.Stub mBinder = new IRadioService.Stub(){

        @Override
        public boolean isPlaying() throws RemoteException {
            return play || mMediaPlayer!=null;
        }

        @Override
        public boolean isPlayHigh() throws RemoteException {
            return playHigh;
        }

        @Override
        public boolean isServiceStart() throws RemoteException {
            return serviceStart;
        }

        @Override
        public void startMusic(boolean high) throws RemoteException {
            startRadio(high);
        }

        @Override
        public void stopMusic() throws RemoteException {
            if(mMediaPlayer!=null){
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;

                sendState(MP_STATE_STOP);
            }
            if(wifiLock.isHeld())
                wifiLock.release();
        }

        @Override
        public void registerCallback(IRadioServiceCallback cb) throws RemoteException {
            mCb = cb;
        }
    };

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        sendState(MP_STATE_ERROR);
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //play = true;
        mMediaPlayer.start();
        wifiLock.acquire();
        sendState(MP_STATE_PLAY);
    }

    private void sendState(int state){
        String notifMessage = getString(R.string.app_name);

        if(state==MP_STATE_PLAY) play = true;
        else play = false;

        if(state==MP_STATE_BUFFER) notifMessage="Buffering";
        else if(state==MP_STATE_STOP) notifMessage="Stop";
        else if(state==MP_STATE_PLAY) notifMessage="Play";
        else if(state==MP_STATE_ERROR) notifMessage="Error";
        else if(state == MP_STATE_SERVICE_START) notifMessage="SERVICE START";
        else if(state == MP_STATE_SERVICE_STOP) notifMessage="SERVICE STOP";
        else if(state == MP_STATE_COMPLETE) notifMessage="COMPLETE. Need re run";

        mNotif.setContentText(notifMessage);
        mNotif.setWhen(System.currentTimeMillis());

        Notification notif = mNotif.build();

        if(state==MP_STATE_PLAY || state == MP_STATE_BUFFER){
            notif.flags = Notification.FLAG_NO_CLEAR;
        }else
            notif.flags = Notification.FLAG_AUTO_CANCEL;

        if(state==MP_STATE_PLAY || state == MP_STATE_BUFFER) {
            mNotificationManager.cancel(1968);
            startForeground(1968, notif);
        }else {
            stopForeground(true);
            mNotificationManager.notify(1968, notif);
        }

        try {
            if(mCb!=null)
                mCb.stateChange();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(XT_INTENT_STATE);
        i.setAction(XT_INTENT_STATE);
        i.putExtra(XT_STATE, state);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;

        if(wifiLock.isHeld())
            wifiLock.release();

        sendState(MP_STATE_COMPLETE);

        startRadio(playHigh);

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }
}
