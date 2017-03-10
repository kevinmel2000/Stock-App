package com.udacity.stockhawk.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.udacity.stockhawk.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tonynguyen on 3/5/17.
 */

public class Receiver extends BroadcastReceiver {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_OK, STATUS_INVALID_STOCK, STATUS_UNKNOWN, STATUS_ADDED, STATUS_NO_CONNECTION})
    public @interface StockHawkStatus{}
    public static final int STATUS_OK = 0;
    public static final int STATUS_INVALID_STOCK = 1;
    public static final int STATUS_UNKNOWN = 2;
    public static final int STATUS_ADDED = 3;
    public static final int STATUS_NO_CONNECTION = 4;

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()){
            case "com.udacity.stockhawk.ACTION_STOCK_NONEXIST":
                setStockHawkStatus(context,STATUS_INVALID_STOCK);
                break;
            case "com.udacity.stockhawk.ACTION_DATA_UPDATED":
                setStockHawkStatus(context, STATUS_OK);
                break;
            case "com.udacity.stockhawk.ACTION_SYMOL_ADDED":
                setStockHawkStatus(context, STATUS_ADDED);
                break;
            case "com.udacity.stockhawk.ACTION_NNCTA":
                setStockHawkStatus(context, STATUS_NO_CONNECTION);
                break;
        }
    }

    public static void setStockHawkStatus(Context c, @StockHawkStatus int shStatus){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.stock_hawk_status_key), shStatus);
        spe.apply();
    }
}
