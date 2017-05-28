/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Data extends Fragment {

    private Context mContext;

    private EditText searchEditText;
    private ListView listView;
    private DatabaseHelper dbHelper;

    private List<Boolean> minimized_categories = new ArrayList<>();

    DataListAdapter adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.nav_item_data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_layout, container, false);

        mContext = getContext();
        dbHelper = new DatabaseHelper(mContext, null, null, 1);
        listView = (ListView) view.findViewById(R.id.fragment_data_list_view);

        //Fill the minimied categories list
        for(int i = 0; i < dbHelper.getCategoryList().size(); i++) {
            minimized_categories.add(false);
        }

        final List<Object> combinedCategoryCarrierList = dbHelper.getCombinedCategoryCarrierList();
        adapter = new DataListAdapter(mContext, combinedCategoryCarrierList, minimized_categories);
        listView.setAdapter(adapter);

        listView.setTextFilterEnabled(true);

        //Search
        searchEditText = (EditText) view.findViewById(R.id.fragment_data_search);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Object> list = new ArrayList<>();
                List<Object> empty_list = new ArrayList<>();

                //Check if the search query string is empty or whitespace, if yes: show all data and exit the function
                if(s.toString().trim().length() == 0) {
                    DataListAdapter new_adapter = new DataListAdapter(mContext, combinedCategoryCarrierList, minimized_categories);
                    listView.setAdapter(new_adapter);
                    adapter.notifyDataSetChanged();
                    return;
                }

                //Fill the list of data which will be shown
                //If you like to change the search method to "contain" instead of "startsWith", you can do that by swapping the two "startsWith" methods in the loop to "contains"
                //Maybe put a setting to toggle that
                for(int i = 0; i < combinedCategoryCarrierList.size(); i++) {
                    if(combinedCategoryCarrierList.get(i) instanceof Carrier) {
                        if(((Carrier)combinedCategoryCarrierList.get(i)).get_name().toLowerCase().startsWith(String.valueOf(s).toLowerCase())) {
                            list.add(combinedCategoryCarrierList.get(i));
                        }
                    }
                    else if(combinedCategoryCarrierList.get(i) instanceof String) {
                        //Check if the category has any child members matching the search query by looping through all items, could be expensive in terms of computation time
                        String tmpCategory = (String)combinedCategoryCarrierList.get(i);
                        for(int x = 0; x < combinedCategoryCarrierList.size(); x++) {
                            if(combinedCategoryCarrierList.get(x) instanceof Carrier) {
                                if(((Carrier)combinedCategoryCarrierList.get(x)).get_name().toLowerCase().startsWith(String.valueOf(s).toLowerCase())) {
                                    if(((Carrier)combinedCategoryCarrierList.get(x)).get_category().equalsIgnoreCase(tmpCategory)) {
                                        list.add(combinedCategoryCarrierList.get(i));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                //Check if the search query had matches, if yes the list size will be larger then zero and the list can be applied
                //If the list is empty, fill the list view with an empty list so no data is shown
                if(list.size() > 0) {
                    DataListAdapter new_adapter = new DataListAdapter(mContext, list, minimized_categories);
                    listView.setAdapter(new_adapter);
                    adapter.notifyDataSetChanged();
                }
                else {
                    DataListAdapter new_adapter = new DataListAdapter(mContext, empty_list, minimized_categories);
                    listView.setAdapter(new_adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = listView.getItemAtPosition(position);

                if(item instanceof Carrier) {
                    //Show Data Editor

                }
                else if (item instanceof String) {
                    //Minimize or maximize category
                    ImageView icon = (ImageView) view.findViewById(R.id.fragment_data_list_view_category_icon);
                    if(icon.getTag().toString().equalsIgnoreCase("maximized")) {
                        icon.setTag("minimized");
                        icon.setImageResource(R.drawable.ic_add_black_24dp);

                        //create a new list without the items in this category
                        List<Object> min_list = new ArrayList<>();
                        for (int i = 0; i < combinedCategoryCarrierList.size(); i++) {
                            if(combinedCategoryCarrierList.get(i) instanceof Carrier) {
                                if(!((Carrier)combinedCategoryCarrierList.get(i)).get_category().equalsIgnoreCase(item.toString())) {
                                    min_list.add(combinedCategoryCarrierList.get(i));
                                }
                            }
                            else if(combinedCategoryCarrierList.get(i) instanceof String) {
                                min_list.add(combinedCategoryCarrierList.get(i));
                            }
                        }

                        //update the minimized categories list
                        for(int i = 0; i < dbHelper.getCategoryList().size(); i++) {
                            if(combinedCategoryCarrierList.get(i) instanceof String) {
                                if(combinedCategoryCarrierList.get(i).equals(item.toString())) {
                                    minimized_categories.set(i, true);
                                }
                            }
                        }

                        DataListAdapter new_adapter = new DataListAdapter(mContext, min_list, minimized_categories);
                        listView.setAdapter(new_adapter);
                        adapter.notifyDataSetChanged();
                    }
                    else if(icon.getTag().toString().equalsIgnoreCase("minimized")) {
                        icon.setTag("maximized");
                        icon.setImageResource(R.drawable.ic_remove_black_24dp);

                        //update the minimized category list
                        for(int i = 0; i < dbHelper.getCategoryList().size(); i++) {
                            if(combinedCategoryCarrierList.get(i) instanceof String) {
                                if(combinedCategoryCarrierList.get(i).equals(item.toString())) {
                                    minimized_categories.set(i, false);
                                }
                            }
                        }

                        DataListAdapter new_adapter = new DataListAdapter(mContext, combinedCategoryCarrierList, minimized_categories);
                        listView.setAdapter(new_adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }
}
