package com.mobilemakers.juansoler.appradar;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class DestinationsDialog extends DialogFragment {

    private static final String TABLE_DIVIDER_NAME = "titleDivider";
    private static final String TABLE_DIVIDER_TYPE = "id";
    private static final String TABLE_DIVIDER_PACKAGE = "android";

    ArrayList<String> mDestinationsList = new ArrayList<>();

    public interface DestinationDialogListener {
        void onFinishDialog(String Destination);
    }

    public DestinationsDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.destinations_dialog, container);
        prepareDialog();
        prepareListView(rootView);
        prepareButtonCancel(rootView);
        return rootView;
    }

    private void prepareDialog() {
        Dialog dialog = getDialog();
        if (dialog != null){
            prepareTitle(dialog);
        }
        this.setCancelable(false);
    }

    private void prepareTitle(Dialog dialog) {
        dialog.setTitle(getString(R.string.Destinations_dialog_title));
        TextView title = (TextView)getDialog().findViewById( android.R.id.title );
        title.setTextColor( getResources().getColor( R.color.accent ) );
        int titleDividerId = getResources().getIdentifier(TABLE_DIVIDER_NAME, TABLE_DIVIDER_TYPE, TABLE_DIVIDER_PACKAGE);
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(getResources().getColor(R.color.accent));
    }

    private void prepareListView(View rootView) {
        ListView listView = (ListView) rootView.findViewById(R.id.listView_destinations);
        setListViewAdapter(listView);
        setOnItemClickListener(listView);
    }

    private void setListViewAdapter(ListView listView) {
        Collections.addAll(mDestinationsList, getResources().getStringArray(R.array.cities));
        DestinationsAdapter adapter = new DestinationsAdapter(getActivity(), mDestinationsList);
        listView.setAdapter(adapter);
    }

    private void setOnItemClickListener(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismissDialog(position);
            }
        });
    }

    private void prepareButtonCancel(View rootView) {
        Button buttonCancelDialog = (Button)rootView.findViewById(R.id.button_cancel_dialog);
        buttonCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog(-1);
            }
        });
    }

    public void dismissDialog(int position){
        DestinationDialogListener dialogListener = (DestinationDialogListener) getFragmentManager().findFragmentById(R.id.container);
        if (position > -1) {
            dialogListener.onFinishDialog(mDestinationsList.get(position));
        } else {
            dialogListener.onFinishDialog("");
        }
        this.dismiss();
    }
}