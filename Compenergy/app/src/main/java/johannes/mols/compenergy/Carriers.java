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

    Carriers(String name, String category, String unit, long energy, Boolean custom) {
        this._name = name;
        this._category = category;
        this._unit = unit;
        this._energy = energy;
        this._custom = custom;
    }

    public void set_name(String name) {
        _name = name;
    }

    public void set_category(String category) {
        _category = category;
    }

    public void set_unit(String unit) {
        _unit = unit;
    }

    public void set_energy(long energy) {
        _energy = energy;
    }

    //You probably don't wanna do that though, should be set once at the start
    public void set_custom(Boolean custom) {
        _custom = custom;
    }

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
}