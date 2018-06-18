package de.nisio.iobroker.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import de.nisio.iobroker.R;
import de.nisio.iobroker.service.DataBus;
import de.nisio.iobroker.data.model.Enum;
import de.nisio.iobroker.ui.base.BaseActivity;
import de.nisio.iobroker.ui.function.FunctionListActivity;
import de.nisio.iobroker.ui.room.RoomListActivity;
import de.nisio.iobroker.ui.room.RoomViewModel;

public class MainActivity extends BaseActivity {

    private RoomViewModel mViewModel;
    private EnumListAdapter mAdapter;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.favorite_list)
    RecyclerView recyclerView;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public ArrayList<Enum> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitleMarginStart(120);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.home);

        DataBus.getBus().register(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RoomViewModel.class);

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
        Intent i = new Intent(this, RoomListActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.showFunctionList)
    public void onClickFuncionList(){
        Intent i = new Intent(this, FunctionListActivity.class);
        startActivity(i);
    }

}
