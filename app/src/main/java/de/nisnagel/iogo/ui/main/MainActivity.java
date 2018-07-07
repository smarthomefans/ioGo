package de.nisnagel.iogo.ui.main;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.ui.base.BaseActivity;

public class MainActivity extends BaseActivity {

    private EnumListAdapter mAdapter;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.favorite_list)
    RecyclerView recyclerView;

    @BindView(R.id.adView)
    AdView mAdView;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public ArrayList<Enum> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        isStoragePermissionGranted();
        toolbar.setTitleMarginStart(120);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.home);

        DataBus.getBus().register(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        EnumViewModel mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);

        mAdapter = new EnumListAdapter(list);
        recyclerView.setAdapter(mAdapter);

        mViewModel.getFavoriteEnums()
                .observe(this, new Observer<List<Enum>>() {
                    @Override
                    public void onChanged(@Nullable List<Enum> newList) {
                        // update UI
                        mAdapter = new EnumListAdapter(newList);
                        recyclerView.setAdapter(mAdapter);
                    }
                });

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getString(R.string.ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("MAIN","Permission is granted");
                return true;
            } else {

                Log.v("MAIN","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("MAIN","Permission is granted");
            return true;
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataBus.getBus().unregister(this);
    }

    @OnClick(R.id.showRoomList)
    public void onClickRoomList(){
        Intent i = new Intent(this, EnumListActivity.class);
        i.putExtra(EnumListActivity.ARG_ENUM_TYPE, EnumRepository.TYPE_ROOM);
        startActivity(i);
    }

    @OnClick(R.id.showFunctionList)
    public void onClickFuncionList(){
        Intent i = new Intent(this, EnumListActivity.class);
        i.putExtra(EnumListActivity.ARG_ENUM_TYPE, EnumRepository.TYPE_FUNCTION);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
