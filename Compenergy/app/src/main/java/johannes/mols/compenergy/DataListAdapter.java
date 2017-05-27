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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class DataListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<Object> originalData = null;
    private List<Object> filteredData = null;
    private static final int CARRIER = 0;
    private static final int HEADER = 1;
    private LayoutInflater inflater;
    private ItemFilter mFilter = new ItemFilter();

    DataListAdapter(Context context, List<Object> input) {
        this.mContext = context;
        this.originalData = input;
        this.filteredData = input;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (originalData.get(position) instanceof Carrier) {
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
        return originalData.size();
    }

    @Override
    public Object getItem(int position) {
        return originalData.get(position);
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

        switch (getItemViewType(position)) {
            case CARRIER:
                TextView name = (TextView) convertView.findViewById(R.id.fragment_data_list_view_carrier_name);
                name.setText(((Carrier) originalData.get(position)).get_name());
                break;
            case HEADER:
                TextView category = (TextView) convertView.findViewById(R.id.fragment_data_list_view_category);
                category.setText((String) originalData.get(position));
                break;
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            DatabaseHelper dbHelper = new DatabaseHelper(mContext, null, null, 1);
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Object> list = originalData;
            int count = list.size();
            final List<Object> nlist = new ArrayList<>(count);
            String filterableString = "";

            for (int i = 0; i < count; i++) {
                switch (getItemViewType(i)) {
                    case CARRIER:
                        filterableString = ((Carrier)list.get(i)).get_name();
                        break;
                    case HEADER:
                        filterableString = "";
                        break;
                }
                if(filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(dbHelper.getCarriersWithName(filterableString).get(0));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.count == 0) {
                notifyDataSetInvalidated();
            } else {
                originalData = (List<Object>)results.values;
                notifyDataSetChanged();
            }

        }
    }
}
