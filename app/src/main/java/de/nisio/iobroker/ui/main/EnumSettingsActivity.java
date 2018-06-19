package de.nisio.iobroker.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.Enum;
import de.nisio.iobroker.ui.base.BaseActivity;
import de.nisio.iobroker.ui.main.EnumViewModel;
import de.nisio.iobroker.ui.room.RoomDetailActivity;
import de.nisio.iobroker.ui.room.RoomDetailFragment;

/**
 * An activity representing a list of Rooms. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RoomDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EnumSettingsActivity extends BaseActivity {

    private EnumViewModel mViewModel;

    private String roomId;

    private Enum mRoom;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.chkFavorit)
    protected CheckBox chkFavorit;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public ArrayList<Enum> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_settings);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.title_activity_room_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);

        roomId = getIntent().getStringExtra(RoomDetailFragment.ARG_ROOM_ID);
        if (roomId != null) {
            mRoom = mViewModel.getEnum(roomId).getValue();
        }

        mViewModel.getEnum(roomId).observe(this, new Observer<Enum>() {
            @Override
            public void onChanged(@Nullable Enum room) {
                // update UI
                getSupportActionBar().setTitle(room.getName() + " - Settings");
            }
        });

    }

    @OnClick(R.id.chkFavorit)
    public void onClickChkFavorit(){
        Toast.makeText(this,"Test",Toast.LENGTH_SHORT).show();
        mViewModel.flagFavorite(roomId, chkFavorit.isChecked());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
