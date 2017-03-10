package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by tonyn on 3/9/2017.
 */

public class WidgetIntentService extends IntentService {

    private static final String[] STOCK_COLUMNS = {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };

    private static final int INDEX_STOCK_ID = 0;
    private static final int INDEX_STOCK_SYMBOL = 1;
    private static final int INDEX_STOCK_PRICE = 2;
    private static final int INDEX_STOCK_PRICE_CHANGE = 3;
    private static final int INDEC_STOCK_PERCENTAGE_CHANGE = 4;

    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;
    private DecimalFormat percentageFormat;

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WidgetProvider.class));

        // Get data from the ContentProvider
        Cursor data = getContentResolver().query(Contract.Quote.URI,
                STOCK_COLUMNS,
                null,
                null,
                null);

        if(data == null){
            return;
        }

        if(!data.moveToFirst()){
            data.close();
            return;
        }


        // Extract data from the Cursor
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        int stockResourceId = android.R.mipmap.sym_def_app_icon;
        int stockId = data.getInt(INDEX_STOCK_ID);
        String symbol = data.getString(INDEX_STOCK_SYMBOL);
        String price = dollarFormat.format(data.getFloat(INDEX_STOCK_PRICE));
        String priceChange = dollarFormatWithPlus.format(data.getFloat(INDEX_STOCK_PRICE_CHANGE));
        String percentageChange = percentageFormat.format(data.getFloat(INDEC_STOCK_PERCENTAGE_CHANGE));
        String description= "Current Price";
        String priceStr = "300000";
        data.close();

        for(int appWidgetId : appWidgetIds){
            int layoutId = R.layout.widget_small;
            RemoteViews views = new RemoteViews(getPackageName(),layoutId);

            // Add data to the RemoteViews
            views.setTextViewText(R.id.widget_symbol, symbol);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
                setRemoteContentDescription(views,description);
            }
            views.setTextViewText(R.id.widget_price, price);

            Log.d("SYMBOLA", symbol);

            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,launchIntent,0);
            views.setOnClickPendingIntent(R.id.widget,pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description){
        views.setContentDescription(R.id.widget_symbol,description);
    }
}
