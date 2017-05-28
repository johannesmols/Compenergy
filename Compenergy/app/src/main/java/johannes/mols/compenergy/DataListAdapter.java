/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class DataListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Object> list = null;
    private List<Boolean> _minimized_categories = null;
    private static final int CARRIER = 0;
    private static final int HEADER = 1;
    private LayoutInflater inflater;

    DataListAdapter(Context context, List<Object> input, List<Boolean> minimized_categories) {
        this.mContext = context;
        this.list = input;
        this._minimized_categories = minimized_categories;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof Carrier) {
            return CARRIER;
        } else {
            return HEADER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            switch (getItemViewType(position)) {
                case CARRIER:
                    convertView = inflater.inflate(R.layout.listview_item_data_layout, null);
                    break;
                case HEADER:
                    convertView = inflater.inflate(R.layout.listview_header_data_layout, null);
                    break;
            }
        }

        if(convertView != null) {
            switch (getItemViewType(position)) {
                case CARRIER:
                    TextView name;
                    name = (TextView) convertView.findViewById(R.id.fragment_data_list_view_carrier_name);
                    name.setText(((Carrier) list.get(position)).get_name());
                    break;
                case HEADER:
                    TextView category = (TextView) convertView.findViewById(R.id.fragment_data_list_view_category);
                    category.setText((String) list.get(position));

                    ImageView imageView = (ImageView) convertView.findViewById(R.id.fragment_data_list_view_category_icon);
                    if(imageView.getTag().toString().equalsIgnoreCase("maximized")) {
                        imageView.setImageResource(R.drawable.ic_remove_black_24dp);
                        imageView.setTag("minimized");
                    }
                    else if (imageView.getTag().toString().equalsIgnoreCase("minimized")) {
                        imageView.setImageResource(R.drawable.ic_add_black_24dp);
                        imageView.setTag("maximized");
                    }
                    break;
            }
        }

        return convertView;
    }
}