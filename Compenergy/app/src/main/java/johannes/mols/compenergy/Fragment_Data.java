/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.util.CircularArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Data extends Fragment {

    private Context mContext;

    private EditText searchEditText;
    private ListView listView;
    private DatabaseHelper dbHelper;

    DataListAdapter adapter;

    public final static String ITEM_TITLE = "title";
    public final static String ITEM_CAPTION ="caption";

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

        final List<Object> combinedCategoryCarrierList = dbHelper.getCombinedCategoryCarrierList();
        adapter = new DataListAdapter(mContext, combinedCategoryCarrierList);
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
                    DataListAdapter new_adapter = new DataListAdapter(mContext, combinedCategoryCarrierList);
                    listView.setAdapter(new_adapter);
                    adapter.notifyDataSetChanged();
                    return;
                }

                //Fill the list of data which will be shown
                //If you like to change the search method to "contain" instead of "start with", you can do that by swapping the two "startsWith" methods in the loop to "contains"
                //Maybe put a setting to trigger that
                for(int i = 0; i < combinedCategoryCarrierList.size(); i++) {
                    if(combinedCategoryCarrierList.get(i) instanceof Carrier) {
                        if(((Carrier)combinedCategoryCarrierList.get(i)).get_name().toLowerCase().startsWith(String.valueOf(s).toLowerCase())) {
                            list.add(combinedCategoryCarrierList.get(i));
                        }
                    }
                    else if(combinedCategoryCarrierList.get(i) instanceof String) {
                        //Check if the category has any child members by looping through all items, could be expensive in terms of computation time
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
                    DataListAdapter new_adapter = new DataListAdapter(mContext, list);
                    listView.setAdapter(new_adapter);
                    adapter.notifyDataSetChanged();
                }
                else {
                    DataListAdapter new_adapter = new DataListAdapter(mContext, empty_list);
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
                //Show Data Editor
            }
        });

        return view;
    }
}
