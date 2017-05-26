/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

class DataListAdapterString extends ArrayAdapter<String> implements Filterable {

    private Context mContext;

    DataListAdapterString(Context context, int resource, List<String> items) {
        super(context, resource, items);
        mContext = context;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            view = vi.inflate(R.layout.listview_item_data_layout, null);
        }

        String p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) view.findViewById(R.id.carrier_name);

            if (tt1 != null) {
                tt1.setText(p);
            }
        }

        return view;
    }
}
