/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class ActMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final boolean DEVELOPER_MODE = false;

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main_nav_drawer_layout);

        //Set Preferences to start of the app
        String key = "compenergy.compare.first_start";
        SharedPreferences prefs = this.getSharedPreferences(key, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, true).apply();

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedScreen(R.id.content_act_compare);

        //Database
        createDbAysncIfNotExistent createDb = new createDbAysncIfNotExistent();
        createDb.execute(mContext);

        //Check for updated database
        checkForDbUpdateAsync checkForDbUpdateAsync = new checkForDbUpdateAsync(new checkForDbUpdateAsync.AsyncResponse() {
            @Override
            public void processFinish(Boolean output) {
                if(!output) {
                    new AlertDialog.Builder(mContext)
                            .setTitle(mContext.getResources().getString(R.string.db_update_available))
                            .setMessage(mContext.getResources().getString(R.string.confirm_db_update))
                            .setPositiveButton(mContext.getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    updateDatabase updateDatabase = new updateDatabase();
                                    updateDatabase.execute(mContext);
                                }
                            })
                            .setNegativeButton(mContext.getResources().getString(R.string.dialog_no), null)
                            .show();
                }
            }
        });
        checkForDbUpdateAsync.execute(mContext);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displaySelectedScreen(int id) {
        android.support.v4.app.Fragment fragment;

        switch (id) {
            case R.id.nav_compare: {
                fragment = new Fragment_Compare();
                break;
            }
            case R.id.nav_data: {
                fragment = new Fragment_Data();
                break;
            }
            case R.id.nav_add_data: {
                fragment = new Fragment_Add_Data();
                break;
            }
            case R.id.nav_favorites: {
                fragment = new Fragment_Favorites();
                break;
            }
            case R.id.nav_submit_data: {
                fragment = new Fragment_Submit_Data();
                break;
            }
            case R.id.nav_settings: {
                fragment = new Fragment_Settings();
                break;
            }
            case R.id.nav_about: {
                fragment = new Fragment_About();
                break;
            }
            case R.id.nav_help: {
                fragment = new Fragment_Help();
                break;
            }
            default: {
                fragment = new Fragment_Compare();
                break;
            }
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_act_compare, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }
}

class createDbAysncIfNotExistent extends AsyncTask<Context, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(Context... params) {
        Boolean result = false;
        DatabaseHelper db = new DatabaseHelper(params[0], null, null, 1);
        if (db.getAllCarriers().size() == 0) {
            db.addDefaultData();
            Log.i("AsyncTask", "Default Data added");
            result = true;
        }
        if(db.getAllDatabaseVersions().size() == 0) {
            db.addDatabaseVersionNumber();
            Log.i("AsyncTask", "Added Db Version");
            result = true;
        }

        return result;
    }
}

class checkForDbUpdateAsync extends AsyncTask<Context, Integer, Boolean> {

    interface AsyncResponse {
        void processFinish(Boolean output);
    }

    private AsyncResponse delegate = null;

    checkForDbUpdateAsync(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(Context... params) {
        final Context mContext = params[0];
        DatabaseHelper db = new DatabaseHelper(mContext, null, null, 1);
        if(!db.isDatabaseCurrent()) {
            Log.i("AsyncTask", "Database version NOT current");
            return false;
        }

        Log.i("AsyncTask", "Database version current");
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.processFinish(aBoolean);
    }
}

class updateDatabase extends AsyncTask<Context, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(Context... params) {
        final Context mContext = params[0];
        DatabaseHelper db = new DatabaseHelper(mContext, null, null, 1);
        db.dropTables();
        db.addDefaultData();
        db.setDatabaseVersion();
        Log.i("AsyncTask", "Updated database");
        return true;
    }
}