package com.mobilemakers.juansoler.appradar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.preference.ListPreference;

import android.preference.PreferenceManager;
import android.util.AttributeSet;


/**
 * Created by paula.baudo on 3/2/2015.
 */
public class SoundListPreference extends ListPreference {


    private Context context;
    private int mClickedDialogEntryIndex = 0;
    private int mLastClickedDialogEntryIndex = 0;
    private CharSequence[] mEntries;
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

        switch (temp){
            case "Bocina de submarino":
                mClickedDialogEntryIndex = 0;
                break;
            case "Fábrica":
                mClickedDialogEntryIndex = 1;
                break;
            case "Sirena de aire":
                mClickedDialogEntryIndex = 2;
                break;
            default:
                mClickedDialogEntryIndex = 3;
                break;
        }

        mLastClickedDialogEntryIndex = mClickedDialogEntryIndex;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        mEntries = getEntries();
        mEntryValues = getEntryValues();

        builder
                .setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLastClickedDialogEntryIndex = mClickedDialogEntryIndex;
                        SharedPreferences.Editor editor =  getEditor();
                        String selectedSound= "";

                        switch (mClickedDialogEntryIndex){
                            case 0:
                                selectedSound = getContext().getString(R.string.sub_klaxon);
                                break;
                            case 1:
                                selectedSound = getContext().getString(R.string.factory);
                                break;
                            case 2:
                                selectedSound = getContext().getString(R.string.air_horn);
                                break;
                            default:
                                selectedSound = getContext().getString(R.string.beep_ping);
                                break;
                        }

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
                .setSingleChoiceItems(mEntries, mClickedDialogEntryIndex ,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                mClickedDialogEntryIndex = which;
                                String value = mEntryValues[which].toString();

                                switch (value) {
                                    case "Bocina de submarino":
                                        mPlayer = MediaPlayer.create(getContext(), R.raw.sub_klaxon);
                                        break;
                                    case "Fábrica":
                                        mPlayer = MediaPlayer.create(getContext(), R.raw.factory);
                                        break;
                                    case "Sirena de aire":
                                        mPlayer = MediaPlayer.create(getContext(), R.raw.air_horn);
                                        break;
                                    default:
                                        mPlayer = MediaPlayer.create(getContext(), R.raw.beep_ping);
                                        break;
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

