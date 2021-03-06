/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class Fragment_Settings extends PreferenceFragmentCompat {

    private Context mContext;

    private android.support.v7.preference.Preference resetButton;
    private android.support.v7.preference.Preference deleteButton;
    private android.support.v7.preference.CheckBoxPreference showGradient;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        getActivity().setTitle(R.string.nav_item_settings);

        resetButton = findPreference(getContext().getString(R.string.pref_database_reset_key));
        deleteButton = findPreference(getContext().getString(R.string.pref_database_delete_key));
        showGradient = (CheckBoxPreference) findPreference(getContext().getString(R.string.pref_appearance_show_gradient_key));
        deleteButton.setOnPreferenceClickListener(onPreferenceClickListener);
        resetButton.setOnPreferenceClickListener(onPreferenceClickListener);
        showGradient.setOnPreferenceChangeListener(onUseGradientPreferenceChange);

        loadPrefs();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = view.getContext();
    }

    private void loadPrefs() {
        String key = getString(R.string.pref_appearance_show_gradient_key);
        SharedPreferences prefs = getActivity().getSharedPreferences(key, Context.MODE_PRIVATE);
        if (prefs.getBoolean(key, true)) {
            //Gradient should be used
            showGradient.setChecked(true);
        } else {
            //Gradient should not be used
            showGradient.setChecked(false);
        }
    }

    private android.support.v7.preference.Preference.OnPreferenceClickListener onPreferenceClickListener = new android.support.v7.preference.Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
            if (preference.getKey().equals(getContext().getString(R.string.pref_database_reset_key))) {
                final ProgressDialog progDialog = new ProgressDialog(mContext);
                new AlertDialog.Builder(mContext)
                        .setTitle(mContext.getResources().getString(R.string.pref_confirm_reset))
                        .setMessage(mContext.getResources().getString(R.string.pref_confirmation))
                        .setPositiveButton(mContext.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetDatabase resetDatabase = new resetDatabase(new resetDatabase.AsyncResponse() {
                                    @Override
                                    public void processFinish(Boolean output) {
                                        if(output) {
                                            Log.i("AsyncTask", "Reset successful");
                                            Toast.makeText(getContext(), getContext().getString(R.string.pref_database_hint_reset), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.i("AsyncTask", "Reset failed");
                                            Toast.makeText(getContext(), getContext().getString(R.string.pref_database_hint_reset_failed), Toast.LENGTH_SHORT).show();
                                        }
                                        progDialog.dismiss();
                                    }
                                });
                                resetDatabase.execute(mContext);
                                if(resetDatabase.getStatus() == AsyncTask.Status.RUNNING) {
                                    progDialog.setMessage(mContext.getResources().getString(R.string.pref_database_loading_reset));
                                    progDialog.show();
                                }
                            }
                        })
                        .setNegativeButton(mContext.getResources().getString(R.string.dialog_no), null)
                        .show();
            }
            else if (preference.getKey().equals(getContext().getString(R.string.pref_database_delete_key))) {
                final ProgressDialog progDialog = new ProgressDialog(mContext);
                new AlertDialog.Builder(mContext)
                        .setTitle(mContext.getResources().getString(R.string.pref_confirm_deletion))
                        .setMessage(mContext.getResources().getString(R.string.pref_confirmation))
                        .setPositiveButton(mContext.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearDatabase clearDatabase = new clearDatabase(new clearDatabase.AsyncResponse() {
                                    @Override
                                    public void processFinish(Boolean output) {
                                        if(output) {
                                            Log.i("AsyncTask", "Deletion successful");
                                            Toast.makeText(getContext(), getContext().getString(R.string.pref_database_hint_delete), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.i("AsyncTask", "Deletion failed");
                                            Toast.makeText(getContext(), getContext().getString(R.string.pref_database_hint_delete_failed), Toast.LENGTH_SHORT).show();
                                        }
                                        progDialog.dismiss();
                                    }
                                });
                                clearDatabase.execute(mContext);
                                if(clearDatabase.getStatus() == AsyncTask.Status.RUNNING) {
                                    progDialog.setMessage(mContext.getResources().getString(R.string.pref_database_loading_clear));
                                    progDialog.show();
                                }
                            }
                        })
                        .setNegativeButton(mContext.getResources().getString(R.string.dialog_no), null)
                        .show();
            }

            return false;
        }
    };

    private android.support.v7.preference.CheckBoxPreference.OnPreferenceChangeListener onUseGradientPreferenceChange = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = getString(R.string.pref_appearance_show_gradient_key);
            SharedPreferences prefs = getActivity().getSharedPreferences(key, Context.MODE_PRIVATE);

            if ((boolean) newValue) {
                //Gradient should be used
                prefs.edit().putBoolean(key, true).apply();
            } else {
                //Gradient should not be used
                prefs.edit().putBoolean(key, false).apply();
            }

            return true;
        }
    };
}

class clearDatabase extends AsyncTask<Context, Integer, Boolean> {
    interface AsyncResponse {
        void processFinish(Boolean output);
    }

    private clearDatabase.AsyncResponse delegate = null;

    clearDatabase(clearDatabase.AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Context... params) {
        final Context mContext = params[0];
        try {
            DatabaseHelper db = new DatabaseHelper(mContext, null, null, 1);
            db.deleteAllCarriers();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.processFinish(aBoolean);
    }
}

class resetDatabase extends AsyncTask<Context, Integer, Boolean> {

    interface AsyncResponse {
        void processFinish(Boolean output);
    }

    private AsyncResponse delegate = null;

    resetDatabase(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Context... params) {
        final Context mContext = params[0];
        try {
            DatabaseHelper db = new DatabaseHelper(mContext, null, null, 1);
            db.dropTables();
            db.addDefaultData();
            db.addDatabaseVersionNumber();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.processFinish(aBoolean);
    }
}