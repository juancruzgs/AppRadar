package com.mobilemakers.juansoler.appradar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.common.GooglePlayServicesUtil;

public class ErrorDialog extends DialogFragment {

    public ErrorDialog() { }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the error code and retrieve the appropriate dialog
        int errorCode = this.getArguments().getInt(Constants.DIALOG_ERROR);
        return GooglePlayServicesUtil.getErrorDialog(errorCode,
                this.getActivity(), Constants.REQUEST_RESOLVE_ERROR);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        ((StartScreenFragment) getFragmentManager().findFragmentByTag(Constants.START_SCREEN_FRAGMENT_TAG))
                .onDialogDismissed();
    }
}