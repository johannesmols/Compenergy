/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class ActEditCarrier extends AppCompatActivity {

    private Context mContext = this;
    private DatabaseHelper dbHelper = new DatabaseHelper(mContext, null, null, 1);
    private Carrier editableCarrier;

    private EditText edit_name;
    private InstantAutoComplete autoComplete_category;
    private Spinner spinner_type;
    private TextView text_info;
    private EditText edit_energy;
    private TextView text_unit_info;
    private Button button_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_layout);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.setTitle(getString(R.string.edit_data_act_title));

        edit_name = (EditText) findViewById(R.id.edit_data_name_edit);
        autoComplete_category = (InstantAutoComplete) findViewById(R.id.edit_data_category_autocomplete_edit);
        spinner_type = (Spinner) findViewById(R.id.edit_data_type_spinner);
        text_info = (TextView) findViewById(R.id.edit_data_type_info);
        edit_energy = (EditText) findViewById(R.id.edit_data_energy_edit);
        text_unit_info = (TextView) findViewById(R.id.edit_data_energy_type);
        button_edit = (Button) findViewById(R.id.edit_data_edit_button);

        //Get the carrier
        final Intent intent = getIntent();
        final String target_name = intent.getStringExtra(getResources().getString(R.string.intent_key_for_editor));
        editableCarrier = dbHelper.getCarriersWithName(target_name).get(0);

        fillComponentsWithData();
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

    private void fillComponentsWithData() {
        edit_name.setText(editableCarrier.get_name());

        List<String> categories = dbHelper.getCategoryList();
        Collections.sort(categories, CustomComparators.ALPHABETICAL_ORDER);
        ArrayAdapter<String> category_adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, categories);
        autoComplete_category.setAdapter(category_adapter);
        autoComplete_category.setText(editableCarrier.get_category());

        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(mContext, R.array.spinner_carrier_types_without_vehicles, R.layout.spinner_item);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(type_adapter);

        if (editableCarrier.get_unit().equals(getResources().getString(R.string.carrier_type_db_capacity))) {
            spinner_type.setSelection(0);
            text_info.setText(getResources().getString(R.string.edit_data_info_type_1));
            text_unit_info.setText(getResources().getString(R.string.edit_data_unit_watt));
        }
        else if (editableCarrier.get_unit().equals(getResources().getString(R.string.carrier_type_db_consumption))) {
            spinner_type.setSelection(1);
            text_info.setText(getResources().getString(R.string.edit_data_info_type_2));
            text_unit_info.setText(getResources().getString(R.string.edit_data_unit_watt));
        }
        else if (editableCarrier.get_unit().equals(getResources().getString(R.string.carrier_type_db_volume_consumption))) {
            spinner_type.setSelection(2);
            text_info.setText(getResources().getString(R.string.edit_data_info_type_3));
            text_unit_info.setText(getResources().getString(R.string.edit_data_unit_joule));
        }
        else if (editableCarrier.get_unit().equals(getResources().getString(R.string.carrier_type_db_content_mass))) {
            spinner_type.setSelection(3);
            text_info.setText(getResources().getString(R.string.edit_data_info_type_4));
            text_unit_info.setText(getResources().getString(R.string.edit_data_unit_joule));
        }
        else if (editableCarrier.get_unit().equals(getResources().getString(R.string.carrier_type_db_content_volume))) {
            spinner_type.setSelection(4);
            text_info.setText(getResources().getString(R.string.edit_data_info_type_5));
            text_unit_info.setText(getResources().getString(R.string.edit_data_unit_joule));
        }

        edit_energy.setText(String.valueOf(editableCarrier.get_energy()));
    }
}
