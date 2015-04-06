package com.mobilemakers.juansoler.appradar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import com.afollestad.materialdialogs.AlertDialogWrapper;


public class CustomAlertDialog {

    String mMessage;
    String mPositiveButton;
    String mNegativeButton;
    Activity mActivity;
    String mSettings;

    public CustomAlertDialog() {
    }

    public CustomAlertDialog(String message, String positiveButton, String negativeButton, String settings, Activity activity) {
        mMessage = message;
        mPositiveButton = positiveButton;
        mNegativeButton = negativeButton;
        mSettings = settings;
        mActivity = activity;
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(mMessage);
        builder.setPositiveButton(mPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mSettings);
                mActivity.startActivity(intent);
            }
        });

        builder.setNegativeButton(mNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
