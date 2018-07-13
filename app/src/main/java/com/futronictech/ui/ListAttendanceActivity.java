package com.futronictech.ui;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.R;
import com.futronictech.adapter.ListAdapter;
import com.futronictech.model.BWStaff_Datum;
import com.futronictech.network.APIClient;
import com.futronictech.network.APIInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListAttendanceActivity extends AppCompatActivity implements
        ListAdapter.ListAdapterOnClickHandler {
    APIInterface mApiInterface;
    ListAdapter mAdapter;
    RecyclerView mRecyclerView;
    ProgressBar mLoadingIndicator;
    TextView mErrorMessageDisplay;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_attendance);
        mApiInterface = APIClient.getClient().create(APIInterface.class);

        // Find the views
        mRecyclerView = findViewById(R.id.rv_dev_list);
        mErrorMessageDisplay = findViewById(R.id.error_view);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        mLoadingIndicator = findViewById(R.id.loading_indicator);

        // Set up the recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ListAdapter(this,this);
        mRecyclerView.setAdapter(mAdapter);

        // Set up the swipe and refresh
        swipeAndRefresh();

        makeDataRequest();
    }

    private void swipeAndRefresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeDataRequest();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void makeDataRequest() {
        if(checkNetworkConnectivity()) {
            // Show the loading indicator
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
            // Start the calling process

            Call<List<BWStaff_Datum>> call = mApiInterface.doGetUserList();
            call.enqueue(new Callback<List<BWStaff_Datum>>() {
                @Override
                public void onResponse(@NonNull Call<List<BWStaff_Datum>> call, @NonNull Response<List<BWStaff_Datum>> response) {
                    mAdapter.setData((ArrayList<BWStaff_Datum>) response.body());
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(@NonNull Call<List<BWStaff_Datum>> call, @NonNull Throwable throwable) {
                    mErrorMessageDisplay.setText(R.string.some_error);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mErrorMessageDisplay.setVisibility(View.VISIBLE);
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                }
            });
        } else{
            // Show the no connection error message
            mErrorMessageDisplay.setText(R.string.no_internet_connection);
            // Show the error message
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    private Boolean checkNetworkConnectivity() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;

        // If there is a network connection, return true, else return false
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onClick(String[] profile) {
        Intent intent = new Intent(this, AttendanceRegisterActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,profile);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.refresh_page:
                swipeAndRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_selector, menu);
        return true;
    }
}
