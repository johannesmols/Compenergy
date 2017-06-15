/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

class CompareCarriers {

    private static Context mContext;

    private static String unit_capacity;
    private static String unit_consumption;
    private static String unit_volume_consumption;
    private static String unit_mass_content;
    private static String unit_volume_content;

    private static String com_values_equal;
    private static String com_percentage;
    private static String com_times_larger;
    private static String com_time;
    private static String com_seconds;
    private static String com_minutes;
    private static String com_hours;
    private static String com_days;

    private static DatabaseHelper dbHelper;
    private static DecimalFormat df;

    private static void setup(Context context) {
        mContext = context;

        dbHelper = new DatabaseHelper(mContext, null, null, 1);
        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);

        unit_capacity = mContext.getString(R.string.carrier_type_db_capacity);
        unit_consumption = mContext.getString(R.string.carrier_type_db_consumption);
        unit_volume_consumption = mContext.getString(R.string.carrier_type_db_volume_consumption);
        unit_mass_content = mContext.getString(R.string.carrier_type_db_content_mass);
        unit_volume_content = mContext.getString(R.string.carrier_type_db_content_volume);

        com_values_equal = mContext.getString(R.string.com_values_equal);
        com_percentage = mContext.getString(R.string.com_percentage);
        com_times_larger = mContext.getString(R.string.com_times_larger);
        com_time = mContext.getString(R.string.com_time);
        com_seconds = mContext.getString(R.string.com_seconds);
        com_minutes = mContext.getString(R.string.com_minutes);
        com_hours = mContext.getString(R.string.com_hours);
        com_days = mContext.getString(R.string.com_days);
    }

    static List<String> compareCarriers(Context context, Carrier c1, Carrier c2) {
        setup(context);

        if(dbHelper.getCarrierCount() == 0 || c1 == null || c2 == null || c1.get_energy() == 0 || c2.get_energy() == 0) {
            return null;
        }

        //Find a random amount for the upper item which will be compared with the lower item
        int amount;
        int max, min;
        if(c1.get_unit().equalsIgnoreCase(unit_capacity)) {
            //Time in seconds
            max = 60 * 60; //One hour
            min = 60;      //One minute
        } else if (c1.get_unit().equalsIgnoreCase(unit_consumption)) {
            //Time in seconds
            max = 60 * 60; //One hour
            min = 60;      //One minute
        } else if (c1.get_unit().equalsIgnoreCase(unit_volume_consumption)) {
            //Kilometre
            max = 1000;
            min = 1;
        } else if (c1.get_unit().equalsIgnoreCase(unit_mass_content)) {
            //Kilogram
            max = 1000;
            min = 1;
        } else if (c1.get_unit().equalsIgnoreCase(unit_volume_content)) {
            //Litre
            max = 1000;
            min = 1;
        } else {
            return null;
        }
        amount = randomRange(max, min);

        return compareWithFixedUnitUpper(c1, c2, (long)amount);
    }

    //Comparing with given amounts
    static List<String> compareCarriers(Context context, Carrier c1, Carrier c2, int amount, String unit, boolean upperOrLower) {
        setup(context);

        if(dbHelper.getCarrierCount() == 0 || c1 == null || c2 == null || c1.get_energy() == 0 || c2.get_energy() == 0 || amount <= 0 || unit.trim().isEmpty()) {
            return null;
        }

        //Convert input unit to normalized input

        if(upperOrLower) {
            compareWithFixedUnitUpper(c1, c2, amount);
        } else {
            compareWithFixedUnitLower(c1, c2, amount);
        }

        return null;
    }

    //Compare with a certain amount given for the upper item
    private static List<String> compareWithFixedUnitUpper(Carrier c1, Carrier c2, long amount) {
        List<String> result = new ArrayList<>();
        BigDecimal e1 = new BigDecimal(c1.get_energy());
        BigDecimal e2 = new BigDecimal(c2.get_energy());

        String cat1 = c1.get_unit();
        String cat2 = c2.get_unit();

        if (cat1.equalsIgnoreCase(unit_capacity)) {
            if (cat2.equalsIgnoreCase(unit_capacity)) {
                //Both capacitors, can ignore amount
                if(e1.compareTo(e2) == 1) { //larger
                    BigDecimal timesBigger = e1.divide(e2, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(0, df.format(timesBigger));
                    BigDecimal percentage = e2.divide(e1, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(1, df.format(percentage) + " %");
                    result.add(2, mContext.getString(R.string.compare_capacity_capacity_larger));
                    result.add(3, mContext.getString(R.string.compare_capacity_capacity_smaller));
                    return result;
                } else if(e1.compareTo(e2) == 0) { //same
                    result.add(0, String.format(Locale.getDefault(), "%f.1f", 1.0));
                    result.add(1, String.format(Locale.getDefault(), "%f.1f", 1.0));
                    result.add(2, com_values_equal);
                    result.add(3, com_values_equal);
                    return result;
                } else { //smaller
                    BigDecimal percentage = e1.divide(e2, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(0, df.format(percentage) + " %");
                    BigDecimal timesBigger = e2.divide(e1, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(1, df.format(timesBigger));
                    result.add(2, mContext.getString(R.string.compare_capacity_capacity_smaller));
                    result.add(3, mContext.getString(R.string.compare_capacity_capacity_larger));
                    return result;
                }
            }
            else if (cat2.equalsIgnoreCase(unit_consumption)) {
                //Upper is electric producer, lower is electric consumer. Calculate how long the consumer can run with some time of producing
                //Amount = Time of producer => Time of consumer = Joule of producer (watt * time (amount)) / Wattage of consumer
                BigDecimal producer_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal time = producer_joule.divide(e2, 2, BigDecimal.ROUND_HALF_UP);
                String[] upperResult = findBestTimeUnit(amount);
                String[] lowerResult = findBestTimeUnit(time.longValue());

                result.add(0, upperResult[0]); //Upper time
                result.add(1, lowerResult[0]); //Lower time
                result.add(2, upperResult[1]);
                result.add(3, lowerResult[1]);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_volume_consumption)) {

            }
            else if (cat2.equalsIgnoreCase(unit_mass_content)) {

            }
            else if (cat2.equalsIgnoreCase(unit_volume_content)) {

            }
            else {
                return null;
            }
        }
        else if (cat1.equalsIgnoreCase(unit_consumption)) {
            if (cat2.equalsIgnoreCase(unit_capacity)) {

            }
            else if (cat2.equalsIgnoreCase(unit_consumption)) {

            }
            else if (cat2.equalsIgnoreCase(unit_volume_consumption)) {

            }
            else if (cat2.equalsIgnoreCase(unit_mass_content)) {

            }
            else if (cat2.equalsIgnoreCase(unit_volume_content)) {

            }
            else {
                return null;
            }
        }
        else if (cat1.equalsIgnoreCase(unit_volume_consumption)) {
            if (cat2.equalsIgnoreCase(unit_capacity)) {

            }
            else if (cat2.equalsIgnoreCase(unit_consumption)) {

            }
            else if (cat2.equalsIgnoreCase(unit_volume_consumption)) {

            }
            else if (cat2.equalsIgnoreCase(unit_mass_content)) {

            }
            else if (cat2.equalsIgnoreCase(unit_volume_content)) {

            }
            else {
                return null;
            }
        }
        else if (cat1.equalsIgnoreCase(unit_mass_content)) {
            if (cat2.equalsIgnoreCase(unit_capacity)) {

            }
            else if (cat2.equalsIgnoreCase(unit_consumption)) {

            }
            else if (cat2.equalsIgnoreCase(unit_volume_consumption)) {

            }
            else if (cat2.equalsIgnoreCase(unit_mass_content)) {

            }
            else if (cat2.equalsIgnoreCase(unit_volume_content)) {

            }
            else {
                return null;
            }
        }
        else if (cat1.equalsIgnoreCase(unit_volume_content)) {
            if (cat2.equalsIgnoreCase(unit_capacity)) {

            }
            else if (cat2.equalsIgnoreCase(unit_consumption)) {

            }
            else if (cat2.equalsIgnoreCase(unit_volume_consumption)) {

            }
            else if (cat2.equalsIgnoreCase(unit_mass_content)) {

            }
            else if (cat2.equalsIgnoreCase(unit_volume_content)) {

            }
            else {
                return null;
            }
        }
        else {
            return null;
        }

        return null;
    }

    //Compare with a certain amount given for the lower item
    private static List<String> compareWithFixedUnitLower(Carrier c1, Carrier c2, int amount) {
        return null;
    }

    private static String[] findBestTimeUnit(long seconds) {
        String[] result = new String[2];
        if (seconds < 60) { //smaller than one minute - display seconds
            result[0] = df.format(seconds);
            result[1] = com_seconds;
        } else if (seconds < 60 * 60) { //smaller than one hour - display minutes
            result[0] = df.format((double)seconds / 60);
            result[1] = com_minutes;
        } else if (seconds < 60 * 60 * 24) { //smaller than one day - display hours
            result[0] = df.format((double)seconds / (60 * 60));
            result[1] = com_hours;
        } else {
            result[0] = df.format((double)seconds / (60 * 60 * 24));
            result[1] = com_days;
        }

        return result;
    }

    private static int randomRange(int max, int min) {
        return new Random().nextInt((max - min) + 1) + min;
    }



    //Comparing without given amounts
    static List<String> compareCarriersOld(Context context, Carrier c1, Carrier c2) {
        setup(context);

        if(dbHelper.getCarrierCount() == 0) {
            return null;
        }

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