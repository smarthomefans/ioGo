package de.nisio.iobroker.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import de.nisio.iobroker.R;
import de.nisio.iobroker.service.DataBus;
import de.nisio.iobroker.ui.base.BaseActivity;
import de.nisio.iobroker.ui.function.FunctionListActivity;
import de.nisio.iobroker.ui.info.InfoActivity;
import de.nisio.iobroker.ui.room.RoomListActivity;
import de.nisio.iobroker.ui.settings.SettingsActivity;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

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
