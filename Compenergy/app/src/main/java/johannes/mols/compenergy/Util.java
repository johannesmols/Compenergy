/*
 * Copyright (c) Johannes Mols 2017.
 */

package johannes.mols.compenergy;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

class Util {

    static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }

    static Point getDisplaySize(Activity activity) {
        DisplayMetrics displayMetrics = getDisplayMetrics(activity);

        Point size = new Point();
        size.x = displayMetrics.widthPixels;
        size.y = displayMetrics.heightPixels;

        return size;
    }

    static int dpToPx(DisplayMetrics displayMetrics, int dp) {
        return dpToPx(displayMetrics, dp, true);
    }

    static int dpToPx(DisplayMetrics displayMetrics, int dp, boolean xdpi) {
        float dpi = xdpi ? displayMetrics.xdpi : displayMetrics.ydpi;
        return Math.round(dp * (dpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    static int pxToDp(DisplayMetrics displayMetrics, int px) {
        return pxToDp(displayMetrics, px, true);
    }

    static int pxToDp(DisplayMetrics displayMetrics, int px, boolean xdpi) {
        float dpi = xdpi ? displayMetrics.xdpi : displayMetrics.ydpi;
        return Math.round(px / (dpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    static void openPlayStore(Context context, String playStorePackage, int flags) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + playStorePackage));
            intent.setFlags(flags);
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + playStorePackage));
                intent.setFlags(flags);
                context.startActivity(intent);
            }
            catch (ActivityNotFoundException ee) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.dialog_no_internet_browser);
                builder.setPositiveButton(context.getString(R.string.dialog_ok), null);
                builder.show();
            }
        }
    }

    static void openInternetBrowser(Context context, String link, int flags) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            intent.setFlags(flags);
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.dialog_no_internet_browser);
            builder.setPositiveButton(context.getString(R.string.dialog_ok), null);
            builder.show();
        }
    }

    static void openEmailApplication(Context context, String link, int flags) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setFlags(flags);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {link});
            Intent mailer = Intent.createChooser(intent, null);
            context.startActivity(mailer);
        }
        catch (ActivityNotFoundException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.dialog_no_email_app);
            builder.setPositiveButton(context.getString(R.string.dialog_ok), null);
            builder.show();
        }
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, " Kilo");
        suffixes.put(1_000_000L, " Mega");
        suffixes.put(1_000_000_000L, " Giga");
        suffixes.put(1_000_000_000_000L, " Tera");
        suffixes.put(1_000_000_000_000_000L, " Peta");
        suffixes.put(1_000_000_000_000_000_000L, " Exa");
    }

    static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    /* --- Input Filter to disallow possible SQL errors --- */

    static InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            for (int i = start; i < end; i++) {
                if (source.charAt(i) == '\'' || source.charAt(i) == '"') {
                    return "";
                }
            }
            return null;
        }
    };

    static String[] findBestTimeUnitFormatted(Context context, BigDecimal seconds) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(true);
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
        df.setDecimalFormatSymbols(symbols);

        String com_seconds = context.getString(R.string.com_seconds);
        String com_minutes = context.getString(R.string.com_minutes);
        String com_hours = context.getString(R.string.com_hours);
        String com_days = context.getString(R.string.com_days);
        String com_years = context.getString(R.string.com_years);

        String[] result = new String[2];
        if (seconds.compareTo(new BigDecimal(60)) == -1) { //smaller than one minute - display seconds
            result[0] = df.format(seconds);
            result[1] = com_seconds;
        } else if (seconds.compareTo(new BigDecimal(60 * 60)) == -1) { //smaller than one hour - display minutes
            result[0] = df.format(seconds.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP));
            result[1] = com_minutes;
        } else if (seconds.compareTo(new BigDecimal(60 * 60 * 24)) == -1) { //smaller than one day - display hours
            result[0] = df.format(seconds.divide(new BigDecimal(60 * 60), 2, BigDecimal.ROUND_HALF_UP));
            result[1] = com_hours;
        } else if (seconds.compareTo(new BigDecimal(60 * 60 * 24 * 365)) == -1){ //smaller than one year - display days
            result[0] = df.format(seconds.divide(new BigDecimal(60 * 60 * 24), 2, BigDecimal.ROUND_HALF_UP));
            result[1] = com_days;
        } else { //display years
            result[0] = df.format(seconds.divide(new BigDecimal(60 * 60 * 24 * 365), 2, BigDecimal.ROUND_HALF_UP));
            result[1] = com_years;
        }

        return result;
    }

    static String[] findBestTimeUnitUnformatted(Context context, BigDecimal seconds) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH); //Use dots in all language settings as decimal separator
        DecimalFormat df = (DecimalFormat) numberFormat;
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(2);

        String com_seconds = context.getString(R.string.com_seconds);
        String com_minutes = context.getString(R.string.com_minutes);
        String com_hours = context.getString(R.string.com_hours);
        String com_days = context.getString(R.string.com_days);
        String com_years = context.getString(R.string.com_years);

        String[] result = new String[2];
        if (seconds.compareTo(new BigDecimal(60)) == -1) { //smaller than one minute - display seconds
            result[0] = df.format(seconds);
            result[1] = com_seconds;
        } else if (seconds.compareTo(new BigDecimal(60 * 60)) == -1) { //smaller than one hour - display minutes
            result[0] = df.format(seconds.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP));
            result[1] = com_minutes;
        } else if (seconds.compareTo(new BigDecimal(60 * 60 * 24)) == -1) { //smaller than one day - display hours
            result[0] = df.format(seconds.divide(new BigDecimal(60 * 60), 2, BigDecimal.ROUND_HALF_UP));
            result[1] = com_hours;
        } else if (seconds.compareTo(new BigDecimal(60 * 60 * 24 * 365)) == -1){ //smaller than one year - display days
            result[0] = df.format(seconds.divide(new BigDecimal(60 * 60 * 24), 2, BigDecimal.ROUND_HALF_UP));
            result[1] = com_days;
        } else { //display years
            result[0] = df.format(seconds.divide(new BigDecimal(60 * 60 * 24 * 365), 2, BigDecimal.ROUND_HALF_UP));
            result[1] = com_years;
        }

        return result;
    }

    static BigDecimal convertTimeWithUnitToSeconds(BigDecimal amount, int unit) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH); //Use dots in all language settings as decimal separator
        DecimalFormat df = (DecimalFormat) numberFormat;
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(2);

        switch (unit) {
            case 0:
                //seconds
                return amount;
            case 1:
                //minutes
                return amount.multiply(new BigDecimal(60));
            case 2:
                //hours
                return amount.multiply(new BigDecimal(60 * 60));
            case 3:
                //days
                return amount.multiply(new BigDecimal(60 * 60 * 24));
            case 4:
                //years
                return amount.multiply(new BigDecimal(60 * 60 * 24 * 365));
            default:
                //seconds
                return amount;
        }
    }
}
