package com.mobilemakers.juansoler.appradar;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Sound management (initialization, load, play...)
 *
 * Created by ariel.cattaneo on 25/02/2015.
 */
public class SoundManager {
    private static SoundManager privateInstance = null;

    private SoundPool mSoundPool;
    private boolean mLoaded;
    private float mVolume;

    private int soundIdAlert;

    private void setVolume(Context context) {
        float normalVolume = ((AudioManager)context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_ALARM);
        float maxVolume = ((AudioManager)context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_ALARM);

        mVolume = normalVolume / maxVolume;
    }

    private void setSoundPool() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mLoaded = true;
            }
        });
    }

    private void loadSounds(Context context) {
        soundIdAlert = mSoundPool.load(context, R.raw.alert, 1);
    }

    private SoundManager(Context context) {
        setVolume(context);

        setSoundPool();
        loadSounds(context);
    }

    private void privPlayAlert() {
        if (mLoaded) {
            mSoundPool.play(soundIdAlert, mVolume, mVolume, 10, 0, 1.0f);
        }
        // TODO: else - Unlikely: do something to alert the user without the alert sound loaded
    }

    public static void playAlert() {
        if (privateInstance != null) {
            privateInstance.privPlayAlert();
        }
    }

    public static void init(Context context) {
        privateInstance = new SoundManager(context);
    }
}
