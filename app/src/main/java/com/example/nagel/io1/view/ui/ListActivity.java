package com.example.nagel.io1.view.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.nagel.io1.viewmodel.ListViewModel;
import com.example.nagel.io1.R;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListViewModel mViewModel;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listview);

        //mViewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        /*mViewModel.getList()
                .observe(this, new Observer<List<String>>() {
                    @Override
                    public void onChanged(@Nullable List<String> stringList) {
                        // update UI
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ListActivity.this,
                                R.layout.list_item, R.id.list_item_textview, stringList);
                        // Assign adapter to ListView
                        listView.setAdapter(adapter);
                    }
                });*/

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}


