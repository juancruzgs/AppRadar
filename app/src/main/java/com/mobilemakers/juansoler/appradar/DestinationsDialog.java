package com.mobilemakers.juansoler.appradar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;

public class DestinationsDialog extends DialogFragment {

    public interface DestinationDialogListener {
        void onFinishDialog(String Destination);
    }

    DestinationsAdapter mAdapter;
    ArrayList<String> mDestinationsList = new ArrayList<>();
    //Boolean mPressed = false;
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
            dialog.setCanceledOnTouchOutside(false);
            dialog.setTitle("Select destination...");
        }
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
        return rootView;
    }

    public void dismissDialog(int position){
        mDialogListener.onFinishDialog(mDestinationsList.get(position));
        //mPressed = true;
        this.dismiss();
    }

    /*@Override
    public void onDismiss(DialogInterface dialog) {
        if (!mPressed) {
            mDialogListener.onFinishDialog("");
        }
        super.onDismiss(dialog);
    }*/
}