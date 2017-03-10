package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.udacity.stockhawk.BuildConfig;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteJobService;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by tonynguyen on 3/6/17.
 */

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context,WidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions){
        context.startService(new Intent(context, WidgetIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent){
        super.onReceive(context,intent);
        if(QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())){
            context.startService(new Intent(context, WidgetIntentService.class));
        }
    }


}
