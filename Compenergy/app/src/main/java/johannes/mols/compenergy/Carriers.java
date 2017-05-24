/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

class Carriers {

    private int _id;
    private String _name;
    private String _category;
    private String _unit;
    private long _energy;
    private Boolean _custom;
    private Boolean _favorite;

    Carriers(int id, String name, String category, String unit, long energy, Boolean custom, Boolean favorite) {
        this._id = id;
        this._name = name;
        this._category = category;
        this._unit = unit;
        this._energy = energy;
        this._custom = custom;
        this._favorite = favorite;
    }

    Carriers(String name, String category, String unit, long energy, Boolean custom, Boolean favorite) {
        this._id = -1;
        this._name = name;
        this._category = category;
        this._unit = unit;
        this._energy = energy;
        this._custom = custom;
        this._favorite = favorite;
    }

    //setters should not be used, use SQL to update data

    public int get_id() {
        return _id;
    }

    String get_name() {
        return _name;
    }

    String get_category() {
        return _category;
    }

    String get_unit() {
        return _unit;
    }

    long get_energy() {
        return _energy;
    }

    Boolean get_custom() {
        return _custom;
    }

    Boolean get_favorite() { return _favorite; }
}