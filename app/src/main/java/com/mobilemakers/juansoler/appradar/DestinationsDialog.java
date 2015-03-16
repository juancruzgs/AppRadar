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
import java.util.ArrayList;
import java.util.Collections;

public class DestinationsDialog extends DialogFragment {

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
            dialog.setTitle(getString(R.string.Destinations_dialog_title));
        }
        this.setCancelable(false);
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