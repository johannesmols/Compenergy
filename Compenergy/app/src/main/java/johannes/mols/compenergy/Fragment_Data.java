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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fragment_Data extends Fragment {

    private Context mContext;

    private EditText searchEditText;
    private ListView listView;
    private DatabaseHelper dbHelper;

    DataListAdapterString adapter;

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

        List<Object> combinedCategoryCarrierList = dbHelper.getCombinedCategoryCarrierList();
        listView.setAdapter(new DataListAdapter(mContext, combinedCategoryCarrierList));

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
