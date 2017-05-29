/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment_Data extends Fragment {

    private Context mContext;

    private ExpandableListView expandableListView;
    private List<String> categories_list;
    private HashMap<String, List<Carrier>> carriers_list;
    private DataExpandableListAdapter adapter;

    private DatabaseHelper dbHelper;

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
        expandableListView = (ExpandableListView) view.findViewById(R.id.fragment_data_expandable_list_view);
        dbHelper = new DatabaseHelper(mContext, null, null, 1);

        adapter = new DataExpandableListAdapter(mContext, categories_list, carriers_list);

        displayList();

        expandAllGroups();

        EditText searchEditText = (EditText) view.findViewById(R.id.fragment_data_search);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterData(s.toString());
                expandAllGroups();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        expandableListView.setOnItemLongClickListener(deleteSelectedItem);

        return view;
    }

    private void expandAllGroups() {
        for(int i = 0; i < adapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    private void displayList() {
        prepareListData();

        adapter = new DataExpandableListAdapter(mContext, categories_list, carriers_list);
        expandableListView.setAdapter(adapter);

        expandAllGroups();
    }

    private void prepareListData() {
        categories_list = new ArrayList<>();
        carriers_list = new HashMap<>();

        categories_list = dbHelper.getCategoryList();

        for(int i = 0; i < categories_list.size(); i++) {
            List<Carrier> carrierList = dbHelper.getCarriersWithCategory(categories_list.get(i));
            carriers_list.put(categories_list.get(i), carrierList);
        }
    }

    AdapterView.OnItemLongClickListener deleteSelectedItem = new AdapterView.OnItemLongClickListener() {
        @SuppressWarnings("deprecation")
        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
            if(parent.getAdapter().getItem(position) instanceof Carrier) {
                final Spanned message;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    message = Html.fromHtml("Do you want to delete <b>" + ((Carrier) parent.getAdapter().getItem(position)).get_name() + "</b>?", Html.FROM_HTML_MODE_LEGACY);
                } else {
                    message = Html.fromHtml("Do you want to delete " + ((Carrier) parent.getAdapter().getItem(position)).get_name() + "?");
                }

                final boolean[] successful = {false};

                new AlertDialog.Builder(mContext)
                        .setTitle("Delete")
                        .setMessage(message)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteCarrier(((Carrier) parent.getAdapter().getItem(position)).get_name());
                                displayList();
                                Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show();
                                successful[0] = true;
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                successful[0] = false;
                            }
                        })
                        .show();

                return successful[0];
            }
            else {
                return false;
            }
        }
    };
}
