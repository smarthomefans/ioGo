package de.nisio.iobroker.ui.main;

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
import de.nisio.iobroker.data.model.Enum;
import de.nisio.iobroker.data.repository.EnumRepository;
import de.nisio.iobroker.ui.base.BaseActivity;

/**
 * An activity representing a list of Rooms. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EnumDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EnumListActivity extends BaseActivity {

    public static final String ARG_ENUM_TYPE = "enum_type";

    private EnumListAdapter mAdapter;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.enum_list)
    RecyclerView recyclerView;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public ArrayList<Enum> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enum_list);
        ButterKnife.bind(this);

        String enumType = getIntent().getStringExtra(ARG_ENUM_TYPE);
        if(EnumRepository.TYPE_FUNCTION.equals(enumType)) {
            toolbar.setTitle(R.string.title_activity_function_list);
        }else if(EnumRepository.TYPE_ROOM.equals(enumType)) {
            toolbar.setTitle(R.string.title_activity_room_list);
        }else if(EnumRepository.TYPE_ROOM.equals(enumType)) {
            toolbar.setTitle("Unknown");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EnumViewModel mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);

        mAdapter = new EnumListAdapter(list);
        recyclerView.setAdapter(mAdapter);

        mViewModel.getEnums(enumType)
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}