package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.Receiver;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tony Nguyen on 2/16/2017.
 */

public class Utility {

    public static boolean networkUp(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static Float getXFloatValue(String s){

        String[] split = s.split(",");
        String x = split[0];

        return Float.valueOf(x);
    }

    public static String getDateFromMilliseconds(String s){

        String[] split = s.split(",");
        String x = split[0];

        long milliseconds = Long.valueOf(x);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        return formatter.format(new Date(milliseconds));
    }

    public static Float getYFloatValue(String s){

        String[] split = s.split(",");
        String x = split[1];

        return Float.valueOf(x);
    }

    public static String formatPriceDescription(Context context, String price){

        return context.getString(R.string.current_price_is) + price;
    }

    public static String formatLowestPriceDescription(Context context, String lowestPrice){

        return context.getString(R.string.todays_low_price_is) + lowestPrice;
    }

    public static String formatHighestPriceDescription(Context context, String highestPrice){

        return context.getString(R.string.today_high_price_is) + highestPrice;
    }

    public static String formatPriceChangeDescription(Context context, String priceChange){
        return context.getString(R.string.price_change_is) + priceChange;
    }

    public static String formatPercentChangeDescription(Context context, String percentChange){
        return context.getString(R.string.percent_change_is) + percentChange;
    }

    public static void makeSnackBar(CoordinatorLayout coordinatorLayout, String response){

        Snackbar snackbar = Snackbar.make(coordinatorLayout, response, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void makeIndefiniteSnackBar(CoordinatorLayout coordinatorLayout, String response){

        Snackbar snackbar = Snackbar.make(coordinatorLayout, response, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    @SuppressWarnings("ResourceType")
    static public @Receiver.StockHawkStatus
    int getStockHawkStatus(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.stock_hawk_status_key),
                Receiver.STATUS_UNKNOWN);
    }

    public static String getInvalidSymbol(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.invalid_symbol_key), "UNKNOWN");
    }

}
