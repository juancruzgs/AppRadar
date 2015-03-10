package com.mobilemakers.juansoler.appradar;

import android.app.Activity;
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
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(mActivity);
        builder.setMessage(mMessage)
                .setCancelable(false)
                .setPositiveButton(mPositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        Intent intent = new Intent(mSettings);
                        mActivity.startActivity(intent);
                    }
                })
                .setNegativeButton(mNegativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }
}
