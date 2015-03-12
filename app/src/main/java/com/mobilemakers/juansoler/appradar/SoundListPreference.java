package com.mobilemakers.juansoler.appradar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.ListPreference;

import android.support.annotation.NonNull;
import android.util.AttributeSet;


public class SoundListPreference extends ListPreference {

    private Context context;
    private int mClickedDialogEntryIndex = 0;
    private int mLastClickedDialogEntryIndex = 0;
    private CharSequence[] mEntryValues;
    private MediaPlayer mPlayer;

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
        String [] strings = context.getResources().getStringArray(R.array.sound_options);
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

        final String [] strings = getContext().getResources().getStringArray(R.array.sound_options);

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

                                if (value.equals(strings[0])) {
                                    mPlayer = MediaPlayer.create(getContext(), R.raw.sub_klaxon);
                                }
                                else if (value.equals(strings[1])) {
                                    mPlayer = MediaPlayer.create(getContext(), R.raw.factory);
                                }
                                else if (value.equals(strings[2])) {
                                    mPlayer = MediaPlayer.create(getContext(), R.raw.air_horn);
                                }
                                else if (value.equals(strings[3])) {
                                    mPlayer = MediaPlayer.create(getContext(), R.raw.beep_ping);
                                }
                                else if (value.equals(strings[4])) {
                                    mPlayer = MediaPlayer.create(getContext(), R.raw.smb_flagpole);
                                }
                                else if (value.equals(strings[5])) {
                                    mPlayer = MediaPlayer.create(getContext(), R.raw.smb_pipe);
                                }
                                else if (value.equals(strings[6])) {
                                    mPlayer = MediaPlayer.create(getContext(), R.raw.smb_vine);
                                }
                                else {
                                    mPlayer = MediaPlayer.create(getContext(), R.raw.smb_warning);
                                }

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

