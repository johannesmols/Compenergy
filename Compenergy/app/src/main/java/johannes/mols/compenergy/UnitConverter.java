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

final class UnitConverter {

    //Constant sources: https://en.wikipedia.org/wiki/Conversion_of_units

    private static final int toKilo = 1000;
    private static final int toMega = 1000000;

    //Energy types to Joule
    private static final BigDecimal gramCalorieToJoule          = new BigDecimal(4.1868);
    private static final BigDecimal kiloCalorieToJoule          = new BigDecimal(4186.8);
    private static final BigDecimal wattHourToJoule             = new BigDecimal(3600);
    private static final BigDecimal kiloWattHourToJoule         = new BigDecimal(3600000);
    private static final BigDecimal electronVoltToJoule         = new BigDecimal(0.00000000000000000016021766);
    private static final BigDecimal britishThermalUnitToJoule   = new BigDecimal(1054.5);
    private static final BigDecimal usThermToJoule              = new BigDecimal(105480400);
    private static final BigDecimal footPoundToJoule            = new BigDecimal(1.3558179483314004);

    //Distance to Kilometer
    private static final BigDecimal nanoMetreToKilometer        = new BigDecimal(0.000000000001);
    private static final BigDecimal microMetreToKilometer       = new BigDecimal(0.000000001);
    private static final BigDecimal milliMetreToKilometer       = new BigDecimal(0.000001);
    private static final BigDecimal centimetreToKilometer       = new BigDecimal(0.0001);
    private static final BigDecimal metreToKilometer            = new BigDecimal(0.001);
    private static final BigDecimal mileToKilometer             = new BigDecimal(1.609344);
    private static final BigDecimal yardToKilometer             = new BigDecimal(0.0009144);
    private static final BigDecimal footToKilometer             = new BigDecimal(0.0003048);
    private static final BigDecimal inchToKilometer             = new BigDecimal(0.0000254);
    private static final BigDecimal nauticalMileToKilometer     = new BigDecimal(1.852);

    //Mass to Kilogram
    private static final BigDecimal ounceToKilogram             = new BigDecimal(0.028349523125);
    private static final BigDecimal poundToKilogram             = new BigDecimal(0.45359237);
    private static final BigDecimal stoneToKilogram             = new BigDecimal(6.35029318);
    private static final BigDecimal usTonToKilogram             = new BigDecimal(907.18474);
    private static final BigDecimal imperialTonToKilogram       = new BigDecimal(1016.0469088);
    private static final BigDecimal microGramToKilogram         = new BigDecimal(0.000000001);
    private static final BigDecimal milliGramToKilogram         = new BigDecimal(0.000001);
    private static final BigDecimal gramToKilogram              = new BigDecimal(0.001);
    private static final BigDecimal tonToKilogram               = new BigDecimal(1000);

    //Volume to Litre
    private static final BigDecimal cubicInchToLiter            = new BigDecimal(0.0163871);
    private static final BigDecimal cubicFootToLiter            = new BigDecimal(28.3168);
    private static final BigDecimal cubicMetreToLiter           = new BigDecimal(1000);
    private static final BigDecimal imperialTeaspoonToLiter     = new BigDecimal(0.00591939);
    private static final BigDecimal imperialTablespoonToLiter   = new BigDecimal(0.0177582);
    private static final BigDecimal imperialFluidOunceToLiter   = new BigDecimal(0.0284131);
    private static final BigDecimal imperialCupToLiter          = new BigDecimal(0.284131);
    private static final BigDecimal imperialPintToLiter         = new BigDecimal(0.568261);
    private static final BigDecimal imperialQuartToLiter        = new BigDecimal(1.13652);
    private static final BigDecimal imperialGallonToLiter       = new BigDecimal(4.54609);
    private static final BigDecimal millilitreToLiter           = new BigDecimal(0.001);
    private static final BigDecimal usTeaspoonToLiter           = new BigDecimal(0.00492892);
    private static final BigDecimal usTablespoonToLiter         = new BigDecimal(0.0147868);
    private static final BigDecimal usFluidOunceToLiter         = new BigDecimal(0.0295735);
    private static final BigDecimal usLegalCupToLiter           = new BigDecimal(0.24);
    private static final BigDecimal usLiquidPintToLiter         = new BigDecimal(0.473176);
    private static final BigDecimal usLiquidQuartToLiter        = new BigDecimal(0.946353);
    private static final BigDecimal usLiquidGallonToLiter       = new BigDecimal(3.785412);

    //Fuel types energy density (Joules/Litre)
    private static final BigDecimal gasolineDensity             = new BigDecimal(34200000);
    private static final BigDecimal dieselDensity               = new BigDecimal(35800000);
    private static final BigDecimal keroseneDensity             = new BigDecimal(37400000);
    private static final BigDecimal lpgDensity                  = new BigDecimal(26000000);
    private static final BigDecimal ethanolDensity              = new BigDecimal(20900000);
    private static final BigDecimal hydrogenDensity             = new BigDecimal(5600000);
    private static final BigDecimal methanolDensity             = new BigDecimal(15600000);


    /* --- Joule/Watt conversion --- */

    static BigDecimal wattToJoule(BigDecimal watt, BigDecimal seconds) {
        BigDecimal result = new BigDecimal(String.valueOf(watt));
        result = result.multiply(new BigDecimal(String.valueOf(seconds)));
        return result;
    }

