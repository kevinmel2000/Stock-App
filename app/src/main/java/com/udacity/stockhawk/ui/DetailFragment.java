package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.stockhawk.data.Contract.Quote;

/**
 * Created by tonynguyen on 2/22/17.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String SELECTED_URI = "selected_uri";

    private static final int DETAIL_LOADER = 0;

    @BindView(R.id.detail_symbol_name)TextView vSymbolName;
    @BindView(R.id.detail_symbol)
    TextView vSymbol;
    @BindView(R.id.detail_price)
    TextView vPrice;
    @BindView(R.id.detail_price_change)
    TextView vPriceChange;
    @BindView(R.id.detail_percent_change)
    TextView vPercentChange;
    @BindView(R.id.detail_highest)TextView vDayHigh;
    @BindView(R.id.detail_lowest)TextView vDayLow;
    @BindView(R.id.detail_previous_close)TextView vPreviousClose;
    @BindView(R.id.detail_open)TextView vOpen;
    @BindView(R.id.detail_bid)TextView vBid;
    @BindView(R.id.detail_ask)TextView vAsk;
    @BindView(R.id.detail_volume)TextView vVolume;
    @BindView(R.id.detail_avg_volume)TextView vAvgVolume;
    @BindView(R.id.line_graph)LineChart vLineChart;

    private Uri mSelectedUri;

    public DetailFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail,container,false);
        ButterKnife.bind(this,view);

        Bundle arguments = getArguments();
        if(arguments != null) {
            mSelectedUri = arguments.getParcelable(SELECTED_URI);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(DETAIL_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(null != mSelectedUri){

            return new CursorLoader(
                    getActivity(),
                    mSelectedUri,
                    Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data != null && data.moveToFirst()) {

            // DecimalFormats
            DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

            DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus.setPositivePrefix("+$");

            DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
            percentageFormat.setMaximumFractionDigits(2);
            percentageFormat.setMinimumFractionDigits(2);
            percentageFormat.setPositivePrefix("+");

            // Retrieving necessary data from the database
            float priceChange = data.getFloat(Quote.POSITION_ABSOLUTE_CHANGE);
            float percentageChange = data.getFloat(Quote.POSITION_PERCENTAGE_CHANGE);

            String symbolName = data.getString(Quote.POSITION_SYMBOL_NAME);
            String symbol = data.getString(Quote.POSITION_SYMBOL);
            String price = dollarFormat.format(data.getFloat(Quote.POSITION_PRICE));
            String priceChangeStr = dollarFormatWithPlus.format(priceChange);
            String percentageChangeStr = percentageFormat.format(percentageChange / 100);
            String dayHighestPrice = dollarFormat.format(data.getFloat(Quote.POSITION_PRICE_HIGHEST));
            String dayLowestPrice = dollarFormat.format(data.getFloat(Quote.POSITION_PRICE_LOWEST));
            String previousClose = dollarFormat.format(data.getFloat(Quote.POSITION_PREVIOUS_CLOSE));
            String open = dollarFormat.format(data.getFloat(Quote.POSITION_OPEN));
            String bid = dollarFormat.format(data.getFloat(Quote.POSITION_BID));
            String ask = dollarFormat.format(data.getFloat(Quote.POSITION_ASK));
            String volume = data.getString(Quote.POSITION_VOLUME);
            String avgVolume = data.getString(Quote.POSITION_AVG_VOLUME);
            String history = data.getString(Quote.POSITION_HISTORY);

            // Initiate the LineGraph with history data strings
            getLineGraph(history, vLineChart);
            vLineChart.setContentDescription(getString(R.string.line_graph_description));

            if(priceChange >= 0){
                vPriceChange.setTextColor(ContextCompat.getColor(getContext(),R.color.material_green_700));
                vPercentChange.setTextColor(ContextCompat.getColor(getContext(),R.color.material_green_700));
            } else {
                vPriceChange.setTextColor(ContextCompat.getColor(getContext(),R.color.material_red_700));
                vPercentChange.setTextColor(ContextCompat.getColor(getContext(),R.color.material_red_700));
            }

            vSymbol.setText(symbol);
            vSymbolName.setText(symbolName);
            vPrice.setText(price);
            vPrice.setContentDescription(Utility.formatPriceDescription(getContext(),price));
            vPriceChange.setText(priceChangeStr);
            vPriceChange.setContentDescription(Utility.formatPriceChangeDescription(getContext(),priceChangeStr));
            vPercentChange.setText(percentageChangeStr);
            vPercentChange.setContentDescription(Utility.formatPercentChangeDescription(getContext(),percentageChangeStr));
            vDayHigh.setText(dayHighestPrice);
            vDayLow.setText(dayLowestPrice);
            vPreviousClose.setText(previousClose);
            vOpen.setText(open);
            vBid.setText(bid);
            vAsk.setText(ask);
            vVolume.setText(volume);
            vAvgVolume.setText(avgVolume);
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);

        // We need to start the enter transition after the data has loaded
        if (activity instanceof DetailActivity) {
            activity.supportStartPostponedEnterTransition();

            if (null != toolbarView) {
                activity.setSupportActionBar(toolbarView);

                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private void getLineGraph(String history, LineChart lineChart){

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        String[] historyStr = history.split("\\r\\n|\\n|\\r");

        for(int i = 0; i < historyStr.length; i++){
            entries.add(new Entry(Utility.getYFloatValue(historyStr[i]), i ));
            labels.add(Utility.getDateFromMilliseconds(historyStr[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries,getString(R.string.line_graph_label_description));
        dataSet.setColor(R.color.colorPrimaryDark);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(R.color.colorPrimaryDark);
        dataSet.setValueTextColor(R.color.colorPrimaryDark);

        LineData lineData = new LineData(labels,dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        Legend legend = lineChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setPinchZoom(true);
        lineChart.setDescription(getString(R.string.line_graph_inner_description));
        lineChart.setDrawBorders(true);
        lineChart.setKeepPositionOnRotation(true);


    }
}
