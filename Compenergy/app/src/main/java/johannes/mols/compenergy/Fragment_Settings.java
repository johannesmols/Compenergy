/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class Fragment_Settings extends PreferenceFragmentCompat {

    private Context mContext;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        getActivity().setTitle(R.string.nav_item_settings);

        android.support.v7.preference.Preference resetButton = findPreference(getContext().getString(R.string.pref_database_reset_key));
        android.support.v7.preference.Preference deleteButton = findPreference(getContext().getString(R.string.pref_database_delete_key));
        deleteButton.setOnPreferenceClickListener(onPreferenceClickListener);
        resetButton.setOnPreferenceClickListener(onPreferenceClickListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = view.getContext();
    }

    private android.support.v7.preference.Preference.OnPreferenceClickListener onPreferenceClickListener = new android.support.v7.preference.Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
            if (preference.getKey().equals(getContext().getString(R.string.pref_database_reset_key))) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Confirm reset")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), getContext().getString(R.string.pref_database_hint_reset), Toast.LENGTH_SHORT).show();
                                DatabaseHelper db = new DatabaseHelper(mContext, null, null, 1);
                                db.dropTables();
                                db.addDefaultData();
                                db.addDatabaseVersionNumber();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
            else if (preference.getKey().equals(getContext().getString(R.string.pref_database_delete_key))) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Confirm deletion")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), getContext().getString(R.string.pref_database_hint_delete), Toast.LENGTH_SHORT).show();
                                DatabaseHelper db = new DatabaseHelper(mContext, null, null, 1);
                                db.deleteAllCarriers();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }

            return false;
        }
    };
}
