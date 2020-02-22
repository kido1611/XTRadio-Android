// IRadioService.aidl
package com.xtradio.android.aidl;

// Declare any non-default types here with import statements
import com.xtradio.android.aidl.IRadioServiceCallback;

interface IRadioService {
    boolean isPlaying();

    void startRadio();
    void stopRadio();
    void registerCallback(IRadioServiceCallback cb);
}
