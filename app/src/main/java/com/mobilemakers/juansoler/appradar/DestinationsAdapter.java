package com.mobilemakers.juansoler.appradar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DestinationsAdapter extends ArrayAdapter<String> {

    Context mContext;
    ArrayList<String> mDestinations;

    public class ViewHolder {
        public final TextView textViewDestination;

        public ViewHolder(View view) {
            textViewDestination = (TextView) view.findViewById(R.id.textView_destination);
        }
    }

    public DestinationsAdapter(Context context, ArrayList<String> destinations) {
        super(context, R.layout.destinations_list_item, destinations);
        mContext = context;
        mDestinations = destinations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = reuseOrGenerateRowView(convertView, parent);
        displayContentInRowView(position, rowView);
        return rowView;
    }

    private void displayContentInRowView(final int position, View rowView) {
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.textViewDestination.setText(mDestinations.get(position));
    }

    private View reuseOrGenerateRowView(View convertView, ViewGroup parent) {
        View rowView;
        if (convertView != null) {
            rowView = convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.destinations_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        }
        return rowView;
    }

}
