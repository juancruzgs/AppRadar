package com.mobilemakers.juansoler.appradar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by paula.baudo on 3/2/2015.
 */
public class SoundListPreference extends ListPreference {

    private Context context;
    private int mClickedDialogEntryIndex;

    public SoundListPreference(final Context context) {
        super(context);
        this.context = context;
    }

    public SoundListPreference(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void showDialog(Bundle state) {
        int preselect = findIndexOfValue(getValue());
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(getDialogTitle())
                .content(getDialogMessage())
                .icon(getDialogIcon())
                .negativeText(getNegativeButtonText())
                .positiveText(getPositiveButtonText())
                .items(getEntries())
                .itemsCallbackSingleChoice(preselect, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        onClick(null, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                        if (which >= 0 && getEntryValues() != null) {
                            String value = getEntryValues()[which].toString();
                            if (callChangeListener(value) && isPersistent())
                                setValue(value);
                        }
                    }
                });

        final View contentView = onCreateDialogView();
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.customView(contentView, false);
        } else {
            builder.content(getDialogMessage());
        }

        builder.show();
    }



}

