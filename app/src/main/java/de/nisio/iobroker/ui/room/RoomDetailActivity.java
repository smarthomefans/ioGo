package de.nisio.iobroker.ui.room;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.nisio.iobroker.ui.base.BaseActivity;
import de.nisio.iobroker.R;
import de.nisio.iobroker.ui.main.EnumSettingsActivity;

/**
 * An activity representing a single Room detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RoomListActivity}.
 */
public class RoomDetailActivity extends BaseActivity implements HasSupportFragmentInjector {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.title_activity_room_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(RoomDetailFragment.ARG_ROOM_ID,
                    getIntent().getStringExtra(RoomDetailFragment.ARG_ROOM_ID));
            RoomDetailFragment fragment = new RoomDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.room_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            navigateUpTo(new Intent(this, RoomListActivity.class));
            return true;
        }

        if (id == R.id.action_room_settings) {
            Toast.makeText(this,R.string.action_settings, Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, EnumSettingsActivity.class);
            i.putExtra(RoomDetailFragment.ARG_ROOM_ID, getIntent().getStringExtra(RoomDetailFragment.ARG_ROOM_ID));
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room, menu);
        return true;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
