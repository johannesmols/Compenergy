/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class DataExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> list_categories;
    private List<String> list_categories_original;
    private HashMap<String, List<Carrier>> list_carriers;
    private HashMap<String, List<Carrier>> list_carriers_original;

    DataExpandableListAdapter(Context context, List<String> categories, HashMap<String, List<Carrier>> carriers) {
        this.mContext = context;
        this.list_categories = categories;
        this.list_categories_original = categories;
        this.list_carriers = carriers;
        this.list_carriers = carriers;
    }

    @Override
    public int getGroupCount() {
        return this.list_categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.list_carriers.get(this.list_categories.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.list_categories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.list_carriers.get(this.list_categories.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_header_data_layout, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.fragment_data_list_view_category);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final String carrierName = ((Carrier)getChild(groupPosition, childPosition)).get_name();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item_data_layout, null);
            }

            TextView txtListChild = (TextView) convertView.findViewById(R.id.fragment_data_list_view_carrier_name);

            txtListChild.setText(carrierName);
            return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filterData(String query) {
        query = query.toLowerCase();
        list_categories.clear();
        list_carriers.clear();

        DatabaseHelper dbHelper = new DatabaseHelper(mContext, null, null, 1);

        if(query.isEmpty()) {
            list_categories.addAll(list_categories_original);
            list_carriers.putAll(list_carriers_original);
        }
        else {
            //Fill category list with categories which contain matching child items
            /*List<String> categories_list = new ArrayList<>();
            List<String> tmpCategoryList = dbHelper.getCategoryList();
            for(String category : tmpCategoryList) {
                //Check if category has items with search query
                List<Carrier> tmpCarrierList = dbHelper.getAllCarriers();
                for(Carrier carrier : tmpCarrierList) {
                    if(carrier.get_category().toLowerCase().equalsIgnoreCase(category)) {
                        if(carrier.get_name().toLowerCase().startsWith(query)) {
                            categories_list.add(category);
                        }
                    }
                }
            }

            if(categories_list.size() > 0) {
                list_categories.addAll(categories_list);
            }

            //Get matching carriers
            List<Carrier> carrierList = dbHelper.getAllCarriers();
            HashMap<String, List<Carrier>>new_carriers_list_2 = new HashMap<>();
            for(Carrier carrier : carrierList) {
                if(carrier.get_name().toLowerCase().startsWith(query)) {
                    new_carriers_list_2.put(categories_list.get(0), carrier);
                }

                for(int i = 0; i < categories_list.size(); i++) {
                    List<Carrier> carrierList = dbHelper.getCarriersWithCategory(categories_list.get(i));
                    carriers_list.put(categories_list.get(i), carrierList);
                }
            }*/

            List<String> new_categories_list = new ArrayList<>();
            HashMap<String, List<Carrier>> new_carriers_list = new HashMap<>();
            List<String> all_categories_list = dbHelper.getCategoryList();
            for(int i = 0; i < all_categories_list.size(); i++) {
                List<Carrier> carriersWithCategoryList = dbHelper.getCarriersWithCategory(all_categories_list.get(i));
                for(Carrier carrierInCategory : carriersWithCategoryList) {
                    if(carrierInCategory.get_name().toLowerCase().startsWith(query)) {
                        new_carriers_list.put(all_categories_list.get(i), carriersWithCategoryList);
                        if(!new_carriers_list.containsValue(all_categories_list.get(i))) {
                            new_categories_list.add(all_categories_list.get(i));
                        }
                    }
                }
            }

            if(new_categories_list.size() > 0 && new_carriers_list.size() > 0) {
                list_categories.clear();
                list_categories.addAll(new_categories_list);
                list_carriers.clear();
                list_carriers.putAll(new_carriers_list);
            }

            notifyDataSetChanged();
        }
    }
}
