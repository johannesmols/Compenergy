/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_CARRIERS_NAME + "(" +
                CARRIER_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                CARRIER_NAME     + " VARCHAR NOT NULL, " +
                CARRIER_CATEGORY + " VARCHAR NOT NULL, " +
                CARRIER_UNIT     + " VARCHAR NOT NULL, " +
                CARRIER_ENERGY   + " BIGINT NOT NULL, " + //BIGINT/LONG max val: 9,223,372,036,854,775,807 => unsigned: 18,446,744,073,709,551,615 (Java has no unsigned longs, use BigInteger if values exceed signed bigint)
                CARRIER_CUSTOM   + " BOOLEAN NOT NULL" +
                                   ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARRIERS_NAME);
        onCreate(db);
    }

    //Add a dataset to the carriers table
    void addCarrier(Carriers carrier) {
        ContentValues values = new ContentValues();
        values.put(CARRIER_NAME, carrier.get_name());
        values.put(CARRIER_CATEGORY, carrier.get_category());
        values.put(CARRIER_UNIT, carrier.get_unit());
        values.put(CARRIER_ENERGY, carrier.get_energy());
        values.put(CARRIER_CUSTOM, carrier.get_custom());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CARRIERS_NAME, null, values);
        db.close();
    }

    //Delete a dataset from the carriers table
    void deleteCarrier(String carrier_name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CARRIERS_NAME + " WHERE " + CARRIER_NAME + "=\"" + carrier_name + "\";");
    }

    //Print something
    String databaseToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CARRIERS_NAME + " WHERE 1";

        //Cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to first row
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex(CARRIER_NAME)) != null) {
                dbString += c.getString(c.getColumnIndex(CARRIER_NAME));
                dbString += "\n";
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return  dbString;
    }
}