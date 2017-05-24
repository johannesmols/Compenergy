/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class DatabaseHelper extends SQLiteOpenHelper {

    //General Database information
    private static final byte DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Compenergy.db";

    //Items table
    private static final String TABLE_CARRIERS_NAME = "carriers";
    private static final String CARRIER_ID = "id";
    private static final String CARRIER_NAME = "name";
    private static final String CARRIER_CATEGORY = "category";
    private static final String CARRIER_UNIT = "unit";
    private static final String CARRIER_ENERGY = "energy";
    private static final String CARRIER_CUSTOM = "custom";

    //Constructor
    DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        table_carriers_exist();
    }

    //Create a table if it doesn't exist
    private void table_carriers_exist() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            Cursor c = db.query(TABLE_CARRIERS_NAME, null, null, null, null, null, null);
            c.close();
            //If it doesn't throw an Exception the able exists, do nothing further
        }
        catch (Exception e) {
            Log.d(DatabaseHelper.class.toString(), TABLE_CARRIERS_NAME + "doesn't exist, creating...");
            onCreate(db);
        }
    }

    //Create Table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_CARRIERS_NAME + "(" +
                CARRIER_ID       + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                CARRIER_NAME     + " VARCHAR UNIQUE NOT NULL, " +
                CARRIER_CATEGORY + " VARCHAR NOT NULL, " +
                CARRIER_UNIT     + " VARCHAR NOT NULL, " +
                CARRIER_ENERGY   + " BIGINT NOT NULL, " + //BIGINT/LONG max val: 9,223,372,036,854,775,807 => unsigned: 18,446,744,073,709,551,615 (Java has no unsigned longs, use BigInteger if values exceed signed bigint)
                CARRIER_CUSTOM   + " BOOLEAN NOT NULL" +
                                   ");";
        db.execSQL(query);
    }

    //Upgrade table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARRIERS_NAME);
        onCreate(db);
    }

    //Get a dataset
    List<Carriers> getCarrier(String name) {
        List<Carriers> result = new ArrayList<>();
        try (SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT * FROM " + TABLE_CARRIERS_NAME + " WHERE " + CARRIER_NAME + "='" + name + "';";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if((c.getString(c.getColumnIndex(CARRIER_NAME)) != null) && (c.getString(c.getColumnIndex(CARRIER_CATEGORY)) != null)) {
                    result.add(new Carriers(c.getInt(c.getColumnIndex(CARRIER_ID)),
                            c.getString(c.getColumnIndex(CARRIER_NAME)),
                            c.getString(c.getColumnIndex(CARRIER_CATEGORY)),
                            c.getString(c.getColumnIndex(CARRIER_UNIT)),
                            c.getLong(c.getColumnIndex(CARRIER_ENERGY)),
                            c.getInt(c.getColumnIndex(CARRIER_CUSTOM)) > 0)); //getting boolean => getInt > 0
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        }
        return result;
    }

    //Add a dataset
    void addCarrier(Carriers carrier) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(CARRIER_NAME, carrier.get_name());
            values.put(CARRIER_CATEGORY, carrier.get_category());
            values.put(CARRIER_UNIT, carrier.get_unit());
            values.put(CARRIER_ENERGY, carrier.get_energy());
            values.put(CARRIER_CUSTOM, carrier.get_custom());
            db.insert(TABLE_CARRIERS_NAME, null, values);
            db.close();
        }
    }

    //Update a dataset
    void updateCarrier(Carriers carrier, Carriers new_carrier) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(CARRIER_NAME, new_carrier.get_name());
            values.put(CARRIER_CATEGORY, carrier.get_category());
            values.put(CARRIER_UNIT, carrier.get_unit());
            values.put(CARRIER_ENERGY, carrier.get_energy());
            values.put(CARRIER_CUSTOM, carrier.get_custom());
            db.update(TABLE_CARRIERS_NAME, values, CARRIER_NAME + "='" + carrier.get_name() + "'", null);
            db.close();
        }
    }

    //Delete a dataset
    void deleteCarrier(String carrier_name) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DELETE FROM " + TABLE_CARRIERS_NAME + " WHERE " + CARRIER_NAME + "=\"" + carrier_name + "\";");
        }
    }

    //Delete all datasets
    void deleteAllCarriers() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DELETE FROM " + TABLE_CARRIERS_NAME + " WHERE 1");
        }
    }

    //Drops table and creates a new one, shouldn't be called, use delete all instead
    void dropTable(String table_name) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DROP TABLE IF EXISTS " + table_name);
            onCreate(db);
        }
    }

/* ---------------------------------------------------------------------------------------------------------------------------------- */

    //Print all names of entries in carrier table (only for testing)
    String databaseToString() {
        String dbString = "";
        try (SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT * FROM " + TABLE_CARRIERS_NAME + " WHERE 1";

            //Cursor point to a location in your results
            Cursor c = db.rawQuery(query, null);
            //Move to first row
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(CARRIER_NAME)) != null) {
                    dbString += c.getString(c.getColumnIndex(CARRIER_NAME));
                    dbString += "\n";
                }
                c.moveToNext();
            }
            c.close();
        }
        catch (Exception ex) {
            return ex.toString();
        }
        return dbString;
    }

/* ---------------------------------------------------------------------------------------------------------------------------------- */

}