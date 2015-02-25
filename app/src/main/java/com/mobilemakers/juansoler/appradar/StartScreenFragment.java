package com.mobilemakers.juansoler.appradar;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

public class StartScreenFragment extends Fragment implements DestinationsDialog.DestinationDialogListener {

    Button mButtonSetDestination;

    public StartScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
        mButtonSetDestination = (Button)rootView.findViewById(R.id.button_select_desntination);
        mButtonSetDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DestinationsDialog destinationsDialog = new DestinationsDialog();
                destinationsDialog.show(fm, "destinations_dialog");
            }
        });
        return rootView;
    }

    @Override
    public void onFinishDialog(String destination) {
        mButtonSetDestination.setText(destination);
    }
}
