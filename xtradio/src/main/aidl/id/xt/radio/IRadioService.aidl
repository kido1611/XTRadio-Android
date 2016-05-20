// IRadioService.aidl
package id.xt.radio;

import id.xt.radio.IRadioServiceCallback;

interface IRadioService {
    boolean isPlaying();
    boolean isPlayHigh();
    boolean isServiceStart();

    void startMusic(boolean high);
    void stopMusic();
    void registerCallback(IRadioServiceCallback cb);
}
