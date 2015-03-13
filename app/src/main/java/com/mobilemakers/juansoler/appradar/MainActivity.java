package com.mobilemakers.juansoler.appradar;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareFragment(savedInstanceState);

        showIconInActionBar();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            onHandleTransition fragment = (onHandleTransition) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.handleTransition(intent);
        } catch (ClassCastException e) {
        }
    }

    public interface onHandleTransition {
        void handleTransition (Intent intent);
    }

    private void prepareFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new StartScreenFragment(),Constants.START_SCREEN_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            OnBackPressedListener fragment = (OnBackPressedListener) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.doBack();
        } catch (ClassCastException e) {
            super.onBackPressed();
        }
    }

    private void showIconInActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public interface OnBackPressedListener {
        public void doBack();
    }
}
