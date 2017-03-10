package com.udacity.stockhawk.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddStockDialog extends DialogFragment {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.dialog_stock)
    EditText stock;

    private static final String ACTION_SYMBOL_ADDED = "com.udacity.stockhawk.ACTION_SYMOL_ADDED";
    private static final String ACTION_NO_NETWORK_CONNECTION_TO_ADD = "com.udacity.stockhawk.ACTION_NNCTA";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View custom = inflater.inflate(R.layout.add_stock_dialog, null);

        ButterKnife.bind(this, custom);

        stock.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(!Utility.networkUp(getActivity())) {
                    addStock();
                } else{
                    Intent intent = new Intent(ACTION_NO_NETWORK_CONNECTION_TO_ADD);
                    getActivity().sendBroadcast(intent);
                }

                return true;
            }
        });
        builder.setView(custom);

        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            addStock();

                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void addStock() {

        String symbolStr = stock.getText().toString();

        if (!symbolStr.isEmpty()) {
            Intent intent = new Intent(ACTION_SYMBOL_ADDED);
            getActivity().sendBroadcast(intent);

            PrefUtils.addStock(getActivity(), symbolStr);
            QuoteSyncJob.syncImmediately(getActivity());

            dismissAllowingStateLoss();

        }

    }
}
