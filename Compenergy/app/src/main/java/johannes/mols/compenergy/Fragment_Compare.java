/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

public class Fragment_Compare extends Fragment {

    private Context mContext;
    private DatabaseHelper dbHelper;
    private AnimationDrawable animationDrawable;

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
        String key = "compenergy.compare.first_start";
        SharedPreferences prefs = getActivity().getSharedPreferences(key, Context.MODE_PRIVATE);
        if(prefs.getBoolean(key, true)) {
            //First started
            shuffle();
        }
        prefs.edit().putBoolean(key, false).apply();

        setHasOptionsMenu(true);

        animationDrawable = (AnimationDrawable) view.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(5000);

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

    private void getItem(String name) {

    }

    private void displayItem(boolean upperOrLower) {

    }

    private void compareItems() {

    }

    private void shuffle() {
        int itemCount = dbHelper.getCarrierCount();
        int max = itemCount - 1;
        int min = 0;
        int item1, item2;
        Random r = new Random();
        item1 = r.nextInt(max - min) + min;
        do {
            item2 = r.nextInt(max - min) + min;
        } while (item2 == item1);

        Log.i("Random Items", String.valueOf(item1) + ", " + String.valueOf(item2));
    }
}
