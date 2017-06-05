/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_About extends PreferenceFragmentCompat {

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        addPreferencesFromResource(R.xml.about);
        getActivity().setTitle(R.string.nav_item_about);

        android.support.v7.preference.Preference version = findPreference(getContext().getString(R.string.about_app_version_key));
        version.setSummary(BuildConfig.VERSION_NAME);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = view.getContext();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
