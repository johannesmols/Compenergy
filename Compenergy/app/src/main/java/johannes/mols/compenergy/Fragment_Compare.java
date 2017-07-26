/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Fragment_Compare extends Fragment {

    private Context mContext;
    private DatabaseHelper dbHelper;
    private AnimationDrawable animationDrawable;
    private static DecimalFormat df;
    private static DecimalFormat df_value;

    private TextView upperItemName;
    private TextView lowerItemName;
    private TextView upperItemEnergy;
    private TextView lowerItemEnergy;
    private TextView upperItemEnergyUnit;
    private TextView lowerItemEnergyUnit;
    private TextView upperItemCompareValue;
    private TextView lowerItemCompareValue;
    private TextView upperItemCompareUnit;
    private TextView lowerItemCompareUnit;

    private String key_upper;
    private String key_lower;
    private String key_comp_upper;
    private String key_comp_lower;

    private static final int REQUEST_CODE_SELECT = 0x7ce;
    private boolean upperOrLower = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare_layout, container, false);

        mContext = getContext();
        dbHelper = new DatabaseHelper(mContext, null, null, 1);

        upperItemName = (TextView) view.findViewById(R.id.fragment_compare_upper_item_name);
        lowerItemName = (TextView) view.findViewById(R.id.fragment_compare_lower_item_name);
        upperItemEnergy = (TextView) view.findViewById(R.id.fragment_compare_upper_item_energy);
        lowerItemEnergy = (TextView) view.findViewById(R.id.fragment_compare_lower_item_energy);
        upperItemEnergyUnit = (TextView) view.findViewById(R.id.fragment_compare_upper_item_energy_compare_unit);
        lowerItemEnergyUnit = (TextView) view.findViewById(R.id.fragment_compare_lower_item_energy_compare_unit);
        upperItemCompareValue = (TextView) view.findViewById(R.id.fragment_compare_upper_item_compare_value);
        lowerItemCompareValue = (TextView) view.findViewById(R.id.fragment_compare_lower_item_compare_value);
        upperItemCompareUnit = (TextView) view.findViewById(R.id.fragment_compare_upper_item_unit);
        lowerItemCompareUnit = (TextView) view.findViewById(R.id.fragment_compare_lower_item_unit);

        //See if the fragment is opened for the first time
        String key = getString(R.string.key_first_start);
        SharedPreferences prefs = getActivity().getSharedPreferences(key, Context.MODE_PRIVATE);
        if(prefs.getBoolean(key, true)) {
            //First started, shuffle
            shuffle();
        } else {
            //Load old carriers
            SharedPreferences pref1 = getActivity().getSharedPreferences(key_upper, Context.MODE_PRIVATE);
            SharedPreferences pref2 = getActivity().getSharedPreferences(key_lower, Context.MODE_PRIVATE);
            try {
                Carrier upper = dbHelper.getCarriersWithName(pref1.getString(key_upper, "")).get(0);
                Carrier lower = dbHelper.getCarriersWithName(pref2.getString(key_lower, "")).get(0);
                compareItems(upper, lower);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        prefs.edit().putBoolean(key, false).apply();

        setHasOptionsMenu(true);

        animationDrawable = (AnimationDrawable) view.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);

        upperItemName.setOnClickListener(upperNameClick);
        lowerItemName.setOnClickListener(lowerNameClick);

        upperItemCompareValue.setOnClickListener(upperItemCompareValueClick);
        lowerItemCompareValue.setOnClickListener(lowerItemCompareValueClick);

        //Number formatting
        df = new DecimalFormat();
        df.setGroupingUsed(true);
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
        df.setDecimalFormatSymbols(symbols);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH); //Use dots in all language settings as decimal separator
        df_value = (DecimalFormat) numberFormat;
        df_value.setGroupingUsed(false);
        df_value.setMaximumFractionDigits(2);

        key_upper = mContext.getString(R.string.key_upper);
        key_lower = mContext.getString(R.string.key_lower);
        key_comp_upper = mContext.getString(R.string.key_comp_upper);
        key_comp_lower = mContext.getString(R.string.key_comp_lower);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.nav_item_compare);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        if(animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_compare_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_compare_toolbar_shuffle:
                shuffle();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private Carrier getItem(Integer id) {
        return dbHelper.getCarrierWithID(id).get(0);
    }

    private void displayItemInfo(boolean upperOrLower, Carrier item) {
        if(upperOrLower) {
            //Name
            upperItemName.setText(item.get_name());

            //Energy
            upperItemEnergy.setText(Util.format(item.get_energy()));
            if(item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_capacity)) || item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_consumption))) {
                if(item.get_energy() >= 1000) {
                    upperItemEnergy.append(getString(R.string.watt_lower_case));
                } else {
                    upperItemEnergy.append(" " + getString(R.string.watt_lower_case));
                }
            } else {
                if(item.get_energy() >= 1000) {
                    upperItemEnergy.append(getString(R.string.joule_lower_case));
                } else {
                    upperItemEnergy.append(" " + getString(R.string.joule_lower_case));
                }
            }

            //Energy unit
            if (item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_capacity))) {
                upperItemEnergyUnit.setText(getString(R.string.compare_type_capacity));
            } else if (item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_consumption))) {
                upperItemEnergyUnit.setText(getString(R.string.compare_type_consumption));
            } else if (item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_volume_consumption))) {
                upperItemEnergyUnit.setText(getString(R.string.compare_type_volume_consumption));
            } else if (item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_content_mass))) {
                upperItemEnergyUnit.setText(getString(R.string.compare_type_mass_content));
            } else if (item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_content_volume))) {
                upperItemEnergyUnit.setText(getString(R.string.compare_type_volume_content));
            }
        }
        else {
            //Name
            lowerItemName.setText(item.get_name());

            //Energy
            lowerItemEnergy.setText(Util.format(item.get_energy()));
            if(item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_capacity)) || item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_consumption))) {
                if(item.get_energy() > 1000) {
                    lowerItemEnergy.append(getString(R.string.watt_lower_case));
                } else {
                    lowerItemEnergy.append(" " + getString(R.string.watt_lower_case));
                }
            } else {
                if(item.get_energy() > 1000) {
                    lowerItemEnergy.append(getString(R.string.joule_lower_case));
                } else {
                    lowerItemEnergy.append(" " + getString(R.string.joule_lower_case));
                }
            }

            //Energy unit
            if (item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_capacity))) {
                lowerItemEnergyUnit.setText(getString(R.string.compare_type_capacity));
            } else if (item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_consumption))) {
                lowerItemEnergyUnit.setText(getString(R.string.compare_type_consumption));
            } else if (item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_volume_consumption))) {
                lowerItemEnergyUnit.setText(getString(R.string.compare_type_volume_consumption));
            } else if (item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_content_mass))) {
                lowerItemEnergyUnit.setText(getString(R.string.compare_type_mass_content));
            } else if (item.get_unit().equalsIgnoreCase(getString(R.string.carrier_type_db_content_volume))) {
                lowerItemEnergyUnit.setText(getString(R.string.compare_type_volume_content));
            }
        }
    }

    private void compareItems(Carrier c1, Carrier c2) {
        displayItemInfo(true, c1);
        displayItemInfo(false, c2);

        List<String> result = CompareCarriers.compareCarriers(mContext, c1, c2);
        if(result != null) { //0: Value for upper item; 1: Value for lower item; 2: Unit for upper item; 3: Unit for lower item
            upperItemCompareValue.setText(result.get(0));
            lowerItemCompareValue.setText(result.get(1));
            upperItemCompareUnit.setText(result.get(2));
            lowerItemCompareUnit.setText(result.get(3));
        } else {
            upperItemCompareValue.setText("");
            lowerItemCompareValue.setText("");
            upperItemCompareUnit.setText("");
            lowerItemCompareUnit.setText("");
            Log.e("Unexpected", "Unexpected error in comparison");
        }

        //Save which items are compared
        SharedPreferences prefs1 = mContext.getSharedPreferences(key_upper, Context.MODE_PRIVATE);
        SharedPreferences prefs2 = mContext.getSharedPreferences(key_lower, Context.MODE_PRIVATE);
        prefs1.edit().putString(key_upper, upperItemName.getText().toString()).apply();
        prefs2.edit().putString(key_lower, lowerItemName.getText().toString()).apply();
    }

    private void compareItemsWithFixedUnit(Carrier c1, Carrier c2, long amount, boolean upperOrLower) {
        displayItemInfo(true, c1);
        displayItemInfo(false, c2);

        List<String> result = CompareCarriers.compareCarriers(mContext, c1, c2, amount, upperOrLower);
        if(result != null) { //0: Value for upper item; 1: Value for lower item; 2: Unit for upper item; 3: Unit for lower item
            upperItemCompareValue.setText(result.get(0));
            lowerItemCompareValue.setText(result.get(1));
            upperItemCompareUnit.setText(result.get(2));
            lowerItemCompareUnit.setText(result.get(3));
        } else {
            upperItemCompareValue.setText("");
            lowerItemCompareValue.setText("");
            upperItemCompareUnit.setText("");
            lowerItemCompareUnit.setText("");
            Log.e("Unexpected", "Unexpected error in comparison");
        }

        //Save which items are compared
        SharedPreferences prefs1 = mContext.getSharedPreferences(key_upper, Context.MODE_PRIVATE);
        SharedPreferences prefs2 = mContext.getSharedPreferences(key_lower, Context.MODE_PRIVATE);
        prefs1.edit().putString(key_upper, upperItemName.getText().toString()).apply();
        prefs2.edit().putString(key_lower, lowerItemName.getText().toString()).apply();
    }

    private void shuffle() {
        if(dbHelper.getCarrierCount() > 0) {
            List<Integer> id_list = dbHelper.getIdList();
            int idx1, idx2;
            int max = id_list.size() - 1;
            int min = 0;
            Random r = new Random();
            idx1 = r.nextInt(max - min) + min;
            do {
                idx2 = r.nextInt(max - min) + min;
            } while (idx2 == idx1);
            int id1 = id_list.get(idx1);
            int id2 = id_list.get(idx2);

            Carrier item1 = getItem(id1);
            Carrier item2 = getItem(id2);

            compareItems(item1, item2);
        } else {
            Log.e("Error", "No items in the database");
        }
    }

    View.OnClickListener upperNameClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent selector = new Intent(mContext, ActSelectItem.class);
            upperOrLower = true;
            startActivityForResult(selector, REQUEST_CODE_SELECT);
        }
    };

    View.OnClickListener lowerNameClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent selector = new Intent(mContext, ActSelectItem.class);
            upperOrLower = false;
            startActivityForResult(selector, REQUEST_CODE_SELECT);
        }
    };

    View.OnClickListener upperItemCompareValueClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Load comparison value
            SharedPreferences pref_item = getActivity().getSharedPreferences(key_upper, Context.MODE_PRIVATE);
            SharedPreferences prefs_upper = mContext.getSharedPreferences(key_comp_upper, Context.MODE_PRIVATE);
            String current_value = prefs_upper.getString(key_comp_upper, "");
            try {
                Carrier item = dbHelper.getCarriersWithName(pref_item.getString(key_upper, "")).get(0);
                if (!current_value.equalsIgnoreCase("-1")) { //if the number is -1, it means it makes no sense to change that value, probably because it's a percentage or "times bigger" value
                    changeValue(new BigDecimal(current_value), item.get_unit(), true);
                } else {
                    Toast.makeText(mContext, getString(R.string.cant_change_that_value), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener lowerItemCompareValueClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Load comparison value
            SharedPreferences pref_item = getActivity().getSharedPreferences(key_lower, Context.MODE_PRIVATE);
            SharedPreferences prefs_lower = mContext.getSharedPreferences(key_comp_lower, Context.MODE_PRIVATE);
            String current_value = prefs_lower.getString(key_comp_lower, "");
            try {
                Carrier item = dbHelper.getCarriersWithName(pref_item.getString(key_lower, "")).get(0);
                if (!current_value.equalsIgnoreCase("-1")) { //if the number is -1, it means it makes no sense to change that value, probably because it's a percentage or "times bigger" value
                    changeValue(new BigDecimal(current_value), item.get_unit(), false);
                } else {
                    Toast.makeText(mContext, getString(R.string.cant_change_that_value), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("InflateParams")
    private void changeValue(BigDecimal current_value, String unit, final boolean upperOrLower) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_change_value, null);
        dialogBuilder.setView(dialogView);

        final EditText edit = (EditText) dialogView.findViewById(R.id.dialog_change_value_edit);
        edit.setText(df_value.format(current_value));

        //Need to add more info and unit selection
        //Time is currently displayed in seconds for example

        dialogBuilder.setTitle(getString(R.string.dialog_edit_value_title));

        dialogBuilder.setPositiveButton(getString(R.string.dialog_compare), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Load old carriers
                SharedPreferences pref1 = getActivity().getSharedPreferences(key_upper, Context.MODE_PRIVATE);
                SharedPreferences pref2 = getActivity().getSharedPreferences(key_lower, Context.MODE_PRIVATE);
                try {
                    Carrier upper = dbHelper.getCarriersWithName(pref1.getString(key_upper, "")).get(0);
                    Carrier lower = dbHelper.getCarriersWithName(pref2.getString(key_lower, "")).get(0);
                    compareItemsWithFixedUnit(upper, lower, Long.parseLong(edit.getText().toString()), upperOrLower);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialogBuilder.setNegativeButton(getString(R.string.dialog_cancel), null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Show selected item
            SharedPreferences pref1 = getActivity().getSharedPreferences(key_upper, Context.MODE_PRIVATE);
            SharedPreferences pref2 = getActivity().getSharedPreferences(key_lower, Context.MODE_PRIVATE);
            int selectedID = data.getIntExtra(getString(R.string.act_selection_intent_result_key_do_not_change), -1);
            if(upperOrLower) {
                //Upper item changed
                Carrier upperItem = dbHelper.getCarrierWithID(selectedID).get(0);
                Carrier lowerItem = dbHelper.getCarriersWithName(pref2.getString(key_lower, "")).get(0);
                compareItems(upperItem, lowerItem);
            } else {
                //Lower item changed
                Carrier upperItem = dbHelper.getCarriersWithName(pref1.getString(key_upper, "")).get(0);
                Carrier lowerItem = dbHelper.getCarrierWithID(selectedID).get(0);
                compareItems(upperItem, lowerItem);
            }
        }
    }
}