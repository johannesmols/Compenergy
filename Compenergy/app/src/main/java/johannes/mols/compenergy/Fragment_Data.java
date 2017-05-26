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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//IMPLEMENT THIS FOR CATEGORY HEADERS
//https://w2davids.wordpress.com/android-sectioned-headers-in-listviews/

public class Fragment_Data extends Fragment {

    private Context mContext;

    private EditText searchEditText;
    private ListView listView;
    private DatabaseHelper dbHelper;

    DataListAdapterString adapter;

    public final static String ITEM_TITLE = "title";
    public final static String ITEM_CAPTION ="caption";

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
    private class CarrierComparator implements Comparator<Carrier> {
        @Override
        public int compare(Carrier c1, Carrier c2) {
            return c1.get_name().compareTo(c2.get_name());
        }
    }

    public Map<String, ?> createItem(String title, String caption) {
        Map<String, String> item = new HashMap<>();
        item.put(ITEM_TITLE, title);
        item.put(ITEM_CAPTION, caption);
        return item;
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
        dbHelper = new DatabaseHelper(mContext, null, null, 1);
        listView = (ListView) view.findViewById(R.id.fragment_data_list_view);
        List<Carrier> carrierList = new ArrayList<>(dbHelper.getAllCarriers());
        List<String> carrierStringList = new ArrayList<>();
        for (Carrier carrier : carrierList) {
            carrierStringList.add(carrier.get_name());
        }
        Collections.sort(carrierList, new CarrierComparator());
        Collections.sort(carrierStringList, ALPHABETICAL_ORDER);
        List<String> categories = dbHelper.getCategoryList();
        adapter = new DataListAdapterString(mContext, R.layout.listview_item_data_layout, carrierStringList);
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
