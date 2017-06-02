/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fragment_Add_Data extends Fragment {

    private Context mContext;

    private EditText edit_name;
    private AutoCompleteTextView edit_category;
    private Spinner spinner_type;
    private EditText edit_energy;
    private Spinner spinner_energy_type;
    private EditText edit_unit_amount;
    private Spinner spinner_unit;
    private Button button_add;

    private DatabaseHelper dbHelper;

    private List<String> alreadyExistentCarriersNameList;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.nav_item_add_data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_data_layout, container, false);

        mContext = getContext();
        dbHelper = new DatabaseHelper(mContext, null, null, 1);
        edit_name = (EditText) view.findViewById(R.id.fragment_add_data_name_edit);
        edit_category = (InstantAutoComplete) view.findViewById(R.id.fragment_add_data_category_autocomplete_edit);
        spinner_type = (Spinner) view.findViewById(R.id.fragment_add_data_type_spinner);
        edit_energy = (EditText) view.findViewById(R.id.fragment_add_data_energy_edit);
        spinner_energy_type = (Spinner) view.findViewById(R.id.fragment_add_data_energy_type_spinner);
        edit_unit_amount = (EditText) view.findViewById(R.id.fragment_add_data_unit_amount_edit);
        spinner_unit = (Spinner) view.findViewById(R.id.fragment_add_data_unit_spinner);
        button_add = (Button) view.findViewById(R.id.fragment_add_data_add_button);

        fill_edit_category();
        fill_spinner_type();
        fill_spinner_energy_type();
        fill_spinner_unit();

        alreadyExistentCarriersNameList = new ArrayList<>(dbHelper.getAllCarriersAsStringList());

        edit_name.addTextChangedListener(editNameTextWatcher);

        edit_category.setOnTouchListener(editCategoryOnTouchListener);
        edit_name.setOnTouchListener(editNameOnTouchListener);

        spinner_type.setOnItemSelectedListener(spinnerTypeItemSelectedListener);

        button_add.setOnClickListener(addButtonClickListener);

        return view;
    }

    /* --- Filling Methods --- */

    private void fill_edit_category() {
        List<String> categories = dbHelper.getCategoryList();
        Collections.sort(categories, CustomComparators.ALPHABETICAL_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, categories);
        edit_category.setAdapter(adapter);
    }

    private void fill_spinner_type() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.spinner_carrier_types, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(adapter);
    }

    private void fill_spinner_energy_type() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.spinner_energy_type, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_energy_type.setAdapter(adapter);
    }

    //Default list at start
    private void fill_spinner_unit() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.unit_list_mass, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_unit.setAdapter(adapter);
    }

    /* --- On Button Click --- */

    View.OnClickListener addButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(validateInput()) {
                addItemToDatabase();
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.invalid_input), Toast.LENGTH_LONG).show();
            }
        }
    };

    //Returns true if all input data is valid
    private boolean validateInput() {
        int selItem = spinner_type.getSelectedItemPosition();
        if(selItem == 0 || selItem == 1) {
            return !(edit_name.getText().toString().trim().length() == 0 ||
                    containsCaseInsensitive(edit_name.getText().toString(), alreadyExistentCarriersNameList) ||
                    edit_category.getText().toString().trim().length() == 0 ||
                    edit_energy.getText().toString().trim().length() == 0 ||
                    new BigDecimal(edit_energy.getText().toString()).compareTo(BigDecimal.ZERO) == 0);
        } else {
            return !(edit_name.getText().toString().trim().length() == 0 ||
                    containsCaseInsensitive(edit_name.getText().toString(), alreadyExistentCarriersNameList) ||
                    edit_category.getText().toString().trim().length() == 0 ||
                    edit_energy.getText().toString().trim().length() == 0 ||
                    new BigDecimal(edit_energy.getText().toString()).compareTo(BigDecimal.ZERO) == 0 ||
                    edit_unit_amount.getText().toString().trim().length() == 0 ||
                    new BigDecimal(edit_unit_amount.getText().toString()).compareTo(BigDecimal.ZERO) == 0);
        }
    }

    private void addItemToDatabase() {
        String[] types = mContext.getResources().getStringArray(R.array.spinner_carrier_types);
        if(types.length >= spinner_type.getSelectedItemPosition() + 1) {
            switch (spinner_type.getSelectedItemPosition()) {
                case 0: //Electric producer
                    if(!addElectricProducer()) {
                        showErrorInputTooLong();
                    } else {
                        ItemAdded();
                    }
                    break;
                case 1: //Electric consumer
                    if(!addElectricConsumer()) {
                        showErrorInputTooLong();
                    } else {
                        ItemAdded();
                    }
                    break;
                case 2: //Energy by distance
                    if(!addEnergyByDistance()) {
                        showErrorInputTooLong();
                    } else {
                        ItemAdded();
                    }
                    break;
                case 3: //Mass energy content
                    if(!addMassEnergyContent()) {
                        showErrorInputTooLong();
                    } else {
                        ItemAdded();
                    }
                    break;
                case 4: //Volume energy content
                    if(!addVolumeEnergyContent()) {
                        showErrorInputTooLong();
                    } else {
                        ItemAdded();
                    }
                    break;
                case 5: //Vehicle
                    break;
                default:
                    break;
            }
        }
    }

    private void ItemAdded() {
        Toast.makeText(mContext, mContext.getResources().getString(R.string.item_added_to_db),Toast.LENGTH_LONG).show();

        edit_name.setText("");
        edit_category.setText("");
        edit_energy.setText("");
        edit_unit_amount.setText("");
    }

    private boolean addElectricProducer() {
        String name = edit_name.getText().toString().trim();
        String category = edit_category.getText().toString().trim();
        String unit = mContext.getResources().getString(R.string.carrier_type_db_capacity);
        BigDecimal input = new BigDecimal(String.valueOf(edit_energy.getText().toString()));
        BigDecimal input_result = UnitConverter.wattInputToWatt(spinner_energy_type.getSelectedItemPosition(), input);
        BigInteger energy_converted = input_result.toBigInteger();
        if(energy_converted.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) == 1 || energy_converted.compareTo(BigInteger.ZERO) == 0) {
            return false;
        }
        long energy = energy_converted.longValue();

        Log.i("Energy input", energy_converted.toString());

        if(!containsCaseInsensitive(name, alreadyExistentCarriersNameList)) {
            Carrier newCarrier = new Carrier(name, category, unit, energy, true, false);
            dbHelper.addCarrier(newCarrier);

            return true;
        } else {
            return false;
        }
    }

    private boolean addElectricConsumer() {
        String name = edit_name.getText().toString().trim();
        String category = edit_category.getText().toString().trim();
        String unit = mContext.getResources().getString(R.string.carrier_type_db_consumption);
        BigDecimal input = new BigDecimal(String.valueOf(edit_energy.getText().toString()));
        BigDecimal input_result = UnitConverter.wattInputToWatt(spinner_energy_type.getSelectedItemPosition(), input);
        BigInteger energy_converted = input_result.toBigInteger();
        if(energy_converted.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) == 1 || energy_converted.compareTo(BigInteger.ZERO) == 0) {
            return false;
        }
        long energy = energy_converted.longValue();

        Log.i("Energy input", energy_converted.toString());

        if(!containsCaseInsensitive(name, alreadyExistentCarriersNameList)) {
            Carrier newCarrier = new Carrier(name, category, unit, energy, true, false);
            dbHelper.addCarrier(newCarrier);

            return true;
        } else {
            return false;
        }
    }

    private boolean addEnergyByDistance() {
        String name = edit_name.getText().toString().trim();
        String category = edit_category.getText().toString().trim();
        String unit = mContext.getResources().getString(R.string.carrier_type_db_volume_consumption);
        BigDecimal energy_input = new BigDecimal(String.valueOf(edit_energy.getText().toString()));
        BigDecimal amount_input = new BigDecimal(String.valueOf(edit_unit_amount.getText().toString()));

        BigInteger energy_result = UnitConverter.energyInputToJoule(spinner_energy_type.getSelectedItemPosition(), energy_input);
        BigDecimal amount_result = UnitConverter.distanceInputToKilometre(spinner_unit.getSelectedItemPosition(), amount_input);
        BigDecimal factor = new BigDecimal(100).divide(amount_result, 20, BigDecimal.ROUND_HALF_UP);
        BigInteger energy_normalized_to_100km = new BigDecimal(energy_result).multiply(factor).toBigInteger();
        if(energy_normalized_to_100km.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) == 1 || energy_normalized_to_100km.compareTo(BigInteger.ZERO) == 0) {
            return false;
        }
        long energy = energy_normalized_to_100km.longValue();

        Log.i("Energy input", energy_input.toString());
        Log.i("Amount input", amount_input.toString());
        Log.i("Energy in Joule", energy_result.toString());
        Log.i("Distance in km", amount_result.toString());
        Log.i("Factor", factor.toString());
        Log.i("Normalized energy", energy_normalized_to_100km.toString());

        if(!containsCaseInsensitive(name, alreadyExistentCarriersNameList)) {
            Carrier newCarrier = new Carrier(name, category, unit, energy, true, false);
            dbHelper.addCarrier(newCarrier);

            return true;
        } else {
            return false;
        }
    }

    private boolean addMassEnergyContent() {
        String name = edit_name.getText().toString().trim();
        String category = edit_category.getText().toString().trim();
        String unit = mContext.getResources().getString(R.string.carrier_type_db_content_mass);
        BigDecimal energy_input = new BigDecimal(String.valueOf(edit_energy.getText().toString()));
        BigDecimal amount_input = new BigDecimal(String.valueOf(edit_unit_amount.getText().toString()));

        BigInteger energy_result = UnitConverter.energyInputToJoule(spinner_energy_type.getSelectedItemPosition(), energy_input);
        BigDecimal amount_result = UnitConverter.massInputToKilogram(spinner_unit.getSelectedItemPosition(), amount_input);
        BigDecimal factor = new BigDecimal(1).divide(amount_result, 20, BigDecimal.ROUND_HALF_UP);
        BigInteger energy_normalized_to_1kg = new BigDecimal(energy_result).multiply(factor).toBigInteger();
        if(energy_normalized_to_1kg.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) == 1 || energy_normalized_to_1kg.compareTo(BigInteger.ZERO) == 0) {
            return false;
        }
        long energy = energy_normalized_to_1kg.longValue();

        Log.i("Energy input", energy_input.toString());
        Log.i("Amount input", amount_input.toString());
        Log.i("Energy in Joule", energy_result.toString());
        Log.i("Mass in kg", amount_result.toString());
        Log.i("Factor", factor.toString());
        Log.i("Normalized energy", energy_normalized_to_1kg.toString());

        if(!containsCaseInsensitive(name, alreadyExistentCarriersNameList)) {
            Carrier newCarrier = new Carrier(name, category, unit, energy, true, false);
            dbHelper.addCarrier(newCarrier);

            return true;
        } else {
            return false;
        }
    }

    private boolean addVolumeEnergyContent() {
        String name = edit_name.getText().toString().trim();
        String category = edit_category.getText().toString().trim();
        String unit = mContext.getResources().getString(R.string.carrier_type_db_content_mass);
        BigDecimal energy_input = new BigDecimal(String.valueOf(edit_energy.getText().toString()));
        BigDecimal amount_input = new BigDecimal(String.valueOf(edit_unit_amount.getText().toString()));

        BigInteger energy_result = UnitConverter.energyInputToJoule(spinner_energy_type.getSelectedItemPosition(), energy_input);
        BigDecimal amount_result = UnitConverter.volumeInputToLitre(spinner_unit.getSelectedItemPosition(), amount_input);
        BigDecimal factor = new BigDecimal(1).divide(amount_result, 20, BigDecimal.ROUND_HALF_UP);
        BigInteger energy_normalized_to_1l = new BigDecimal(energy_result).multiply(factor).toBigInteger();
        if(energy_normalized_to_1l.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) == 1 || energy_normalized_to_1l.compareTo(BigInteger.ZERO) == 0) {
            return false;
        }
        long energy = energy_normalized_to_1l.longValue();

        Log.i("Energy input", energy_input.toString());
        Log.i("Amount input", amount_input.toString());
        Log.i("Energy in Joule", energy_result.toString());
        Log.i("Volume in l", amount_result.toString());
        Log.i("Factor", factor.toString());
        Log.i("Normalized energy", energy_normalized_to_1l.toString());

        if(!containsCaseInsensitive(name, alreadyExistentCarriersNameList)) {
            Carrier newCarrier = new Carrier(name, category, unit, energy, true, false);
            dbHelper.addCarrier(newCarrier);

            return true;
        } else {
            return false;
        }
    }

    private void showErrorInputTooLong() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getResources().getString(R.string.add_data_input_too_large_title))
                .setMessage(mContext.getResources().getString(R.string.add_data_input_too_large_message))
                .setNeutralButton(mContext.getResources().getString(R.string.dialog_ok), null)
                .show();
    }

    /* --- On Text Change Events (EditText) --- */

    TextWatcher editNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(containsCaseInsensitive(s.toString(), alreadyExistentCarriersNameList)) {
                edit_name.setError(mContext.getResources().getString(R.string.name_already_exists));
            } else {
                edit_name.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean containsCaseInsensitive(String s, List<String> list) {
        for(String string : list) {
            if(string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    /* --- On Change Events (Spinner) --- */

    AdapterView.OnItemSelectedListener spinnerTypeItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] types = parent.getResources().getStringArray(R.array.spinner_carrier_types);
            if(types.length >= position + 1) {
                switch (position) {
                    case 0: //Electric producer => Energy is in Watt, hide the amount input and change energy type spinner items to Watt only
                        ArrayAdapter<CharSequence> adapter_0 = ArrayAdapter.createFromResource(mContext, R.array.spinner_energy_type_electric, R.layout.spinner_item);
                        adapter_0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_energy_type.setAdapter(adapter_0);
                        spinner_energy_type.setSelection(0);
                        edit_energy.setHint(R.string.add_data_energy_edit_hint);
                        edit_unit_amount.setVisibility(View.GONE);
                        spinner_unit.setVisibility(View.GONE);
                        break;
                    case 1: //Electric consumer
                        ArrayAdapter<CharSequence> adapter_1 = ArrayAdapter.createFromResource(mContext, R.array.spinner_energy_type_electric, R.layout.spinner_item);
                        adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_energy_type.setAdapter(adapter_1);
                        spinner_energy_type.setSelection(0);
                        edit_energy.setHint(R.string.add_data_energy_edit_hint);
                        edit_unit_amount.setVisibility(View.GONE);
                        spinner_unit.setVisibility(View.GONE);
                        break;
                    case 2: //Energy by distance
                        ArrayAdapter<CharSequence> adapter_2 = ArrayAdapter.createFromResource(mContext, R.array.spinner_energy_type, R.layout.spinner_item);
                        adapter_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_energy_type.setAdapter(adapter_2);
                        spinner_energy_type.setSelection(6); //Kilowatt hour
                        ArrayAdapter<CharSequence> adapter_2_1 = ArrayAdapter.createFromResource(mContext, R.array.unit_list_distance, R.layout.spinner_item);
                        adapter_2_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_unit.setAdapter(adapter_2_1);
                        edit_energy.setHint(R.string.add_data_energy_edit_hint);
                        edit_unit_amount.setHint(R.string.add_data_unit_amount_edit_hint);
                        spinner_unit.setSelection(5); //Kilometre
                        edit_unit_amount.setVisibility(View.VISIBLE);
                        spinner_unit.setVisibility(View.VISIBLE);
                        break;
                    case 3: //Mass energy content
                        ArrayAdapter<CharSequence> adapter_3 = ArrayAdapter.createFromResource(mContext, R.array.spinner_energy_type, R.layout.spinner_item);
                        adapter_3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_energy_type.setAdapter(adapter_3);
                        spinner_energy_type.setSelection(1); //Kilojoule
                        ArrayAdapter<CharSequence> adapter_3_1 = ArrayAdapter.createFromResource(mContext, R.array.unit_list_mass, R.layout.spinner_item);
                        adapter_3_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_unit.setAdapter(adapter_3_1);
                        edit_energy.setHint(R.string.add_data_energy_edit_hint);
                        edit_unit_amount.setHint(R.string.add_data_unit_amount_edit_hint);
                        spinner_unit.setSelection(8); //Kilogram
                        edit_unit_amount.setVisibility(View.VISIBLE);
                        spinner_unit.setVisibility(View.VISIBLE);
                        break;
                    case 4: //Volume energy content
                        ArrayAdapter<CharSequence> adapter_4 = ArrayAdapter.createFromResource(mContext, R.array.spinner_energy_type, R.layout.spinner_item);
                        adapter_4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_energy_type.setAdapter(adapter_4);
                        spinner_energy_type.setSelection(1);
                        ArrayAdapter<CharSequence> adapter_4_1 = ArrayAdapter.createFromResource(mContext, R.array.unit_list_volume, R.layout.spinner_item);
                        adapter_4_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_unit.setAdapter(adapter_4_1);
                        edit_energy.setHint(R.string.add_data_energy_edit_hint);
                        edit_unit_amount.setHint(R.string.add_data_unit_amount_edit_hint);
                        spinner_unit.setSelection(11); //Litre
                        edit_unit_amount.setVisibility(View.VISIBLE);
                        spinner_unit.setVisibility(View.VISIBLE);
                        break;
                    case 5: //Vehicle
                        ArrayAdapter<CharSequence> adapter_5 = ArrayAdapter.createFromResource(mContext, R.array.spinner_energy_type_if_vehicle, R.layout.spinner_item);
                        adapter_5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_energy_type.setAdapter(adapter_5);
                        spinner_energy_type.setSelection(0); //Gasoline
                        ArrayAdapter<CharSequence> adapter_5_1 = ArrayAdapter.createFromResource(mContext, R.array.unit_list_distance, R.layout.spinner_item);
                        adapter_5_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_unit.setAdapter(adapter_5_1);
                        edit_energy.setHint(R.string.add_data_energy_edit_hint_if_vehicle);
                        edit_unit_amount.setHint(R.string.add_data_unit_amount_edit_hint_if_vehicle);
                        edit_unit_amount.setText(String.valueOf(100));
                        spinner_unit.setSelection(3); //Kilometre
                        edit_unit_amount.setVisibility(View.VISIBLE);
                        spinner_unit.setVisibility(View.VISIBLE);
                        break;
                    default:
                        ArrayAdapter<CharSequence> adapter_default = ArrayAdapter.createFromResource(mContext, R.array.spinner_energy_type_electric, R.layout.spinner_item);
                        adapter_default.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_energy_type.setAdapter(adapter_default);
                        spinner_energy_type.setSelection(0);
                        edit_energy.setHint(R.string.add_data_energy_edit_hint);
                        edit_unit_amount.setVisibility(View.GONE);
                        spinner_unit.setVisibility(View.GONE);
                        break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.spinner_energy_type_electric, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_energy_type.setAdapter(adapter);
            spinner_energy_type.setSelection(0);
            edit_energy.setHint(R.string.add_data_energy_edit_hint);
            edit_unit_amount.setVisibility(View.GONE);
            spinner_unit.setVisibility(View.GONE);
        }
    };

    /* --- On Touch Events --- */

    //Clear the field when clicking the X
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

    //Clear the field when clicking the X
    View.OnTouchListener editCategoryOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //final int DRAWABLE_LEFT = 0;
            //final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            //final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (edit_category.getRight() - edit_category.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    edit_category.setText("");
                    return true;
                }
            }
            return false;
        }
    };
}
