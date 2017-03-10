package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;

public class MainActivity extends AppCompatActivity implements MainFragment.CallBack{

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if(findViewById(R.id.detail_container) != null){

            mTwoPane = true;

            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        QuoteSyncJob.initialize(this);
    }

    @Override
    public void onItemSelected(Uri selectedUri) {
        if(mTwoPane){

            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.SELECTED_URI,selectedUri);

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container,detailFragment,DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this,DetailActivity.class)
                    .setData(selectedUri);

            startActivity(intent);

        }
    }
}
