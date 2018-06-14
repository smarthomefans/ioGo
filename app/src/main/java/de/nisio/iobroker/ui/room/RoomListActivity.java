package de.nisio.iobroker.ui.room;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.Room;
import de.nisio.iobroker.ui.base.BaseActivity;

/**
 * An activity representing a list of Rooms. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RoomDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RoomListActivity extends BaseActivity {

    private RoomViewModel mViewModel;
    private RoomListAdapter mAdapter;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.room_list)
    RecyclerView recyclerView;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public ArrayList<Room> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.title_activity_room_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RoomViewModel.class);

        mAdapter = new RoomListAdapter(list);
        recyclerView.setAdapter(mAdapter);

        mViewModel.getRooms()
                .observe(this, new Observer<List<Room>>() {
                    @Override
                    public void onChanged(@Nullable List<Room> newList) {
                        // update UI
                        mAdapter = new RoomListAdapter(newList);
                        recyclerView.setAdapter(mAdapter);
                    }
                });
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
