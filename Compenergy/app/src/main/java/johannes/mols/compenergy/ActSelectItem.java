/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ActSelectItem extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private List<String> categories_list;
    private HashMap<String, List<Carrier>> carriers_list;
    private DataExpandableListAdapter adapter;
    private EditText searchEditText;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_item_layout);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_select_item);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.setTitle(getString(R.string.act_selection_title));

        expandableListView = (ExpandableListView) findViewById(R.id.act_select_expandable_list_view);
        dbHelper = new DatabaseHelper(this, null, null, 1);

        displayList();

        expandableListView.setOnChildClickListener(itemSelectionListener);

        searchEditText = (EditText) findViewById(R.id.act_select_item_data_search);
        searchEditText.setOnTouchListener(selectionSearchOnTouchListener);
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
    }

    //Back button in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void expandAllGroups() {
        for(int i = 0; i < adapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    private void displayList() {
        prepareListData();

        adapter = new DataExpandableListAdapter(this, categories_list, carriers_list);
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

        //Sort the categories list before inserting the favorite list at the start, it has to stay at Index 0
        Collections.sort(categories_list, CustomComparators.ALPHABETICAL_ORDER);

        //Insert Favorite List
        List<Carrier> favoriteList = dbHelper.getFavoriteCarriers();
        if(favoriteList.size() > 0) {
            categories_list.add(0, getString(R.string.act_selection_fav_group_title));
            carriers_list.put(getString(R.string.act_selection_fav_group_title), favoriteList);
        }

        //Continue with sorting the carriers list
        CustomComparators.CarrierComparator comparator = new CustomComparators.CarrierComparator();
        for (List<Carrier> l : carriers_list.values())
            Collections.sort(l, comparator);
    }

    View.OnTouchListener selectionSearchOnTouchListener = new View.OnTouchListener() {
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

    ExpandableListView.OnChildClickListener itemSelectionListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Carrier clickedItem = ((Carrier) adapter.getChild(groupPosition, childPosition));
            Intent result = new Intent();
            result.putExtra(getString(R.string.act_selection_intent_result_key_do_not_change), clickedItem.get_id());
            setResult(Activity.RESULT_OK, result);
            finish();
            return false; //indicates that the event is not consumed, other events can still be triggered
        }
    };
}
