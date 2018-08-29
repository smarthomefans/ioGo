package de.nisnagel.iogo.ui.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.ui.base.BaseActivity;

public class HistoryActivity extends BaseActivity implements HasSupportFragmentInjector {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_circle);
        getSupportActionBar().setTitle("");

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(Constants.ARG_STATE_ID,
                    getIntent().getStringExtra(Constants.ARG_STATE_ID));
            HistoryFragment fragment = new HistoryFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.history_container, fragment).commit();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

}
