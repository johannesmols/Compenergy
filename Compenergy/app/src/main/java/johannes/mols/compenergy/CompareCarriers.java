/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.content.Context;
import android.content.SharedPreferences;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
    private static String com_times_bigger;
    private static String com_seconds;
    private static String com_minutes;
    private static String com_hours;
    private static String com_days;
    private static String com_years;
    private static String com_km;
    private static String com_kg;
    private static String com_litre;

    private static String key_comp_upper;
    private static String key_comp_lower;

    private static DatabaseHelper dbHelper;
    private static DecimalFormat df;

    private static void setup(Context context) {
        mContext = context;

        dbHelper = new DatabaseHelper(mContext, null, null, 1);
        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(true);
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
        df.setDecimalFormatSymbols(symbols);

        unit_capacity = mContext.getString(R.string.carrier_type_db_capacity);
        unit_consumption = mContext.getString(R.string.carrier_type_db_consumption);
        unit_volume_consumption = mContext.getString(R.string.carrier_type_db_volume_consumption);
        unit_mass_content = mContext.getString(R.string.carrier_type_db_content_mass);
        unit_volume_content = mContext.getString(R.string.carrier_type_db_content_volume);

        com_values_equal = mContext.getString(R.string.com_values_equal);
        com_percentage = mContext.getString(R.string.com_percentage);
        com_times_bigger = mContext.getString(R.string.com_times_bigger);
        com_seconds = mContext.getString(R.string.com_seconds);
        com_minutes = mContext.getString(R.string.com_minutes);
        com_hours = mContext.getString(R.string.com_hours);
        com_days = mContext.getString(R.string.com_days);
        com_years = mContext.getString(R.string.com_years);
        com_km = mContext.getString(R.string.com_km);
        com_kg = mContext.getString(R.string.com_kg);
        com_litre = mContext.getString(R.string.com_litre);

        key_comp_upper = mContext.getString(R.string.key_comp_upper);
        key_comp_lower = mContext.getString(R.string.key_comp_lower);
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
            max = 1000000;
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
    static List<String> compareCarriers(Context context, Carrier c1, Carrier c2, long amount, boolean upperOrLower) {
        setup(context);

        if(dbHelper.getCarrierCount() == 0 || c1 == null || c2 == null || c1.get_energy() == 0 || c2.get_energy() == 0 || amount <= 0) { // || unit.trim().isEmpty()) {
            return null;
        }

        if(upperOrLower) {
            return compareWithFixedUnitUpper(c1, c2, amount);
        } else {
            return compareWithFixedUnitLower(c1, c2, amount);
        }
    }

    //Compare with a certain amount given for the upper item
    private static List<String> compareWithFixedUnitUpper(Carrier c1, Carrier c2, long amount) {
        List<String> result = new ArrayList<>(); //0=upper value; 1=lower value; 2=upper unit; 3=lower unit
        BigDecimal e1 = new BigDecimal(c1.get_energy());
        BigDecimal e2 = new BigDecimal(c2.get_energy());

        String cat1 = c1.get_unit();
        String cat2 = c2.get_unit();

        if (cat1.equalsIgnoreCase(unit_capacity)) {
            if (cat2.equalsIgnoreCase(unit_capacity)) {
                //Both capacitors, can ignore amount
                if(e1.compareTo(e2) == 1) { //larger
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    BigDecimal timesBigger = e1.divide(e2, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(0, df.format(timesBigger));
                    BigDecimal percentage = e2.divide(e1, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(1, df.format(percentage) + " %");
                    result.add(2, com_times_bigger);
                    result.add(3, com_percentage);
                    return result;
                } else if(e1.compareTo(e2) == 0) { //same
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    result.add(0, String.format(Locale.getDefault(), "%.1f", 1.0));
                    result.add(1, String.format(Locale.getDefault(), "%.1f", 1.0));
                    result.add(2, com_values_equal);
                    result.add(3, com_values_equal);
                    return result;
                } else { //smaller
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    BigDecimal percentage = e1.divide(e2, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(0, df.format(percentage) + " %");
                    BigDecimal timesBigger = e2.divide(e1, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(1, df.format(timesBigger));
                    result.add(2, com_percentage);
                    result.add(3, com_times_bigger);
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

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(time);

                result.add(0, upperResult[0]); //Upper time
                result.add(1, lowerResult[0]); //Lower time
                result.add(2, upperResult[1]);
                result.add(3, lowerResult[1]);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_volume_consumption)) {
                //Upper is electric producer, lower is consumer by distance. Calculate how far the consumer can move with some time of producing
                //Amount = Time of producer => Distance of consumer = Joule of producer (watt * time (amount)) / Consumption of consumer * 100 (consumption is in 100km)
                BigDecimal producer_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal distance = producer_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100.0));
                String[] upperResult = findBestTimeUnit(amount);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(distance);

                result.add(0, upperResult[0]);
                result.add(1, df.format(distance));
                result.add(2, upperResult[1]);
                result.add(3, com_km);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_mass_content)) {
                //Upper is electric producer, lower is energy content by mass. Calculate how much time of producing is worth how much weight of the second item
                //Amount = Time of producer => Weight of mass content = Joule of producer (watt * time (amount)) / Mass energy content per kg in Joule
                BigDecimal producer_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal weight = producer_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);
                String[] upperResult = findBestTimeUnit(amount);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(weight);

                result.add(0, upperResult[0]);
                result.add(1, df.format(weight));
                result.add(2, upperResult[1]);
                result.add(3, com_kg);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_volume_content)) {
                //Upper is electric producer, lower is energy content by volume. Calculate how much time of producing is worth how much volume of the second item
                //Amount = Time of producer => Volume of volume content = Joule of producer (watt * time (amount)) / Volume energy content per litre in Joule
                BigDecimal producer_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal volume = producer_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);
                String[] upperResult = findBestTimeUnit(amount);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(volume);

                result.add(0, upperResult[0]);
                result.add(1, df.format(volume));
                result.add(2, upperResult[1]);
                result.add(3, com_litre);
                return result;
            }
            else {
                return null;
            }
        }
        else if (cat1.equalsIgnoreCase(unit_consumption)) {
            if (cat2.equalsIgnoreCase(unit_capacity)) {
                //Upper is electric consumer, lower is electric producer. Calculate how long the producer needs to generate the energy which the consumer uses in the given amount of time
                //Amount = Time of consumer => Time of producer = Joule of consumer (watt * time (amount)) / Wattage of producer
                BigDecimal consumer_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal time = consumer_joule.divide(e2, 2, BigDecimal.ROUND_HALF_UP);
                String[] upperResult = findBestTimeUnit(amount);
                String[] lowerResult = findBestTimeUnit(time.longValue());

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(time);

                result.add(0, upperResult[0]); //Upper time
                result.add(1, lowerResult[0]); //Lower time
                result.add(2, upperResult[1]);
                result.add(3, lowerResult[1]);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_consumption)) {
                //Both consumers, can ignore amount
                if(e1.compareTo(e2) == 1) { //larger
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    BigDecimal timesBigger = e1.divide(e2, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(0, df.format(timesBigger));
                    BigDecimal percentage = e2.divide(e1, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(1, df.format(percentage) + " %");
                    result.add(2, com_times_bigger);
                    result.add(3, com_percentage);
                    return result;
                } else if (e1.compareTo(e2) == 0) { //same
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    result.add(0, String.format(Locale.getDefault(), "%.1f", 1.0));
                    result.add(1, String.format(Locale.getDefault(), "%.1f", 1.0));
                    result.add(2, com_values_equal);
                    result.add(3, com_values_equal);
                    return result;
                } else { //smaller
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    BigDecimal percentage = e1.divide(e2, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(0, df.format(percentage) + " %");
                    BigDecimal timesBigger = e2.divide(e1, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(1, df.format(timesBigger));
                    result.add(2, com_percentage);
                    result.add(3, com_times_bigger);
                    return result;
                }
            }
            else if (cat2.equalsIgnoreCase(unit_volume_consumption)) {
                //Upper is electric consumer, lower is consumer by distance. Calculate how far the volume consumer can move with some time of consuming the other item
                //Amount = Time of consumer => Distance of volume consumer = Joule of consumer (watt * time (amount)) / Consumption of consumer * 100 (consumption is in 100km)
                BigDecimal electric_consumer_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal distance = electric_consumer_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100.0));
                String[] upperResult = findBestTimeUnit(amount);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(distance);

                result.add(0, upperResult[0]);
                result.add(1, df.format(distance));
                result.add(2, upperResult[1]);
                result.add(3, com_km);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_mass_content)) {
                //Upper is electric consumer, lower is energy content by mass. Calculate how much time of consuming is worth how much weight of the second item
                //Amount = Time of consumer => Weight of mass content = Joule of consumer (watt * time (amount)) / Mass energy content per kg in Joule
                BigDecimal consumer_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal weight = consumer_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);
                String[] upperResult = findBestTimeUnit(amount);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(weight);

                result.add(0, upperResult[0]);
                result.add(1, df.format(weight));
                result.add(2, upperResult[1]);
                result.add(3, com_kg);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_volume_content)) {
                //Upper is electric consumer, lower is energy content by volume. Calculate how much time of consuming is worth how much volume of the second item
                //Amount = Time of consumer => Volume of volume content = Joule of consumer (watt * time (amount)) / Volume energy content per litre in Joule
                BigDecimal consumer_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal volume = consumer_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);
                String[] upperResult = findBestTimeUnit(amount);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(volume);

                result.add(0, upperResult[0]);
                result.add(1, df.format(volume));
                result.add(2, upperResult[1]);
                result.add(3, com_litre);
                return result;
            }
            else {
                return null;
            }
        }
        else if (cat1.equalsIgnoreCase(unit_volume_consumption)) {
            if (cat2.equalsIgnoreCase(unit_capacity)) {
                //Upper is consumer by distance, lower is electric producer. Calculate how long the producer needs to produce for the distance of the consumer by distance
                //Amount = Distance in km => Time of producer = Consumption of consumer on distance (consumption in joule / 100 * amount (distance in km)) / wattage of electric producer
                BigDecimal volume_consumer_joule = e1.divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(amount));
                BigDecimal time = volume_consumer_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);
                String[] lowerResult = findBestTimeUnit(time.longValue());

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(time);

                result.add(0, df.format(amount));
                result.add(1, lowerResult[0]);
                result.add(2, com_km);
                result.add(3, lowerResult[1]);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_consumption)) {
                //Upper is consumer by distance, lower is electric consumer. Calculate how long the consumer needs to consume for the distance of the consumer by distance
                //Amount = Distance in km => Time of consumer = Consumption of consumer on distance (consumption in joule / 100 * amount (distance in km)) / wattage of electric consumer
                BigDecimal volume_consumer_joule = e1.divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(amount));
                BigDecimal time = volume_consumer_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);
                String[] lowerResult = findBestTimeUnit(time.longValue());

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(time);

                result.add(0, df.format(amount));
                result.add(1, lowerResult[0]);
                result.add(2, com_km);
                result.add(3, lowerResult[1]);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_volume_consumption)) {
                //Both consumers by distance, can ignore amount
                if(e1.compareTo(e2) == 1) { //larger
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    BigDecimal timesBigger = e1.divide(e2, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(0, df.format(timesBigger));
                    BigDecimal percentage = e2.divide(e1, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(1, df.format(percentage) + " %");
                    result.add(2, com_times_bigger);
                    result.add(3, com_percentage);
                    return result;
                } else if (e1.compareTo(e2) == 0) { //same
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    result.add(0, String.format(Locale.getDefault(), "%.1f", 1.0));
                    result.add(1, String.format(Locale.getDefault(), "%.1f", 1.0));
                    result.add(2, com_values_equal);
                    result.add(3, com_values_equal);
                    return result;
                } else { //smaller
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    BigDecimal percentage = e1.divide(e2, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(0, df.format(percentage) + " %");
                    BigDecimal timesBigger = e2.divide(e1, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(1, df.format(timesBigger));
                    result.add(2, com_percentage);
                    result.add(3, com_times_bigger);
                    return result;
                }
            }
            else if (cat2.equalsIgnoreCase(unit_mass_content)) {
                //Upper is consumer by distance, lower is mass energy content. Calculate the amount of mass of the lower item which is needed to travel the amount of distance with the upper consumer by distance
                //Amount = Distance in km => Mass of lower item to equal distance consumption = Consumption of consumer on distance (consumption in joule / 100 * amount (distance in km)) / joule of mass content per kg
                BigDecimal volume_consumer_joule = e1.divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(amount));
                BigDecimal mass = volume_consumer_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(mass);

                result.add(0, df.format(amount));
                result.add(1, df.format(mass));
                result.add(2, com_km);
                result.add(3, com_kg);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_volume_content)) {
                //Upper is consumer by distance, lower is volume energy content. Calculate the amount of volume of the lower item which is needed to travel the amount of distance with the upper consumer by distance
                //Amount = Distance in km => Volume of lower item to equal distance consumption = Consumption of consumer on distance (consumption in joule / 100 * amount (distance in km)) / joule of volume content per litre
                BigDecimal volume_consumer_joule = e1.divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(amount));
                BigDecimal volume = volume_consumer_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(volume);

                result.add(0, df.format(amount));
                result.add(1, df.format(volume));
                result.add(2, com_km);
                result.add(3, com_litre);
                return result;
            }
            else {
                return null;
            }
        }
        else if (cat1.equalsIgnoreCase(unit_mass_content)) {
            if (cat2.equalsIgnoreCase(unit_capacity)) {
                //Upper is energy content by mass, lower is electric producer. Calculate how much time the electric producer needs to produce the energy of the upper item with the given amount in kg
                //Amount = Mass of item in kg => Time of production = joule of upper item (energy content per kg * amount (kg)) / wattage of electric producer
                BigDecimal mass_content_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal time = mass_content_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);
                String[] lowerResult = findBestTimeUnit(time.longValue());

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(time);

                result.add(0, df.format(amount));
                result.add(1, lowerResult[0]);
                result.add(2, com_kg);
                result.add(3, lowerResult[1]);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_consumption)) {
                //Upper is energy content by mass, lower is electric consumer. Calculate how much time the electric consumer needs to consume the energy of the upper item with the given amount in kg
                //Amount = Mass of item in kg => Time of consumption = joule of upper item (energy content per kg * amount (kg)) / wattage of electric consumer
                BigDecimal mass_content_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal time = mass_content_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);
                String[] lowerResult = findBestTimeUnit(time.longValue());

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(time);

                result.add(0, df.format(amount));
                result.add(1, lowerResult[0]);
                result.add(2, com_kg);
                result.add(3, lowerResult[1]);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_volume_consumption)) {
                //Upper is energy content by mass, lower is consumer by distance. Calculate how far the consumer by distance can move with the amount of kg of the upper item
                //Amount = Mass of item in kg => Distance of consumer by distance = joule of upper item with amount (joule per kg * amount (kg)) / joule per km (joule per 100km / 100)
                BigDecimal mass_content_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal joule_per_km_of_consumer = e2.divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP);
                BigDecimal distance = mass_content_joule.divide(joule_per_km_of_consumer, 2, BigDecimal.ROUND_HALF_UP);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(distance);

                result.add(0, df.format(amount));
                result.add(1, df.format(distance));
                result.add(2, com_kg);
                result.add(3, com_km);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_mass_content)) {
                //Both mass energy content, can ignore amount
                if(e1.compareTo(e2) == 1) { //larger
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    BigDecimal timesBigger = e1.divide(e2, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(0, df.format(timesBigger));
                    BigDecimal percentage = e2.divide(e1, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(1, df.format(percentage) + " %");
                    result.add(2, com_times_bigger);
                    result.add(3, com_percentage);
                    return result;
                } else if (e1.compareTo(e2) == 0) { //same
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    result.add(0, String.format(Locale.getDefault(), "%.1f", 1.0));
                    result.add(1, String.format(Locale.getDefault(), "%.1f", 1.0));
                    result.add(2, com_values_equal);
                    result.add(3, com_values_equal);
                    return result;
                } else { //smaller
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    BigDecimal percentage = e1.divide(e2, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(0, df.format(percentage) + " %");
                    BigDecimal timesBigger = e2.divide(e1, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(1, df.format(timesBigger));
                    result.add(2, com_percentage);
                    result.add(3, com_times_bigger);
                    return result;
                }
            }
            else if (cat2.equalsIgnoreCase(unit_volume_content)) {
                //Upper is energy content by mass, lower is energy content by volume. Calculate how much volume of the lower item is needed to have the same energy as the amount of the upper item in kg
                //Amount = Mass of item in kg => Volume of lower item in litre = joule for mass (mass in kg * amount (kg)) / joule for volume per litre
                BigDecimal mass_content_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal volume = mass_content_joule.divide(e2, 2, BigDecimal.ROUND_HALF_UP);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(volume);

                result.add(0, df.format(amount));
                result.add(1, df.format(volume));
                result.add(2, com_kg);
                result.add(3, com_litre);
                return result;
            }
            else {
                return null;
            }
        }
        else if (cat1.equalsIgnoreCase(unit_volume_content)) {
            if (cat2.equalsIgnoreCase(unit_capacity)) {
                //Upper is volume energy content, lower is electric producer. Calculate how long the producer needs to run to have the equal energy as the amount of the upper item in litre
                //Amount = volume in litre => Time of producer = joules of volume (joule per litre * amount (litre)) / wattage of electric producer
                BigDecimal volume_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal time = volume_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);
                String[] lowerResult = findBestTimeUnit(time.longValue());

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(time);

                result.add(0, df.format(amount));
                result.add(1, lowerResult[0]);
                result.add(2, com_litre);
                result.add(3, lowerResult[1]);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_consumption)) {
                //Upper is volume energy content, lower is electric consumer. Calculate how long the consumer needs to run to have the equal energy as the amount of the upper item in litre
                //Amount = volume in litre => Time of consumer = joules of volume (joule per litre * amount (litre)) / wattage of electric consumer
                BigDecimal volume_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal time = volume_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);
                String[] lowerResult = findBestTimeUnit(time.longValue());

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(time);

                result.add(0, df.format(amount));
                result.add(1, lowerResult[0]);
                result.add(2, com_litre);
                result.add(3, lowerResult[1]);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_volume_consumption)) {
                //Upper is volume energy content, lower is consumer by distance. Calculate how far the lower item can move with the amount of energy of the upper item
                //Amount = volume in litre => Distance of consumer by distance = joule of amount (joule per litre * amount (litre)) / joule per kilometre (joule per 100km / 100)
                BigDecimal volume_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal consumption_per_km = e2.divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP);
                BigDecimal distance = volume_joule.divide(consumption_per_km, 10, BigDecimal.ROUND_HALF_UP);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(distance);

                result.add(0, df.format(amount));
                result.add(1, df.format(distance));
                result.add(2, com_litre);
                result.add(3, com_km);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_mass_content)) {
                //Upper is energy content by volume, lower is energy content by mass. Calculate how much mass of the lower item is needed to have the same energy as the amount of the upper item in litre
                //Amount = Volume of item in litre => Mass of lower item in kg = joule for volume (volume in litre * amount (litre)) / joule for mass per kg
                BigDecimal volume_content_joule = e1.multiply(new BigDecimal(amount));
                BigDecimal mass = volume_content_joule.divide(e2, 10, BigDecimal.ROUND_HALF_UP);

                saveAsUpperCompareResult(new BigDecimal(amount));
                saveAsLowerCompareResult(mass);

                result.add(0, df.format(amount));
                result.add(1, df.format(mass));
                result.add(2, com_litre);
                result.add(3, com_kg);
                return result;
            }
            else if (cat2.equalsIgnoreCase(unit_volume_content)) {
                //Both volume energy content, can ignore amount
                if(e1.compareTo(e2) == 1) { //larger
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    BigDecimal timesBigger = e1.divide(e2, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(0, df.format(timesBigger));
                    BigDecimal percentage = e2.divide(e1, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(1, df.format(percentage) + " %");
                    result.add(2, com_times_bigger);
                    result.add(3, com_percentage);
                    return result;
                } else if (e1.compareTo(e2) == 0) { //same
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    result.add(0, String.format(Locale.getDefault(), "%.1f", 1.0));
                    result.add(1, String.format(Locale.getDefault(), "%.1f", 1.0));
                    result.add(2, com_values_equal);
                    result.add(3, com_values_equal);
                    return result;
                } else { //smaller
                    saveAsUpperCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense
                    saveAsLowerCompareResult(new BigDecimal("-1")); //-1 => changing that value makes no sense

                    BigDecimal percentage = e1.divide(e2, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    result.add(0, df.format(percentage) + " %");
                    BigDecimal timesBigger = e2.divide(e1, 2, BigDecimal.ROUND_HALF_UP);
                    result.add(1, df.format(timesBigger));
                    result.add(2, com_percentage);
                    result.add(3, com_times_bigger);
                    return result;
                }
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    //Compare with a certain amount given for the lower item
    private static List<String> compareWithFixedUnitLower(Carrier c1, Carrier c2, long amount) {
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
        } else if (seconds < 60 * 60 * 24 * 365){ //smaller than one year - display days
            result[0] = df.format((double)seconds / (60 * 60 * 24));
            result[1] = com_days;
        } else { //display years
            result[0] = df.format((double)seconds / (60 * 60 * 24 * 365));
            result[1] = com_years;
        }

        return result;
    }

    private static int randomRange(int max, int min) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    private static void saveAsUpperCompareResult(BigDecimal result) {
        SharedPreferences prefs_upper = mContext.getSharedPreferences(key_comp_upper, Context.MODE_PRIVATE);
        if (result != null) {
            prefs_upper.edit().putString(key_comp_upper, result.toString()).apply();
        }
    }

    private static void saveAsLowerCompareResult(BigDecimal result) {
        SharedPreferences prefs_lower = mContext.getSharedPreferences(key_comp_lower, Context.MODE_PRIVATE);
        if (result != null) {
            prefs_lower.edit().putString(key_comp_lower, result.toString()).apply();
        }
    }
}