/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Data extends Fragment {

    private EditText input;
    private Button add;
    private Button delete;
    private Button drop;
    private Button update;
    private Button getCarrier;
    private TextView output;
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

        input = (EditText) view.findViewById(R.id.fragment_data_edit_txt);
        add = (Button) view.findViewById(R.id.fragment_data_button_add);
        delete = (Button) view.findViewById(R.id.fragment_data_button_delete);
        drop = (Button) view.findViewById(R.id.fragment_data_button_drop_table);
        update = (Button) view.findViewById(R.id.fragment_data_button_update);
        getCarrier = (Button) view.findViewById(R.id.fragment_data_button_get_carrier);
        output = (TextView) view.findViewById(R.id.fragment_data_txt_output);
        dbHelper = new DatabaseHelper(getContext(), null, null, 1);
        printDatabase();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Carriers carrier = new Carriers(input.getText().toString(), "test_category", "test_unit", 0, true);
                dbHelper.addCarrier(carrier);
                printDatabase();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String delete_text = input.getText().toString();
                dbHelper.deleteCarrier(delete_text);
                printDatabase();
            }
        });

        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteAllCarriers();
                printDatabase();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Carriers carrier = new Carriers(input.getText().toString(), "test", "test", 0, true);
                dbHelper.updateCarrier(carrier, carrier);
                printDatabase();
            }
        });

        getCarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Carriers> result = new ArrayList<>(dbHelper.getCarrier(input.getText().toString()));
                //Do whatever you please with this
            }
        });

        return view;
    }

    public void printDatabase() {
        output.setText(dbHelper.databaseToString());
        input.setText("");
    }
}
