/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import java.util.Comparator;

class CustomComparators {

    //Alphabetical sort of String List
    static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
        public int compare(String str1, String str2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
            if (res == 0) {
                res = str1.compareTo(str2);
            }
            return res;
        }
    };

    //Custom comparator by Carrier property name
    static class CarrierComparator implements Comparator<Carrier> {
        @Override
        public int compare(Carrier c1, Carrier c2) {
            return c1.get_name().compareTo(c2.get_name());
        }
    }
}
