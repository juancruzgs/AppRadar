package com.mobilemakers.juansoler.appradar;

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

    public DestinationsDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.destinations_dialog, container);
        getDialog().setTitle("Select destination...");
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
        DestinationDialogListener activity = (DestinationDialogListener) getFragmentManager().findFragmentById(R.id.container);
        activity.onFinishDialog(mDestinationsList.get(position));
        this.dismiss();
    }
}