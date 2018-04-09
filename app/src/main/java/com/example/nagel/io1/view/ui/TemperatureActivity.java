package com.example.nagel.io1.view.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;

import com.example.nagel.io1.R;
import com.example.nagel.io1.service.model.Temperature;
import com.example.nagel.io1.view.adapter.TemperatureListAdapter;
import com.example.nagel.io1.viewmodel.ListViewModel;

import java.util.ArrayList;
import java.util.List;

public class TemperatureActivity extends AppCompatActivity {

    public static final String TAG = TemperatureActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TemperatureListAdapter mAdapter;
    private ListViewModel mViewModel;

    public ArrayList<Temperature> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.recyclerView);

        TemperatureActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter = new TemperatureListAdapter(getApplicationContext(), list);
                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(TemperatureActivity.this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        });

        mViewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        mViewModel.getTempList()
                .observe(this, new Observer<List<Temperature>>() {
                    @Override
                    public void onChanged(@Nullable List<Temperature> newList) {
                        // update UI
                        mAdapter = new TemperatureListAdapter(getApplicationContext(), (ArrayList)newList);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}