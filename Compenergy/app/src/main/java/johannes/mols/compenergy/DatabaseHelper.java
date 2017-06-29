/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;

    //General Database information
    private static final int DATABASE_VERSION_DO_NOT_CHANGE = 1; //Do not change this number, it will call onUpgrade() and automatically rewrite the entire database, which is not wanted
    private static final int DATABASE_VERSION = 1; //Increment this number to give the user a notification that a new DB version is available and the choice if he wants to use it
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

    //Database version table
    private static final String TABLE_DB_VERSION = "database_version";
    private static final String COLUMN_DB_VERSION = "db_version";

    //Constructor
    DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION_DO_NOT_CHANGE);
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
                CARRIER_ENERGY   + " BIGINT NOT NULL, " + //BIGINT/LONG max val: 9223372036854775807 => unsigned: 18446744073709551615 (Java has no unsigned longs, use BigInteger if values exceed signed bigint)
                CARRIER_CUSTOM   + " BOOLEAN NOT NULL, " +
                CARRIER_FAVORITE + " BOOLEAN NOT NULL" +
                ");";
        db.execSQL(query);

        //Version control for the database
        query = "CREATE TABLE IF NOT EXISTS " + TABLE_DB_VERSION + " (" + COLUMN_DB_VERSION + " INTEGER NOT NULL);";
        db.execSQL(query);
    }

    void addDatabaseVersionNumber() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DB_VERSION, DATABASE_VERSION);
        db.insert(TABLE_DB_VERSION, null, values);
        db.close();
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

    List<String> getAllCarriersAsStringList() {
        List<String> result = new ArrayList<>();
        try (SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT " + CARRIER_NAME + " FROM " + TABLE_CARRIERS_NAME + " WHERE 1;";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if(c.getString(c.getColumnIndex(CARRIER_NAME)) != null) {
                    result.add(c.getString(c.getColumnIndex(CARRIER_NAME)));
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

    List<Carrier> getCarrierWithID(int id) {
        return getCarriers(CARRIER_ID + "='" + id + "'");
    }

    List<Carrier> getCarriersWithName(String name) {
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

    List<Carrier> getFavoritesWithCategory(String category) {
        return getCarriers(CARRIER_CATEGORY + "='" + category + "' AND " + CARRIER_FAVORITE + "= 1 ");
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

    List<String> getCategoryListThatContainsFavorites() {
        List<String> categories = new ArrayList<>();
        try (SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT DISTINCT " + CARRIER_CATEGORY + " FROM " + TABLE_CARRIERS_NAME + " WHERE " + CARRIER_FAVORITE + "=1;";

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

    List<Integer> getIdList() {
        List<Integer> ids = new ArrayList<>();
        try (SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT * FROM " + TABLE_CARRIERS_NAME + ";";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                ids.add(c.getInt(c.getColumnIndex(CARRIER_ID)));
                c.moveToNext();
            }
            c.close();
            db.close();
        }

        return ids;
    }

    List<Object> getCombinedCategoryCarrierList() {
        ArrayList<Object> list = new ArrayList<>();
        List<String> categoryList = getCategoryList();
        List<Carrier> carrierList = getAllCarriers();
        Collections.sort(carrierList, new CustomComparators.CarrierComparator());
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

    int getCarrierCount() {
        SQLiteDatabase db = getReadableDatabase();
        return (int)DatabaseUtils.queryNumEntries(db, TABLE_CARRIERS_NAME);
    }

    boolean isDatabaseCurrent() {
        List<Integer> db_version = new ArrayList<>();
        try (SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT " + COLUMN_DB_VERSION + " FROM " + TABLE_DB_VERSION + " WHERE 1;";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                db_version.add(c.getInt(c.getColumnIndex(COLUMN_DB_VERSION)));
                c.moveToNext();
            }
            c.close();
            db.close();
        }

        return db_version.size() == 1 && db_version.get(0) == DATABASE_VERSION;
    }

    void setDatabaseVersion() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DB_VERSION, DATABASE_VERSION);
            db.update(TABLE_DB_VERSION, values, COLUMN_DB_VERSION + "=?", null);
        }
    }

    List<Integer> getAllDatabaseVersions() {
        List<Integer> result = new ArrayList<>();
        try (SQLiteDatabase db = getWritableDatabase()) {
            String query = "SELECT * FROM " + TABLE_DB_VERSION + " WHERE 1;";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                result.add(c.getInt(c.getColumnIndex(COLUMN_DB_VERSION)));

                c.moveToNext();
            }
            c.close();
            db.close();
        }
        return result;
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
    void updateCarrier(int id, Carrier new_carrier) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(CARRIER_NAME, new_carrier.get_name());
            values.put(CARRIER_CATEGORY, new_carrier.get_category());
            values.put(CARRIER_UNIT, new_carrier.get_unit());
            values.put(CARRIER_ENERGY, new_carrier.get_energy());
            values.put(CARRIER_CUSTOM, new_carrier.get_custom());
            values.put(CARRIER_FAVORITE, new_carrier.get_favorite());
            db.update(TABLE_CARRIERS_NAME, values, CARRIER_ID + "=" + id, null);
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
    void dropTables() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARRIERS_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DB_VERSION);
            onCreate(db);
        }
    }
}
