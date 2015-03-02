package com.mobilemakers.juansoler.appradar;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Sound management (initialization, load, play...)
 */
public class SoundManager {
    public final static int NUMBER_OF_ALERTS = 3;

    private static SoundManager privateInstance = null;

    private static HashMap<String, Integer> mSoundsMap;

    private SoundPool mSoundPool;
    private Context mContext;
    private boolean mLoaded;
    private float mVolume;

    private int [] soundIdAlert;
    private boolean [] soundSet;

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

    private void loadSounds() {
        mSoundsMap = new HashMap<>();
        mSoundsMap.put(mContext.getString(R.string.sub_klaxon), R.raw.sub_klaxon);
        mSoundsMap.put(mContext.getString(R.string.factory), R.raw.factory);
        mSoundsMap.put(mContext.getString(R.string.air_horn), R.raw.air_horn);
        mSoundsMap.put(mContext.getString(R.string.beep_ping), R.raw.beep_ping);
    }

    private void privSetAlertSound(int alert, int resource) {
        mSoundPool.unload(soundIdAlert[alert]);

        soundIdAlert[alert] = mSoundPool.load(mContext, resource, 1);
        soundSet[alert] = true;
    }

    public void setAlertSound(int alert, String name) throws SoundNotFoundException {
        if (!mSoundsMap.containsKey(name)) {
            throw new SoundNotFoundException();
        }
        privateInstance.privSetAlertSound(alert, mSoundsMap.get(name));
    }

    private SoundManager(Context context) {
        mContext = context;
        setVolume(context);

        soundIdAlert = new int[NUMBER_OF_ALERTS];
        soundSet = new boolean[NUMBER_OF_ALERTS];
        for (int counter = 0; counter < NUMBER_OF_ALERTS; counter++) {
            soundSet[counter] = false;
        }

        setSoundPool();
        loadSounds();
    }

    private void privPlayAlert(int alert) throws SoundNotSetException {
        if (mLoaded) {
            if (!soundSet[alert]) {
                throw new SoundNotSetException();
            }
            mSoundPool.play(soundIdAlert[alert], mVolume, mVolume, 10, 0, 1.0f);
        }
        else {
            playAlertTone();
        }
    }

    public static void playAlert(int alert) throws SoundManagerNotInitializedException,
        SoundNotSetException {
        if (privateInstance == null) {
            throw new SoundManagerNotInitializedException();
        }
        privateInstance.privPlayAlert(alert);
    }

    public static void init(Context context) {
        privateInstance = new SoundManager(context);
        // TODO: Set sounds from preferences.
    }

    /**
     * Generated tone (used if the ogg sound wasn't loaded yet - unlikely (I guess))
     *
     * Based on the submarine klaxon.
     */
    public void playAlertTone() {
        playTone(440, 0.8);
        playTone(0, 0.2);
        playTone(440, 0.8);
        playTone(0, 0.2);
        playTone(440, 0.8);
        playTone(0, 0.2);
        playTone(440, 0.8);
    }

    public void playTone(double freqOfTone, double duration) {
        //double duration = 1000;                // seconds
        //   double freqOfTone = 1000;           // hz
        int sampleRate = 8000;              // a number

        double dnumSamples = duration * sampleRate;
        dnumSamples = Math.ceil(dnumSamples);
        int numSamples = (int) dnumSamples;
        double sample[] = new double[numSamples];
        byte generatedSnd[] = new byte[2 * numSamples];


        for (int i = 0; i < numSamples; ++i) {      // Fill the sample array
            sample[i] = Math.sin(freqOfTone * 2 * Math.PI * i / (sampleRate));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        int i = 0 ;

        int ramp = numSamples / 20 ;                                    // Amplitude ramp as a percent of sample count


        for (i = 0; i< ramp; ++i) {                                     // Ramp amplitude up (to avoid clicks)
            double dVal = sample[i];
            // Ramp up to maximum
            final short val = (short) ((dVal * 32767 * i/ramp));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }


        for (i = i; i< numSamples - ramp; ++i) {                        // Max amplitude for most of the samples
            double dVal = sample[i];
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (i = i; i< numSamples; ++i) {                               // Ramp amplitude down
            double dVal = sample[i];
            // Ramp down to zero
            final short val = (short) ((dVal * 32767 * (numSamples-i)/ramp ));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        AudioTrack audioTrack = null;                                   // Get audio track
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                AudioTrack.MODE_STREAM);
        audioTrack.play();                                          // Play the track
        audioTrack.write(generatedSnd, 0, generatedSnd.length);     // Load the track
        audioTrack.release();           // Track play done. Release track.
    }

    /**
     * SoundManager exception superclass.
     *
     * Created by ariel.cattaneo on 27/02/2015.
     */
    public static class SoundManagerException extends Exception {
    }

    /**
     * SoundManager exception: it wasn't initialized.
     *
     * Created by ariel.cattaneo on 27/02/2015.
     */
    public static class SoundManagerNotInitializedException extends SoundManagerException {
        @Override
        public String getMessage() {
            return "The SoundManager object wasn't initialized.\nYou have to call SoundManager.init(context) before using it.";
        }
    }

    /**
     * SoundManager exception: the sound file wasn't found.
     *
     * Created by ariel.cattaneo on 27/02/2015.
     */
    public static class SoundNotFoundException extends SoundManagerException {
        @Override
        public String getMessage() {
            return "The sound file wasn't found.";
        }
    }

    /**
     * SoundManager exception: the sound wasn't set.
     *
     * Created by ariel.cattaneo on 27/02/2015.
     */
    public static class SoundNotSetException extends SoundManagerException {
        @Override
        public String getMessage() {
            return "The sound wasn't set.";
        }
    }
}
