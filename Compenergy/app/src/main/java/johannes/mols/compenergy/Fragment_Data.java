/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Fragment_Data extends Fragment {

    private Context mContext;

    private EditText input;
    private TextView output;
    private ListView listView;
    private DatabaseHelper dbHelper;

    ArrayAdapter<String> adapter;

    //Alphabetical sort
    private static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
        public int compare(String str1, String str2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
            if (res == 0) {
                res = str1.compareTo(str2);
            }
            return res;
        }
    };

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
        dbHelper = new DatabaseHelper(mContext, null, null, 1);
        listView = (ListView) view.findViewById(R.id.fragment_data_list_view);
        List<Carrier> carrierList = new ArrayList<>(dbHelper.getAllCarriers());
        List<String> carrierNameList = new ArrayList<>();
        for (Carrier carrier : carrierList) {
            carrierNameList.add(carrier.get_name());
        }
        Collections.sort(carrierNameList, ALPHABETICAL_ORDER);
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, carrierNameList);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        /* --- START: TESTING CODE, REMOVE WHEN LIST VIEW IMPLEMENTED --- */
        input = (EditText) view.findViewById(R.id.fragment_data_edit_txt);
        Button add = (Button) view.findViewById(R.id.fragment_data_button_add);
        Button delete = (Button) view.findViewById(R.id.fragment_data_button_delete);
        Button delete_all = (Button) view.findViewById(R.id.fragment_data_button_delete_all);
        Button drop = (Button) view.findViewById(R.id.fragment_data_button_drop_table);
        Button update = (Button) view.findViewById(R.id.fragment_data_button_update);
        Button getCarrier = (Button) view.findViewById(R.id.fragment_data_button_get_carrier);
        output = (TextView) view.findViewById(R.id.fragment_data_txt_output);
        dbHelper = new DatabaseHelper(getContext(), null, null, 1);
        printDatabase();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Carrier carrier = new Carrier(input.getText().toString(), "test_category", "test_unit", 0, true, false);
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

        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteAllCarriers();
                printDatabase();
            }
        });

        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.dropTableCarriers();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Carrier carrier = new Carrier(input.getText().toString(), "test", "test", 0, true, false);
                dbHelper.updateCarrier(carrier, carrier);
                printDatabase();
            }
        });

        getCarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Carrier> result = new ArrayList<>(dbHelper.getCarrierWithName(input.getText().toString()));
            }
        });
        /* --- END: TESTING CODE, REMOVE WHEN LIST VIEW IMPLEMENTED --- */

        return view;
    }

    public void printDatabase() {
        output.setText(dbHelper.databaseAllCarrierNamesToString());
        input.setText("");
    }
}
