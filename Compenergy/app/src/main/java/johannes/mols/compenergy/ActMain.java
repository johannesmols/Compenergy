/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ActMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main_nav_drawer_layout);

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
        createDatabaseIfNotExistent();

        //Check for updated database
        DatabaseHelper db = new DatabaseHelper(this, null, null, 1);
        if(!db.isDatabaseCurrent()) {
            new AlertDialog.Builder(this)
                    .setTitle("A database update is available")
                    .setMessage("Do you want to update to the latest version? \nCustom data will be lost!")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(this, getString(R.string.pref_database_hint_reset), Toast.LENGTH_SHORT).show();
                            DatabaseHelper db = new DatabaseHelper(mContext, null, null, 1);
                            db.dropTables();
                            db.addDefaultData();
                            db.setDatabaseVersion();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void createDatabaseIfNotExistent() {
        DatabaseHelper db = new DatabaseHelper(this, null, null, 1); //Constructor automatically creates table if it doesn't exist
        if (db.getAllCarriers().size() == 0) {                       //Fill database with default items if size is 0, therefore just created
            db.addDefaultData();                                     //Database will also be refilled if it was emptied and the app was closed without adding new items
        }
        if(db.getAllDatabaseVersions().size() == 0) {
            db.addDatabaseVersionNumber();
        }
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
                fragment = new Fragment_Favorites_Data();
                break;
            }
            case R.id.nav_categories: {
                fragment = new Fragment_Categories();
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
