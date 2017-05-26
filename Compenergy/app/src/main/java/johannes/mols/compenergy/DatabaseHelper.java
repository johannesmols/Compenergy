/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;

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
    private static final String CARRIER_FAVORITE = "favorite";

    //Constructor
    DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        mContext = context;
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
                CARRIER_CUSTOM   + " BOOLEAN NOT NULL, " +
                CARRIER_FAVORITE + " BOOLEAN NOT NULL" +
                ");";
        db.execSQL(query);
    }

    void addDefaultData() {
        List<Carrier> carriers;
        XMLPullParserHandler parser = new XMLPullParserHandler();
        InputStream rawXML = mContext.getResources().openRawResource(R.raw.default_database);
        carriers = parser.parse(rawXML);

        for (int i = 0; i < carriers.size(); i++) {
            addCarrier(carriers.get(i));
        }
    }

    //Upgrade table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARRIERS_NAME);
        onCreate(db);
    }

    //Get a dataset
    private List<Carrier> getCarriers(String condition) {
        List<Carrier> result = new ArrayList<>();
        try (SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT * FROM " + TABLE_CARRIERS_NAME + " WHERE " + condition + ";";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if((c.getString(c.getColumnIndex(CARRIER_NAME)) != null) &&
                        (c.getString(c.getColumnIndex(CARRIER_CATEGORY)) != null) &&
                        (c.getString(c.getColumnIndex(CARRIER_UNIT)) != null))
                {
                    result.add(new Carrier(c.getInt(c.getColumnIndex(CARRIER_ID)),
                            c.getString(c.getColumnIndex(CARRIER_NAME)),
                            c.getString(c.getColumnIndex(CARRIER_CATEGORY)),
                            c.getString(c.getColumnIndex(CARRIER_UNIT)),
                            c.getLong(c.getColumnIndex(CARRIER_ENERGY)),
                            c.getInt(c.getColumnIndex(CARRIER_CUSTOM)) > 0,
                            c.getInt(c.getColumnIndex(CARRIER_FAVORITE)) > 0)); //getting boolean => getInt > 0
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        }
        return result;
    }

    List<Carrier> getAllCarriers() {
        return getCarriers("1");
    }

    List<Carrier> getCarrierWithName(String name) {
        return getCarriers(CARRIER_NAME + "='" + name + "'");
    }

    List<Carrier> getCarriersWithCategory(String category) {
        return getCarriers(CARRIER_CATEGORY + "='" + category + "'");
    }

    List<Carrier> getCarriersWithUnit(String unit) {
        return getCarriers(CARRIER_UNIT + "='" + unit + "'");
    }

    List<Carrier> getCarriersWithExactEnergy(long energy) {
        return getCarriers(CARRIER_ENERGY + "='" + energy + "'");
    }

    List<Carrier> getCarriersWithHigherEnergy(long energy) {
        return getCarriers(CARRIER_ENERGY + ">'" + energy + "'");
    }

    List<Carrier> getCarriersWithLowerEnergy(long energy) {
        return getCarriers(CARRIER_ENERGY + "<'" + energy + "'");
    }

    List<Carrier> getFavoriteCarriers() {
        return getCarriers(CARRIER_FAVORITE + "=true");
    }

    List<Carrier> getCustomCarriers() {
        return getCarriers(CARRIER_CUSTOM + "=true");
    }

    List<String> getCategoryList() {
        List<String> categories = new ArrayList<>();
        try (SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT DISTINCT " + CARRIER_CATEGORY + " FROM " + TABLE_CARRIERS_NAME + ";";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if(c.getString(c.getColumnIndex(CARRIER_CATEGORY)) != null) {
                    categories.add(c.getString(c.getColumnIndex(CARRIER_CATEGORY)));
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        }

        return categories;
    }

    List<Object> getCombinedCategoryCarrierList() {
        ArrayList<Object> list = new ArrayList<>();
        List<String> categoryList = getCategoryList();
        List<Carrier> carrierList = getAllCarriers();
        Collections.sort(categoryList, CustomComparators.ALPHABETICAL_ORDER);

        for(String category : categoryList) {
            list.add(category);
            for(Carrier carrier : carrierList) {
                if(carrier.get_category().equalsIgnoreCase(category)) {
                    list.add(carrier);
                }
            }
        }

        /* Output may look like this
         *
         * Category 1       (String)
         * Carrier A        (Carrier)
         * Carrier D        (Carrier)
         * Category 2       (String)
         * Carrier B        (Carrier)
         * Carrier C        (Carrier)
         * ...
         *
         */

        return list;
    }

    //Add a dataset
    void addCarrier(Carrier carrier) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(CARRIER_NAME, carrier.get_name());
            values.put(CARRIER_CATEGORY, carrier.get_category());
            values.put(CARRIER_UNIT, carrier.get_unit());
            values.put(CARRIER_ENERGY, carrier.get_energy());
            values.put(CARRIER_CUSTOM, carrier.get_custom());
            values.put(CARRIER_FAVORITE, carrier.get_favorite());
            db.insert(TABLE_CARRIERS_NAME, null, values);
            db.close();
        }
    }

    //Update a dataset
    void updateCarrier(Carrier carrier, Carrier new_carrier) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(CARRIER_NAME, new_carrier.get_name());
            values.put(CARRIER_CATEGORY, carrier.get_category());
            values.put(CARRIER_UNIT, carrier.get_unit());
            values.put(CARRIER_ENERGY, carrier.get_energy());
            values.put(CARRIER_CUSTOM, carrier.get_custom());
            values.put(CARRIER_FAVORITE, carrier.get_favorite());
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
    void dropTableCarriers() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARRIERS_NAME);
            onCreate(db);
        }
    }

/* ---------------------------------------------------------------------------------------------------------------------------------- */

    //Print all names of entries in carrier table (only for testing)
    String databaseAllCarrierNamesToString() {
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
