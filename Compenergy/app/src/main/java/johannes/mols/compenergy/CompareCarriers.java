/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;

import java.util.List;

class CompareCarriers {

    private static String unit_capacity;
    private static String unit_consumption;
    private static String unit_volume_consumption;
    private static String unit_mass_content;
    private static String unit_volume_content;

    CompareCarriers(Context mContext) {
        unit_capacity = mContext.getString(R.string.carrier_type_db_capacity);
        unit_consumption = mContext.getString(R.string.carrier_type_db_consumption);
        unit_volume_consumption = mContext.getString(R.string.carrier_type_db_volume_consumption);
        unit_mass_content = mContext.getString(R.string.carrier_type_db_content_mass);
        unit_volume_content = mContext.getString(R.string.carrier_type_db_content_volume);
    }

    static List<String> compareCarriers(Carrier c1, Carrier c2) {
        if (c1.get_unit().equalsIgnoreCase(unit_capacity)) {
            if(c2.get_unit().equalsIgnoreCase(unit_capacity)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_consumption)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_volume_consumption)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_mass_content)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_volume_content)) {

            } else {
                return null;
            }
        } else if(c1.get_unit().equalsIgnoreCase(unit_consumption)) {
            if(c2.get_unit().equalsIgnoreCase(unit_capacity)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_consumption)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_volume_consumption)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_mass_content)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_volume_content)) {

            } else {
                return null;
            }
        } else if(c1.get_unit().equalsIgnoreCase(unit_volume_consumption)) {
            if(c2.get_unit().equalsIgnoreCase(unit_capacity)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_consumption)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_volume_consumption)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_mass_content)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_volume_content)) {

            } else {
                return null;
            }
        } else if(c1.get_unit().equalsIgnoreCase(unit_mass_content)) {
            if(c2.get_unit().equalsIgnoreCase(unit_capacity)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_consumption)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_volume_consumption)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_mass_content)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_volume_content)) {

            } else {
                return null;
            }
        } else if(c1.get_unit().equalsIgnoreCase(unit_volume_content)) {
            if(c2.get_unit().equalsIgnoreCase(unit_capacity)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_consumption)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_volume_consumption)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_mass_content)) {

            } else if (c2.get_unit().equalsIgnoreCase(unit_volume_content)) {

            } else {
                return null;
            }
        } else {
            return null;
        }

        //Delete
        return null;
    }
}