package de.nisnagel.iogo.ui.main;

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
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.ui.base.BaseActivity;

/**
 * An activity representing a list of Rooms. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EnumDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EnumSettingsActivity extends BaseActivity {

    private EnumViewModel mViewModel;

    private String roomId;

    private Enum mEnum;

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
        setContentView(R.layout.activity_enum_settings);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.title_activity_room_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);

        roomId = getIntent().getStringExtra(EnumDetailFragment.ARG_ENUM_ID);
        if (roomId != null) {
            mEnum = mViewModel.getEnum(roomId).getValue();
        }

        mViewModel.getEnum(roomId).observe(this, new Observer<Enum>() {
            @Override
            public void onChanged(@Nullable Enum anEnum) {
                // update UI
                getSupportActionBar().setTitle(anEnum.getName() + " - Settings");
                chkFavorit.setChecked("true".equals(anEnum.getFavorite()));
            }
        });

    }

    @OnClick(R.id.chkFavorit)
    public void onClickChkFavorit(){
        String tmp = (chkFavorit.isChecked()) ? "true" : "false";
        mEnum.setFavorite(tmp);
        mViewModel.saveEnum(mEnum);
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
