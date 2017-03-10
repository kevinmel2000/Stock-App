package com.udacity.stockhawk.ui;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.sync.Receiver;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tony Nguyen on 2/16/2017.
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
        StockAdapter.StockAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int STOCK_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_view)
    RecyclerView stockRecyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.error)
    TextView error;
    @BindView(R.id.main_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.main_coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private StockAdapter adapter;
    private Uri mSelectedUri;
    private int mPosition = RecyclerView.NO_POSITION;

    public interface CallBack{
         void onItemSelected(Uri selectedUri);
    }

    @Override
    public void onClick(String symbol, int selectedPosition) {
        mSelectedUri = Contract.Quote.makeUriForStock(symbol);
        ((CallBack)getActivity()).onItemSelected(mSelectedUri);
        mPosition = selectedPosition;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, v);

        adapter = new StockAdapter(getContext(), this);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stockRecyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String symbol = adapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
                PrefUtils.removeStock(getContext(), symbol);
                getActivity().getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);
            }
        }).attachToRecyclerView(stockRecyclerView);

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_units) {
            new AddStockDialog().show(getActivity().getFragmentManager(), "StockDialogFragment");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_settings, menu);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if (!Utility.networkUp(getContext()) && adapter.getItemCount() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_network));
        }

        getLoaderManager().initLoader(STOCK_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        if(mPosition != RecyclerView.NO_POSITION){
            outState.putInt(SELECTED_KEY,mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {

        QuoteSyncJob.syncImmediately(getContext());
        Log.d("HI", "HIIIIIII");


        if (!Utility.networkUp(getContext()) && adapter.getItemCount() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_network));
//            Utility.makeIndefiniteSnackBar(coordinatorLayout,getString(R.string.error_no_network));
            error.setVisibility(View.VISIBLE);
        } else if (!Utility.networkUp(getContext())) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
//            Utility.makeIndefiniteSnackBar(coordinatorLayout, getString(R.string.toast_no_connectivity));
        } else if (PrefUtils.getStocks(getContext()).size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_stocks));
//            Utility.makeSnackBar(coordinatorLayout, getString(R.string.error_no_stocks));
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
//            Utility.makeSnackBar(coordinatorLayout, "Stocks Updated!");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swipeRefreshLayout.setRefreshing(false);

        if (data.getCount() != 0) {
            error.setVisibility(View.GONE);
        } else if (adapter.getItemCount() == 0){
            error.setVisibility(View.VISIBLE);
        }
        adapter.setCursor(data);
        if(mPosition != RecyclerView.NO_POSITION){
            stockRecyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setCursor(null);
    }

    private void displayStatus(){

        String message;

        @Receiver.StockHawkStatus int status = Utility.getStockHawkStatus(getActivity());
        switch (status) {
            case Receiver.STATUS_OK:
                break;
            case Receiver.STATUS_INVALID_STOCK:
                String invalidSymbol = Utility.getInvalidSymbol(getContext());
                Utility.makeSnackBar(coordinatorLayout, invalidSymbol + " is an invalid SYMBOL");
                break;
            case Receiver.STATUS_ADDED:
                Utility.makeSnackBar(coordinatorLayout, "Symbol Added!");
                break;
            case Receiver.STATUS_NO_CONNECTION:
                Utility.makeSnackBar(coordinatorLayout, "Cannot perform action, please check your network connection.");
            default:
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        if(s.equals(getString(R.string.stock_hawk_status_key))){
            displayStatus();
        }

    }
}
