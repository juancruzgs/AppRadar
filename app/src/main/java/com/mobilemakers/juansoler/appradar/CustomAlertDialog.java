package com.mobilemakers.juansoler.appradar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;


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
        builder.setMessage(mMessage)
                .setPositiveButton(mPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mSettings);
                mActivity.startActivity(intent);
            }
        })
                .setNegativeButton(mNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        })
                .show();
    }
}
