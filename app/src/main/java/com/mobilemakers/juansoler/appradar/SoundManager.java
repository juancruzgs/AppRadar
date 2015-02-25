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
    private Context mContext;

    private SoundPool mSoundPool;
    private boolean mLoaded;
    private float mVolume;

    private int soundIdAlert;

    public SoundManager(Context context) {
        mContext = context;

        float normalVolume = ((AudioManager)context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamVolume(AudioManager.STREAM_ALARM);
        float maxVolume = ((AudioManager)context.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_ALARM);

        mVolume = normalVolume / maxVolume;

        mSoundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mLoaded = true;
            }
        });
        soundIdAlert = mSoundPool.load(context, R.raw.alert, 1);
    }

    public void playAlert() {
        if (mLoaded) {
            mSoundPool.play(soundIdAlert, mVolume, mVolume, 10, 0, 1.0f);
        }
        // TODO: else - Unlikely: do something to alert the user without the alert sound loaded
    }
}
