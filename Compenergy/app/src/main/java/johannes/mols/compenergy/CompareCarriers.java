/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

class CompareCarriers {

    private static Context mContext;

    private static String unit_capacity;
    private static String unit_consumption;
    private static String unit_volume_consumption;
    private static String unit_mass_content;
    private static String unit_volume_content;

    private static void setup(Context context) {
        mContext = context;

        unit_capacity = mContext.getString(R.string.carrier_type_db_capacity);
        unit_consumption = mContext.getString(R.string.carrier_type_db_consumption);
        unit_volume_consumption = mContext.getString(R.string.carrier_type_db_volume_consumption);
        unit_mass_content = mContext.getString(R.string.carrier_type_db_content_mass);
        unit_volume_content = mContext.getString(R.string.carrier_type_db_content_volume);
    }

    static List<String> compareCarriers(Context context, Carrier c1, Carrier c2) {
        setup(context);

        if(c1 == null || c2 == null || c1.get_energy() == 0 || c2.get_energy() == 0) {
            return null;
        }

        List<String> result = new ArrayList<>(); //0: Value for upper item; 1: Value for lower item; 2: Unit for upper item; 3: Unit for lower item
        BigDecimal c1_energy = new BigDecimal(c1.get_energy());
        BigDecimal c2_energy = new BigDecimal(c2.get_energy());
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);

        if (c1.get_unit().equalsIgnoreCase(unit_capacity)) {
            if(c2.get_unit().equalsIgnoreCase(unit_capacity)) {
                //Percentage and "times bigger"
                if(c1_energy.compareTo(c2_energy) == 1) { //larger
                    BigDecimal timesBigger = c1_energy.divide(c2_energy, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(0, df.format(timesBigger));
                    BigDecimal percentage = c2_energy.divide(c1_energy, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(1, df.format(percentage) + " %");
                    result.add(2, mContext.getString(R.string.compare_capacity_capacity_larger));
                    result.add(3, mContext.getString(R.string.compare_capacity_capacity_smaller));
                    return result;
                } else if(c1_energy.compareTo(c2_energy) == 0) {
                    result.add(0, "1.0");
                    result.add(1, "1.0");
                    result.add(2, "values equal");
                    result.add(3, "values equal");
                    return result;
                } else {
                    BigDecimal percentage = c1_energy.divide(c2_energy, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(0, df.format(percentage) + " %");
                    BigDecimal timesBigger = c2_energy.divide(c1_energy, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(1, df.format(timesBigger));
                    result.add(2, mContext.getString(R.string.compare_capacity_capacity_smaller));
                    result.add(3, mContext.getString(R.string.compare_capacity_capacity_larger));
                    return result;
                }
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

        return null;
    }
}