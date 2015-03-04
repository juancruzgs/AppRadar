package com.mobilemakers.juansoler.appradar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.preference.ListPreference;

import android.util.AttributeSet;


/**
 * Created by paula.baudo on 3/2/2015.
 */
public class SoundListPreference extends ListPreference {


    private Context context;
    private int mClickedDialogEntryIndex = 0;
    private int mLastClickedDialogEntryIndex = 0;

    public SoundListPreference(final Context context) {
        super(context);
        this.context = context;
    }

    public SoundListPreference(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        CharSequence[] entries = getEntries();
        final CharSequence[] entryValues = getEntryValues();

        builder
                .setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLastClickedDialogEntryIndex = mClickedDialogEntryIndex;
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
                                String value = entryValues[which].toString();
                                MediaPlayer mPlayer;

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

}

