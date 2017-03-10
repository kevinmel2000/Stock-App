package com.udacity.stockhawk.data;


import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;

public final class Contract {

    static final String AUTHORITY = "com.udacity.stockhawk";
    static final String PATH_QUOTE = "quote";
    static final String PATH_QUOTE_WITH_SYMBOL = "quote/*";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    @SuppressWarnings("unused")
    public static final class Quote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_SYMBOL_NAME = "symbol_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_LOWEST_PRICE = "lowest_price";
        public static final String COLUMN_HIGHEST_PRICE = "highest_price";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final String COLUMN_HISTORY = "history";
        public static final String COLUMN_PREVIOUS_CLOSE = "previous_close";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_BID = "bid";
        public static final String COLUMN_ASK = "ask";
        public static final String COLUMN_VOLUME = "volume";
        public static final String COLUMN_AVG_VOLUME = "avg_volume";
        public static final int POSITION_ID = 0;
        public static final int POSITION_SYMBOL = 1;
        public static final int POSITION_SYMBOL_NAME = 2;
        public static final int POSITION_PRICE = 3;
        public static final int POSITION_PRICE_LOWEST = 4;
        public static final int POSITION_PRICE_HIGHEST = 5;
        public static final int POSITION_ABSOLUTE_CHANGE = 6;
        public static final int POSITION_PERCENTAGE_CHANGE = 7;
        public static final int POSITION_HISTORY = 8;
        public static final int POSITION_PREVIOUS_CLOSE = 9;
        public static final int POSITION_OPEN = 10;
        public static final int POSITION_BID = 11;
        public static final int POSITION_ASK = 12;
        public static final int POSITION_VOLUME = 13;
        public static final int POSITION_AVG_VOLUME = 14;


        public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SYMBOL,
                COLUMN_SYMBOL_NAME,
                COLUMN_PRICE,
                COLUMN_LOWEST_PRICE,
                COLUMN_HIGHEST_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE,
                COLUMN_HISTORY,
                COLUMN_PREVIOUS_CLOSE,
                COLUMN_OPEN,
                COLUMN_BID,
                COLUMN_ASK,
                COLUMN_VOLUME,
                COLUMN_AVG_VOLUME
        );
        static final String TABLE_NAME = "quotes";

        public static Uri makeUriForStock(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri makeUriForSymbol(String symbol){
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getStockFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

}
