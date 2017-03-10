package com.udacity.stockhawk.ui;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract.Quote;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private final Context context;
    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;
    private Cursor cursor;
    private final StockAdapterOnClickHandler clickHandler;

    StockAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.symbol) TextView symbol;
        @BindView(R.id.symbol_name) TextView symbolName;
        @BindView(R.id.price) TextView price;
        @BindView(R.id.lowest_price)TextView lowestPrice;
        @BindView(R.id.highest_price)TextView highestPrice;
        @BindView(R.id.change) TextView change;
        @BindView(R.id.percent_change) TextView percentChange;


        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);

            int symbolColumn = cursor.getColumnIndex(Quote.COLUMN_SYMBOL);
            clickHandler.onClick(cursor.getString(symbolColumn), adapterPosition);
        }
    }

    interface StockAdapterOnClickHandler {
        void onClick(String symbol, int position);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        cursor.moveToPosition(position);

        String currentPrice = cursor.getString(Quote.POSITION_PRICE);
        String lowestPrice = cursor.getString(Quote.POSITION_PRICE_LOWEST);
        String highestPrice = cursor.getString(Quote.POSITION_PRICE_HIGHEST);

        holder.symbol.setText(cursor.getString(Quote.POSITION_SYMBOL));
        holder.symbolName.setText(cursor.getString(Quote.POSITION_SYMBOL_NAME));
        holder.price.setText(dollarFormat.format(cursor.getFloat(Quote.POSITION_PRICE)));
        holder.price.setContentDescription(Utility.formatPriceDescription(context,currentPrice));
        holder.lowestPrice.setText(dollarFormat.format(cursor.getFloat(Quote.POSITION_PRICE_LOWEST)));
        holder.lowestPrice.setContentDescription(Utility.formatLowestPriceDescription(context,lowestPrice) );
        holder.highestPrice.setText(dollarFormat.format(cursor.getFloat(Quote.POSITION_PRICE_HIGHEST)));
        holder.highestPrice.setContentDescription(Utility.formatHighestPriceDescription(context,highestPrice) );

        float rawAbsoluteChange = cursor.getFloat(Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = cursor.getFloat(Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            holder.change.setTextColor(ContextCompat.getColor(context,R.color.material_green_700));
            holder.percentChange.setTextColor(ContextCompat.getColor(context,R.color.material_green_700));
        } else {
            holder.change.setTextColor(ContextCompat.getColor(context,R.color.material_red_700));
            holder.percentChange.setTextColor(ContextCompat.getColor(context,R.color.material_red_700));
        }

        String change = dollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);

        holder.change.setText(change);
        holder.change.setContentDescription(Utility.formatPriceChangeDescription(context,change));
        holder.percentChange.setText(percentage);
        holder.percentChange.setContentDescription(Utility.formatPercentChangeDescription(context,percentage));


    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }

    String getSymbolAtPosition(int position) {
        cursor.moveToPosition(position);
        return cursor.getString(Quote.POSITION_SYMBOL);
    }
}
