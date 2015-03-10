package com.mobilemakers.juansoler.appradar;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class Transitions {

    public static void transitionIN(final View view, long duration) {

        Animation animationIn = new AlphaAnimation(Constants.ANIMATION_ALPHA_FROM, Constants.ANIMATION_ALPHA_TO);
        animationIn.setDuration(duration);
        view.startAnimation(animationIn);
        animationIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void transitionOUT(final View view, long duration,final Boolean gone) {

        Animation animationOut = new AlphaAnimation(Constants.ANIMATION_ALPHA_TO, Constants.ANIMATION_ALPHA_FROM);
        animationOut.setDuration(duration);
        view.startAnimation(animationOut);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (gone) {
                    view.setVisibility(View.GONE);
                }else {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void transitionOUT(final View view, long duration,final Boolean gone, final View replaceWith) {

        Animation animationOut = new AlphaAnimation(Constants.ANIMATION_ALPHA_TO, Constants.ANIMATION_ALPHA_FROM);
        animationOut.setDuration(duration);
        view.startAnimation(animationOut);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (gone) {
                    view.setVisibility(View.GONE);
                    transitionIN(replaceWith, 1000);
                }else {
                    view.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
