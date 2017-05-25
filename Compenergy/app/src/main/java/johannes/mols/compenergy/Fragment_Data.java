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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//IMPLEMENT THIS FOR CATEGORY HEADERS
//https://w2davids.wordpress.com/android-sectioned-headers-in-listviews/

public class Fragment_Data extends Fragment {

    private Context mContext;

    private EditText searchEditText;
    private ListView listView;
    private DatabaseHelper dbHelper;

    DataListAdapter adapter;

    //Alphabetical sort of String List
    private static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
        public int compare(String str1, String str2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
            if (res == 0) {
                res = str1.compareTo(str2);
            }
            return res;
        }
    };

    //Custom comparator by Carrier property name
    private class CustomComparator implements Comparator<Carrier> {
        @Override
        public int compare(Carrier c1, Carrier c2) {
            return c1.get_name().compareTo(c2.get_name());
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.nav_item_data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_layout, container, false);

        //Fill List View
        mContext = getContext();
        dbHelper = new DatabaseHelper(mContext, null, null, 1);                                             //Database helper class
        listView = (ListView) view.findViewById(R.id.fragment_data_list_view);                              //Get list view object
        List<Carrier> carrierList = new ArrayList<>(dbHelper.getAllCarriers());                             //Get all carriers as a list of the Carrier class
        List<String> carrierStringList = new ArrayList<>();                                                 //The same list but as string, containing the name of each carrier class item
        for (Carrier carrier : carrierList) {                                                               //Fill the string list with all the names
            carrierStringList.add(carrier.get_name());
        }
        Collections.sort(carrierStringList, ALPHABETICAL_ORDER);                                            //Sort the name list alphabetically
        adapter = new DataListAdapter(mContext, R.layout.listview_item_data_layout, carrierStringList);     //Use the custom List Adapter to display the name list with custom styled rows
        listView.setAdapter(adapter);                                                                       //Assign the adapter to the list view
        listView.setTextFilterEnabled(true);

        //Category Test - works
        List<String> categories = dbHelper.getCategoryList();
        Toast.makeText(mContext, categories.toArray().toString(), Toast.LENGTH_SHORT);//Enable filtering

        //Search
        searchEditText = (EditText) view.findViewById(R.id.fragment_data_search);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(searchEditText.getText().toString());
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
