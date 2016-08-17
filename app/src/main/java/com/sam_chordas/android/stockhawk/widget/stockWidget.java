package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteDatabase;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Implementation of App Widget functionality.
 */
public class stockWidget extends AppWidgetProvider {

    private static String TAG = "stockWidget";
    public static ArrayList<String> currentStocks;

    // TODO: 8/9/2016 see if moving to update will fix widget update
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Access stock data

        Cursor tempCursor = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE, QuoteColumns.ISCURRENT}, QuoteColumns.ISCURRENT,
                null, null);

        // Populate widget
        tempCursor.moveToFirst();
        Log.d(TAG, String.valueOf(tempCursor.getCount()));
        Log.d(TAG, tempCursor.getColumnNames().toString());
        StringBuilder sb = new StringBuilder();
        // date section


        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-7:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("K:mm:ss a");
// you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));

        String localTime = date.format(currentLocalTime);

        sb.append(localTime+"\n");
        //
        for (int i = 0; i < tempCursor.getCount(); i++) {
            if (tempCursor.getString(tempCursor.getColumnIndex("is_current")).equalsIgnoreCase("0"))
                ;
            {

                sb.append(tempCursor.getString(tempCursor.getColumnIndex("symbol")));
                sb.append(" ");
                sb.append("$" + tempCursor.getString(tempCursor.getColumnIndex("bid_price")));
                currentStocks.add(sb.toString());
                tempCursor.moveToNext();
                if (i < tempCursor.getCount() - 1) {
                    sb.append("\n");
                }
            }
        }
        Log.d(TAG, sb.toString());
        tempCursor.close();

//        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget);
        views.setTextViewText(R.id.appwidget_text, sb.toString());


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        views.getLayoutId();
// TODO: 8/16/2016 look into making a button on widget open app instead of entire widget face, that way next and previous may work
        // Set up on tap widget
        Intent mIntent = new Intent(context, MyStocksActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public void widgetPreviousButton (View v){

    }

}

