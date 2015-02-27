package com.mobilemakers.juansoler.appradar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

public class DestinationsDialog extends DialogFragment {

    public interface DestinationDialogListener {
        void onFinishDialog(String Destination);
    }

    DestinationsAdapter mAdapter;
    ArrayList<String> mDestinationsList = new ArrayList<>();
    DestinationDialogListener mDialogListener;

    public DestinationsDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.destinations_dialog, container);
        mDialogListener = (DestinationDialogListener) getFragmentManager().findFragmentById(R.id.container);
        Dialog dialog = getDialog();
        if (dialog != null){
            dialog.setTitle("Select destination...");
        }
        this.setCancelable(false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView_destinations);
        mDestinationsList.add("Buenos Aires");
        mDestinationsList.add("Mar del Plata");
        mAdapter = new DestinationsAdapter(getActivity(), mDestinationsList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismissDialog(position);
            }
        });
        Button buttonCancelDialog = (Button)rootView.findViewById(R.id.button_cancel_dialog);
        buttonCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog(-1);
            }
        });
        return rootView;
    }

    public void dismissDialog(int position){
        if (position > -1) {
            mDialogListener.onFinishDialog(mDestinationsList.get(position));
        } else {
            mDialogListener.onFinishDialog("");
        }
        this.dismiss();
    }

}