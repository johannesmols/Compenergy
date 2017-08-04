/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActEditCarrier extends AppCompatActivity {

    private Context mContext = this;
    private DatabaseHelper dbHelper = new DatabaseHelper(mContext, null, null, 1);

    private Carrier editableCarrier;
    private List<String> alreadyExistentCarriersNameList;
    private String editableCarrierName;

    private EditText edit_name;
    private InstantAutoComplete autoComplete_category;
    private Spinner spinner_type;
    private TextView text_info;
    private EditText edit_energy;
    private TextView text_unit_info;
    private Button button_edit;
    private CheckBox fav_checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_layout);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
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
        fav_checkbox = (CheckBox) findViewById(R.id.edit_data_toolbar_favorite_toggle);

        edit_name.setOnTouchListener(editNameOnTouchListener);
        autoComplete_category.setOnTouchListener(editCategoryOnTouchListener);
        spinner_type.setOnItemSelectedListener(spinnerTypeItemSelectedListener);

        button_edit.setOnClickListener(editButtonClickListener);

        fav_checkbox.setOnCheckedChangeListener(fav_checked_change_listener);

        edit_name.addTextChangedListener(editNameTextWatcher);

        edit_name.setFilters(new InputFilter[] { Util.filter });
        autoComplete_category.setFilters(new InputFilter[] { Util.filter });
        edit_energy.setFilters(new InputFilter[] { Util.filter });

        //Get the carrier
        final Intent intent = getIntent();
        final String target_name = intent.getStringExtra(getResources().getString(R.string.intent_key_for_editor));
        editableCarrier = dbHelper.getCarriersWithName(target_name).get(0);
        editableCarrierName = editableCarrier.get_name();
        alreadyExistentCarriersNameList = new ArrayList<>(dbHelper.getAllCarriersAsStringList());

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

        if(editableCarrier.get_favorite()) {
            fav_checkbox.setChecked(true);
        } else {
            fav_checkbox.setChecked(false);
        }
    }

    AdapterView.OnItemSelectedListener spinnerTypeItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] types = parent.getResources().getStringArray(R.array.spinner_carrier_types_without_vehicles);
            if(types.length >= position + 1) {
                switch (position) {
                    case 0: //Electric producer
                        text_info.setText(getResources().getString(R.string.edit_data_info_type_1));
                        text_unit_info.setText(getResources().getString(R.string.edit_data_unit_watt));
                        break;
                    case 1: //Electric consumer
                        text_info.setText(getResources().getString(R.string.edit_data_info_type_2));
                        text_unit_info.setText(getResources().getString(R.string.edit_data_unit_watt));
                        break;
                    case 2: //Energy by distance
                        text_info.setText(getResources().getString(R.string.edit_data_info_type_3));
                        text_unit_info.setText(getResources().getString(R.string.edit_data_unit_joule));
                        break;
                    case 3: //Mass energy content
                        text_info.setText(getResources().getString(R.string.edit_data_info_type_4));
                        text_unit_info.setText(getResources().getString(R.string.edit_data_unit_joule));
                        break;
                    case 4: //Volume energy content
                        text_info.setText(getResources().getString(R.string.edit_data_info_type_5));
                        text_unit_info.setText(getResources().getString(R.string.edit_data_unit_joule));
                        break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onPause() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_name.getWindowToken(), 0);
        super.onPause();
    }

    View.OnClickListener editButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(inputNotEmpty()) {
                if(containsCaseInsensitive(edit_name.getText().toString(), alreadyExistentCarriersNameList)) {
                    //Already existent, check if name is the original
                    if(edit_name.getText().toString().equalsIgnoreCase(editableCarrierName)) {
                        //Update object
                        if(updateItem()) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showErrorInputTooLong();
                        }
                    }
                } else {
                    //Update object
                    if(updateItem()) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        showErrorInputTooLong();
                    }
                }
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.invalid_input), Toast.LENGTH_LONG).show();
            }
        }
    };

    private boolean updateItem() {
        if(!(new BigDecimal(edit_energy.getText().toString()).compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) == 1)) {
            BigDecimal  energy = new BigDecimal(edit_energy.getText().toString());
            if(energy.longValue() == 0) {
                return false;
            }
            Carrier new_carrier = new Carrier(edit_name.getText().toString(), autoComplete_category.getText().toString(), null, energy.longValue(), true, editableCarrier.get_favorite());
            switch (spinner_type.getSelectedItemPosition()) {
                case 0: //Capacity
                    new_carrier.set_unit(getResources().getString(R.string.carrier_type_db_capacity));
                    break;
                case 1: //Consumption
                    new_carrier.set_unit(getResources().getString(R.string.carrier_type_db_consumption));
                    break;
                case 2: //Volume consumption
                    new_carrier.set_unit(getResources().getString(R.string.carrier_type_db_volume_consumption));
                    break;
                case 3: //Content mass
                    new_carrier.set_unit(getResources().getString(R.string.carrier_type_db_content_mass));
                    break;
                case 4: //Content volume
                    new_carrier.set_unit(getResources().getString(R.string.carrier_type_db_content_volume));
                    break;
                default:
                    return false;
            }
            dbHelper.updateCarrier(editableCarrier.get_id(), new_carrier);
            return true;
        }
        return false;
    }

    private void showErrorInputTooLong() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getResources().getString(R.string.add_data_input_too_large_title))
                .setMessage(mContext.getResources().getString(R.string.add_data_input_too_large_message))
                .setNeutralButton(mContext.getResources().getString(R.string.dialog_ok), null)
                .show();
    }

    //Returns true if all input data is valid
    private boolean inputNotEmpty() {
        return !(edit_name.getText().toString().trim().length() == 0 ||
                autoComplete_category.getText().toString().trim().length() == 0 ||
                edit_energy.getText().toString().trim().length() == 0 ||
                new BigDecimal(edit_energy.getText().toString()).compareTo(BigDecimal.ZERO) == 0);
                //containsCaseInsensitive(edit_name.getText().toString(), alreadyExistentCarriersNameList));
    }

    private boolean containsCaseInsensitive(String s, List<String> list) {
        for(String string : list) {
            if(string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    TextWatcher editNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(containsCaseInsensitive(s.toString(), alreadyExistentCarriersNameList)) {
                if(editableCarrierName.equalsIgnoreCase(edit_name.getText().toString())) {
                    edit_name.setError(null);
                } else {
                    edit_name.setError(mContext.getResources().getString(R.string.name_already_exists));
                }
            } else {
                edit_name.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    View.OnTouchListener editNameOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //final int DRAWABLE_LEFT = 0;
            //final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            //final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (edit_name.getRight() - edit_name.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    edit_name.setText("");
                    return true;
                }
            }
            return false;
        }
    };

    View.OnTouchListener editCategoryOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //final int DRAWABLE_LEFT = 0;
            //final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            //final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (autoComplete_category.getRight() - autoComplete_category.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    autoComplete_category.setText("");
                    return true;
                }
            }
            return false;
        }
    };

    CompoundButton.OnCheckedChangeListener fav_checked_change_listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Carrier new_carrier = editableCarrier;
            if (isChecked) {
                if (!new_carrier.get_favorite()) {
                    //update to true
                    new_carrier.set_favorite(true);
                    dbHelper.updateCarrier(editableCarrier.get_id(), new_carrier);
                }
            } else {
                if (new_carrier.get_favorite()) {
                    //update to false
                    new_carrier.set_favorite(false);
                    dbHelper.updateCarrier(editableCarrier.get_id(), new_carrier);
                }
            }
        }
    };
}