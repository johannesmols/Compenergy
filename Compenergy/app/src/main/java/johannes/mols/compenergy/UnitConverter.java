/*
 * Copyright (c) Johannes Mols 2017.
 *
 * Constant sources:
 * [1] : http://www.sfei.org/it/gis/map-interpretation/conversion-constants#top
 *
 */

package johannes.mols.compenergy;

import java.math.BigDecimal;

public final class UnitConverter {

    /* --- Energy conversion --- */

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
}