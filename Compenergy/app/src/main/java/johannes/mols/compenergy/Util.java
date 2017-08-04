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
}
