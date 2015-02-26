package com.mobilemakers.juansoler.appradar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

public class StartScreenFragment extends Fragment implements DestinationsDialog.DestinationDialogListener {

    Button mButtonSetDestination;

    public StartScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
        mButtonSetDestination = (Button) rootView.findViewById(R.id.button_select_desntination);
        mButtonSetDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionOUT();
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
        transitionIN();
    }

    public void transitionIN() {

        Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000);
        mButtonSetDestination.startAnimation(in);
        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mButtonSetDestination.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void transitionOUT() {

        Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(1000);
        mButtonSetDestination.startAnimation(out);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mButtonSetDestination.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
