/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Fragment_Favorites extends Fragment {

    private Context mContext;

    private ExpandableListView expandableListView;
    private List<String> categories_list;
    private HashMap<String, List<Carrier>> carriers_list;
    private FavoriteExpandableListAdapter adapter;
    private EditText searchEditText;

    private DatabaseHelper dbHelper;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.nav_item_favorites);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites_layout, container, false);
        mContext = getContext();
        expandableListView = (ExpandableListView) view.findViewById(R.id.fragment_favorites_expandable_list_view);
        dbHelper = new DatabaseHelper(mContext, null, null, 1);

        displayList();

        expandAllGroups();

        searchEditText = (EditText) view.findViewById(R.id.fragment_favorites_search);
        searchEditText.setOnTouchListener(editSearchOnTouchListener);
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

        expandableListView.setOnChildClickListener(toggleFavorite);

        return view;
    }

    private void expandAllGroups() {
        for(int i = 0; i < adapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    private void displayList() {
        prepareListData();

        adapter = new FavoriteExpandableListAdapter(mContext, categories_list, carriers_list);
        expandableListView.setAdapter(adapter);

        expandAllGroups();
    }

    private void prepareListData() {
        categories_list = new ArrayList<>();
        carriers_list = new HashMap<>();

        categories_list = dbHelper.getCategoryListThatContainsFavorites();

        for(int i = 0; i < categories_list.size(); i++) {
            List<Carrier> carrierList = dbHelper.getFavoritesWithCategory(categories_list.get(i));
            carriers_list.put(categories_list.get(i), carrierList);
        }

        //Sort
        Collections.sort(categories_list, CustomComparators.ALPHABETICAL_ORDER);
        CustomComparators.CarrierComparator comparator = new CustomComparators.CarrierComparator();
        for (List<Carrier> l : carriers_list.values())
            Collections.sort(l, comparator);
    }

    private void toggleCarrier(Carrier c) {
        Carrier item = dbHelper.getCarriersWithName(c.get_name()).get(0);
        if(item.get_favorite()) {
            item.set_favorite(false);
        } else {
            item.set_favorite(true);
        }
        dbHelper.updateCarrier(item.get_id(), item);
        updateToggle(item);
    }

    private void updateToggle(Carrier c) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            displayList();
        }
    }

    ExpandableListView.OnChildClickListener toggleFavorite = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Carrier item = (Carrier) adapter.getChild(groupPosition, childPosition);
            toggleCarrier(item);
            return true; //indicates that the event is consumed, meaning the long click listener can't be triggered too
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
