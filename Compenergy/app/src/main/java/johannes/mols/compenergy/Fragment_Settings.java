/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Settings extends PreferenceFragmentCompat {

    private android.support.v7.preference.Preference resetButton;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addPreferencesFromResource(R.xml.preferences);
        getActivity().setTitle(R.string.nav_item_settings);

        resetButton = findPreference(getString(R.string.pref_database_reset_key));
        resetButton.setOnPreferenceClickListener(onPreferenceClickListener);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private android.support.v7.preference.Preference.OnPreferenceClickListener onPreferenceClickListener = new android.support.v7.preference.Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
            DatabaseHelper db = new DatabaseHelper(getContext(), null, null, 1);
            if (preference.getKey().equals(R.string.pref_database_reset_key)) {
                db.dropTableCarriers();
            }
            else if (preference.getKey().equals(R.string.pref_database_delete_key)) {
                db.deleteAllCarriers();
            }

            return false;
        }
    };
}