    static BigDecimal jouleToWatt(BigDecimal joule, BigDecimal seconds) {
        //signum() returns -1,0,1 depending on if the BigDecimal is negative, zero or positive
        if(seconds.signum() == 1) {
            BigDecimal result = new BigDecimal(String.valueOf(joule));
            result = result.divide(new BigDecimal(String.valueOf(seconds)), BigDecimal.ROUND_HALF_UP);
            return result;
        }
        return new BigDecimal(0);
    }

    //input_type = index of item in energy units list (0 = Joule, 1 = Kilojoule, ...)
    static BigInteger energyInputToJoule(int input_type, BigDecimal input) {
        BigInteger result;
        switch (input_type) {
            case 0: //Joule
                result = input.toBigInteger();
                break;
            case 1: //Kilojoule
                result = input.multiply(BigDecimal.valueOf(toKilo)).toBigInteger();
                break;
            case 2: //Megajoule
                result = input.multiply(BigDecimal.valueOf(toMega)).toBigInteger();
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

    static BigDecimal wattInputToWatt(int input_type, BigDecimal input) {
        BigDecimal result;
        switch (input_type) {
            case 0: //Watt
                result = input;
                break;
            case 1: //Kilowatt
                result = input.multiply(new BigDecimal(toKilo));
                break;
            case 2: //Megawatt
                result = input.multiply(new BigDecimal(toMega));
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }

        return result;
    }

    static BigDecimal distanceInputToKilometre(int input_type, BigDecimal input) {
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

    static BigDecimal massInputToKilogram(int input_type, BigDecimal input) {
        BigDecimal result;
        switch (input_type) {
            case 0: //Ounce
                result = input.multiply(ounceToKilogram);
                break;
            case 1: //Pound
                result = input.multiply(poundToKilogram);
                break;
            case 2: //Stone
                result = input.multiply(stoneToKilogram);
                break;
            case 3: //US ton
                result = input.multiply(usTonToKilogram);
                break;
            case 4: //Imperial ton
                result = input.multiply(imperialTonToKilogram);
                break;
            case 5: //Microgram
                result = input.multiply(microGramToKilogram);
                break;
            case 6: //Milligram
                result = input.multiply(milliGramToKilogram);
                break;
            case 7: //Gram
                result = input.multiply(gramToKilogram);
                break;
            case 8: //Kilogram
                result = input;
                break;
            case 9: //Ton
                result = input.multiply(tonToKilogram);
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }

        return result;
    }

    static BigDecimal volumeInputToLitre(int input_type, BigDecimal input) {
        BigDecimal result;
        switch (input_type) {
            case 0: //Cubic inch
                result = input.multiply(cubicInchToLiter);
                break;
            case 1: //Cubic foot
                result = input.multiply(cubicFootToLiter);
                break;
            case 2: //Cubic metre
                result = input.multiply(cubicMetreToLiter);
                break;
            case 3: //Imperial teaspoon
                result = input.multiply(imperialTeaspoonToLiter);
                break;
            case 4: //Imperial tablespoon
                result = input.multiply(imperialTablespoonToLiter);
                break;
            case 5: //Imperial fluid ounce
                result = input.multiply(imperialFluidOunceToLiter);
                break;
            case 6: //Imperial cup
                result = input.multiply(imperialCupToLiter);
                break;
            case 7: //Imperial pint
                result = input.multiply(imperialPintToLiter);
                break;
            case 8: //Imperial quart
                result = input.multiply(imperialQuartToLiter);
                break;
            case 9: //Imperial gallon
                result = input.multiply(imperialGallonToLiter);
                break;
            case 10: //Millilitre
                result = input.multiply(millilitreToLiter);
                break;
            case 11: //Litre
                result = input;
                break;
            case 12: //US teaspoon
                result = input.multiply(usTeaspoonToLiter);
                break;
            case 13: //US tablespoon
                result = input.multiply(usTablespoonToLiter);
                break;
            case 14: //US fluid ounce
                result = input.multiply(usFluidOunceToLiter);
                break;
            case 15: //US legal cup
                result = input.multiply(usLegalCupToLiter);
                break;
            case 16: //US liquid pint
                result = input.multiply(usLiquidPintToLiter);
                break;
            case 17: //US liquid quart
                result = input.multiply(usLiquidQuartToLiter);
                break;
            case 18: //US liquid gallon
                result = input.multiply(usLiquidGallonToLiter);
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }

        return result;
    }

    static BigDecimal vehicleConsumptionToJoule(int input_type, BigDecimal input) {
        BigDecimal result;
        switch (input_type) {
            case 0: //Gasoline
                result = input.multiply(gasolineDensity);
                break;
            case 1: //Diesel
                result = input.multiply(dieselDensity);
                break;
            case 2: //Kerosene
                result = input.multiply(keroseneDensity);
                break;
            case 3: //LPG
                result = input.multiply(lpgDensity);
                break;
            case 4: //Ethanol
                result = input.multiply(ethanolDensity);
                break;
            case 5: //Hydrogen
                result = input.multiply(hydrogenDensity);
                break;
            case 6: //Methanol
                result = input.multiply(methanolDensity);
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }

        return result;
    }
}