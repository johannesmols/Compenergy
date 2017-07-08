/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ExpandableListView;

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

        expandableListView = (ExpandableListView) findViewById(R.id.act_select_expandable_list_view);
        dbHelper = new DatabaseHelper(this, null, null, 1);
    }
}
