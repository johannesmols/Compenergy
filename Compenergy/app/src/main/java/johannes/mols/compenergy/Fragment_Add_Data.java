/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

        edit_category.setOnTouchListener(editCategoryOnTouchListener);
        edit_name.setOnTouchListener(editNameOnTouchListener);

        return view;
    }

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
