package de.nisnagel.iogo.ui.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

public class StateActivity extends BaseActivity implements HasSupportFragmentInjector {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_circle);
        getSupportActionBar().setTitle("");

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(Constants.ARG_STATE_ID,
                    getIntent().getStringExtra(Constants.ARG_STATE_ID));
            StateFragment fragment = new StateFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.state_container, fragment).commit();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

}
