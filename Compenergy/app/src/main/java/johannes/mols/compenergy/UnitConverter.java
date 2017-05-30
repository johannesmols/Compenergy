/*
 * Copyright (c) Johannes Mols 2017.
 *
 * Constant sources:
 * [1] : http://www.sfei.org/it/gis/map-interpretation/conversion-constants#top
 *
 */

package johannes.mols.compenergy;

import java.math.BigInteger;

public final class UnitConverter {

    /* --- Energy conversion --- */

    public static BigInteger wattToJoule(long watt, long seconds) {
        BigInteger result = new BigInteger(String.valueOf(watt));
        result = result.multiply(new BigInteger(String.valueOf(seconds)));
        return result;
    }
}
