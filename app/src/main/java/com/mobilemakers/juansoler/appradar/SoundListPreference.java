package com.mobilemakers.juansoler.appradar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import java.util.HashMap;


public class SoundListPreference extends ListPreference {

    private Context context;
    private int mClickedDialogEntryIndex = 0;
    private int mLastClickedDialogEntryIndex = 0;
    private CharSequence[] mEntryValues;
    private MediaPlayer mPlayer;

    static private HashMap<String, Integer> mSoundMap = null;

    static private void initSoundMap() {
        mSoundMap = new HashMap<>();
        mSoundMap.put("sub_klaxon", R.raw.sub_klaxon);
        mSoundMap.put("factory", R.raw.factory);
        mSoundMap.put("air_horn", R.raw.air_horn);
        mSoundMap.put("beep_ping", R.raw.beep_ping);
        mSoundMap.put("smb_flagpole", R.raw.smb_flagpole);
        mSoundMap.put("smb_pipe", R.raw.smb_pipe);
        mSoundMap.put("smb_vine", R.raw.smb_vine);
        mSoundMap.put("smb_warning", R.raw.smb_warning);
    }

    static public int getSound(String name) {
        if (mSoundMap == null) {
            initSoundMap();
        }
        return mSoundMap.get(name);
    }

    public SoundListPreference(final Context context) {
        super(context);
        this.context = context;
    }

    public SoundListPreference(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String temp = restoreValue ? getPersistedString("") : (String)defaultValue;

        if(!restoreValue)
            persistString(temp);

        boolean detected = false;
        int pos = 0;
        String [] strings = context.getResources().getStringArray(R.array.sound_values);
        while(!detected && pos < strings.length) {
            if (temp.equals(strings[pos])) {
                detected = true;
            } else {
                pos++;
            }
        }
        if (!detected) {
            pos = strings.length-1;
        }
        mClickedDialogEntryIndex = pos;

        mLastClickedDialogEntryIndex = mClickedDialogEntryIndex;
    }


    @Override
    protected void onPrepareDialogBuilder(@NonNull AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        CharSequence[] entries = getEntries();
        mEntryValues = getEntryValues();

        final String [] strings = getContext().getResources().getStringArray(R.array.sound_values);

        builder
                .setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLastClickedDialogEntryIndex = mClickedDialogEntryIndex;
                        SharedPreferences.Editor editor =  getEditor();
                        String selectedSound = strings[mClickedDialogEntryIndex];

                        editor.putString(getKey(), selectedSound);
                        editor.commit();
                    }
                })
                .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mClickedDialogEntryIndex = mLastClickedDialogEntryIndex;
                    }
                })
                .setSingleChoiceItems(entries, mClickedDialogEntryIndex ,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                mClickedDialogEntryIndex = which;
                                String value = mEntryValues[which].toString();

                                mPlayer = MediaPlayer.create(getContext(), SoundListPreference.getSound(value));

                                mPlayer.start();
                            }
                        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult && mClickedDialogEntryIndex >= 0 && mEntryValues != null) {
            String value = mEntryValues[mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }

        if (mPlayer != null) {
            mPlayer.release();
        }
    }

}

