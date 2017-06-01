/*
 * Copyright (c) Johannes Mols 2017.
 *
 * Constant sources:
 * [1] : http://www.sfei.org/it/gis/map-interpretation/conversion-constants#top
 *
 */

package johannes.mols.compenergy;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class UnitConverter {

    //Constant sources: https://en.wikipedia.org/wiki/Conversion_of_units

    private static final int toKilo = 1000;
    private static final int toMega = 1000000;

    //Energy types to Joule
    private static final BigDecimal gramCalorieToJoule = new BigDecimal(4.1868);
    private static final BigDecimal kiloCalorieToJoule = new BigDecimal(4186.8);
    private static final BigDecimal wattHourToJoule = new BigDecimal(3600);
    private static final BigDecimal kiloWattHourToJoule = new BigDecimal(3600000);
    private static final BigDecimal electronVoltToJoule = new BigDecimal(0.00000000000000000016021766);
    private static final BigDecimal britishThermalUnitToJoule = new BigDecimal(1054.5);
    private static final BigDecimal usThermToJoule = new BigDecimal(105480400);
    private static final BigDecimal footPoundToJoule = new BigDecimal(1.3558179483314004);

    //Distance to Kilometer
    private static final BigDecimal nanoMetreToKilometer    = new BigDecimal(0.000000000001);
    private static final BigDecimal microMetreToKilometer   = new BigDecimal(0.000000001);
    private static final BigDecimal milliMetreToKilometer   = new BigDecimal(0.000001);
    private static final BigDecimal centimetreToKilometer   = new BigDecimal(0.0001);
    private static final BigDecimal metreToKilometer        = new BigDecimal(0.001);
    private static final BigDecimal mileToKilometer         = new BigDecimal(1.609344);
    private static final BigDecimal yardToKilometer         = new BigDecimal(0.0009144);
    private static final BigDecimal footToKilometer         = new BigDecimal(0.0003048);
    private static final BigDecimal inchToKilometer         = new BigDecimal(0.0000254);
    private static final BigDecimal nauticalMileToKilometer = new BigDecimal(1.852);

    /* --- Joule/Watt conversion --- */

    public static BigDecimal wattToJoule(BigDecimal watt, BigDecimal seconds) {
        BigDecimal result = new BigDecimal(String.valueOf(watt));
        result = result.multiply(new BigDecimal(String.valueOf(seconds)));
        return result;
    }

    public static BigDecimal jouleToWatt(BigDecimal joule, BigDecimal seconds) {
        //signum() returns -1,0,1 depending on if the BigDecimal is negative, zero or positive
        if(seconds.signum() == 1) {
            BigDecimal result = new BigDecimal(String.valueOf(joule));
            result = result.divide(new BigDecimal(String.valueOf(seconds)), BigDecimal.ROUND_HALF_UP);
            return result;
        }
        return new BigDecimal(0);
    }

    /* --- Energy I/O --- */

    //input_type = index of item in energy units list (0 = Joule, 1 = Kilojoule, ...)
    public static BigInteger energyInputToJoule(int input_type, BigDecimal input) {
        BigInteger result;
        switch (input_type) {
            case 0: //Joule
                result = input.toBigInteger();
                break;
            case 1: //Kilojoule
                result = input.toBigInteger().multiply(BigInteger.valueOf(toKilo));
                break;
            case 2: //Megajoule
                result = input.toBigInteger().multiply(BigInteger.valueOf(toMega));
                break;
            case 3: //Gram calorie
                result = input.multiply(gramCalorieToJoule).toBigInteger();
                break;
            case 4: //Kilocalorie
                result = input.multiply(kiloCalorieToJoule).toBigInteger();
                break;
            case 5: //Watt hour
                result = input.multiply(wattHourToJoule).toBigInteger();
                break;
            case 6: //Kilowatt hour
                result = input.multiply(kiloWattHourToJoule).toBigInteger();
                break;
            case 7: //Electronvolt
                result = input.multiply(electronVoltToJoule).toBigInteger();
                break;
            case 8: //British thermal unit
                result = input.multiply(britishThermalUnitToJoule).toBigInteger();
                break;
            case 9: //US therm
                result = input.multiply(usThermToJoule).toBigInteger();
                break;
            case 10: //Foot-pound
                result = input.multiply(footPoundToJoule).toBigInteger();
                break;
            default:
                result = BigInteger.ZERO;
                break;
        }

        return result;
    }

    /* --- Distance I/O --- */

    public static BigDecimal distanceInputToKilometre(int input_type, BigDecimal input) {
        BigDecimal result;
        switch (input_type) {
            case 0: //Nanometre
                result = input.multiply(nanoMetreToKilometer);
                break;
            case 1: //Micrometre
                result = input.multiply(microMetreToKilometer);
                break;
            case 2: //Millimetre
                result = input.multiply(milliMetreToKilometer);
                break;
            case 3: //Centimetre
                result = input.multiply(centimetreToKilometer);
                break;
            case 4: //Metre
                result = input.multiply(metreToKilometer);
                break;
            case 5: //Kilometre
                result = input;
                break;
            case 6: //Mile
                result = input.multiply(mileToKilometer);
                break;
            case 7: //Yard
                result = input.multiply(yardToKilometer);
                break;
            case 8: //Foot
                result = input.multiply(footToKilometer);
                break;
            case 9: //Inch
                result = input.multiply(inchToKilometer);
                break;
            case 10: //Nautical mile
                result = input.multiply(nauticalMileToKilometer);
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }

        return result;
    }
}