/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Fragment_Data extends Fragment {

    private Context mContext;

    private ExpandableListView expandableListView;
    private List<String> categories_list;
    private HashMap<String, List<Carrier>> carriers_list;
    private DataExpandableListAdapter adapter;
    private EditText searchEditText;

    private DatabaseHelper dbHelper;

    private static final int REQUEST_CODE_EDIT = 0x539;

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

        displayList();

        searchEditText = (EditText) view.findViewById(R.id.fragment_data_search);
        searchEditText.setOnTouchListener(editSearchOnTouchListener);
        searchEditText.setFilters(new InputFilter[] { Util.filter });
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
        expandableListView.setOnChildClickListener(editSelectedItem);

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

        //Sort
        Collections.sort(categories_list, CustomComparators.ALPHABETICAL_ORDER);
        CustomComparators.CarrierComparator comparator = new CustomComparators.CarrierComparator();
        for (List<Carrier> l : carriers_list.values())
            Collections.sort(l, comparator);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            displayList();
        }
    }

    ExpandableListView.OnChildClickListener editSelectedItem = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Intent editor = new Intent(mContext, ActEditCarrier.class);
            editor.putExtra(mContext.getResources().getString(R.string.intent_key_for_editor), ((Carrier)adapter.getChild(groupPosition, childPosition)).get_name());
            startActivityForResult(editor, REQUEST_CODE_EDIT);
            return true; //indicates that the event is consumed, meaning the long click listener can't be triggered too
        }
    };

    AdapterView.OnItemLongClickListener deleteSelectedItem = new AdapterView.OnItemLongClickListener() {
        @SuppressWarnings("deprecation")
        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
            if(parent.getAdapter().getItem(position) instanceof Carrier) {
                final Spanned message;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    message = Html.fromHtml(mContext.getResources().getString(R.string.data_delete_confirm) + " <b>" + ((Carrier) parent.getAdapter().getItem(position)).get_name() + "</b>?", Html.FROM_HTML_MODE_LEGACY);
                } else {
                    message = Html.fromHtml(mContext.getResources().getString(R.string.data_delete_confirm) + " " + ((Carrier) parent.getAdapter().getItem(position)).get_name() + "?");
                }

                new AlertDialog.Builder(mContext)
                        .setTitle(mContext.getResources().getString(R.string.data_delete_item_title))
                        .setMessage(message)
                        .setPositiveButton(mContext.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteCarrier(((Carrier) parent.getAdapter().getItem(position)).get_name());
                                displayList();
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.item_deleted), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(mContext.getResources().getString(R.string.dialog_no), null)
                        .show();
            }
            return true;
        }
    };

    View.OnTouchListener editSearchOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //final int DRAWABLE_LEFT = 0;
            //final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            //final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (searchEditText.getRight() - searchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    searchEditText.setText("");
                    return true;
                }
            }
            return false;
        }
    };
}