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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class FavoriteExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> list_categories = new ArrayList<>();
    private List<String> list_categories_original = new ArrayList<>();
    private HashMap<String, List<Carrier>> list_carriers = new HashMap<>();
    private HashMap<String, List<Carrier>> list_carriers_original = new HashMap<>();

    FavoriteExpandableListAdapter(Context context, List<String> categories, HashMap<String, List<Carrier>> carriers) {
        this.mContext = context;
        this.list_categories = categories;
        this.list_categories_original = categories;
        this.list_carriers = carriers;
        this.list_carriers_original = carriers;
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

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.list_view_category);
        lblListHeader.setText(headerTitle);

        ImageView expandIcon = (ImageView) convertView.findViewById(R.id.list_view_category_icon);
        if(isExpanded) {
            expandIcon.setImageResource(R.drawable.ic_remove_black_24dp);
        } else {
            expandIcon.setImageResource(R.drawable.ic_add_black_24dp);
        }

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final String carrierName = ((Carrier)getChild(groupPosition, childPosition)).get_name();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item_favorite_layout, null);
            }

            TextView txtListChild;
            txtListChild = (TextView) convertView.findViewById(R.id.fragment_favorites_list_view_carrier_name);

            txtListChild.setText(carrierName);
            return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    void filterData(String query) {
        query = query.toLowerCase();
        list_categories = new ArrayList<>(); //Don't use .clear() => it will clear the other original list too, thanks java -.-
        list_carriers = new HashMap<>();

        DatabaseHelper dbHelper = new DatabaseHelper(mContext, null, null, 1);

        if(query.trim().isEmpty()) {
            list_categories = new ArrayList<>(list_categories_original);
            list_carriers = new HashMap<>(list_carriers_original);
            notifyDataSetInvalidated();
        }
        else {
            //Filter all data with the given search query. Yes, it's complicated
            List<String> new_categories_list = new ArrayList<>();
            HashMap<String, List<Carrier>> new_carriers_list = new HashMap<>();
            List<String> all_categories_list;
            all_categories_list = dbHelper.getCategoryListThatContainsFavorites();
            for(int i = 0; i < all_categories_list.size(); i++) {
                List<Carrier> carriersWithCategoryList;
                carriersWithCategoryList = dbHelper.getFavoritesWithCategory(all_categories_list.get(i));
                List<Carrier> matchingCarriersInCategory = new ArrayList<>();
                for(Carrier carrierInCategory : carriersWithCategoryList) {
                    if(carrierInCategory.get_name().toLowerCase().contains(query)) {
                        matchingCarriersInCategory.add(carrierInCategory);
                        if(!new_categories_list.contains(all_categories_list.get(i))) {
                            new_categories_list.add(all_categories_list.get(i));
                        }
                    }
                }
                new_carriers_list.put(all_categories_list.get(i), matchingCarriersInCategory);
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