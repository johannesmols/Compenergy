/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

class Carrier {

    private int _id;
    private String _name;
    private String _category;
    private String _unit;
    private long _energy;
    private Boolean _custom;
    private Boolean _favorite;

    //Used for getting carriers from the database, includes the ID
    Carrier(int id, String name, String category, String unit, long energy, Boolean custom, Boolean favorite) {
        this._id = id;
        this._name = name;
        this._category = category;
        this._unit = unit;
        this._energy = energy;
        this._custom = custom;
        this._favorite = favorite;
    }

    //Used for inserting or updating carriers in the database, not including the ID, it's not used
    Carrier(String name, String category, String unit, long energy, Boolean custom, Boolean favorite) {
        this._id = -1;
        this._name = name;
        this._category = category;
        this._unit = unit;
        this._energy = energy;
        this._custom = custom;
        this._favorite = favorite;
    }

    Carrier() {
        this._id = -1;
        this._name = "";
        this._category = "";
        this._unit = "";
        this._energy = 0;
        this._custom = false;
        this._favorite = false;
    }

    public int get_id() {
        return this._id;
    }

    String get_name() {
        return this._name;
    }

    String get_category() {
        return this._category;
    }

    String get_unit() {
        return this._unit;
    }

    long get_energy() {
        return this._energy;
    }

    Boolean get_custom() {
        return this._custom;
    }

    Boolean get_favorite() {
        return this._favorite;
    }

    void set_id(int id) {
        this._id = id;
    }

    void set_name(String name) {
        this._name = name;
    }

    void set_category(String category) {
        this._category = category;
    }

    void set_unit(String unit) {
        this._unit = unit;
    }

    void set_energy(long energy) {
        this._energy = energy;
    }

    void set_custom(Boolean custom) {
        this._custom = custom;
    }

    void set_favorite(Boolean favorite) {
        this._favorite = favorite;
    }
}