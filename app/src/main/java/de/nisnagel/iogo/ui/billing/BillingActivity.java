/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.ui.billing;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.ui.base.BaseActivity;
import timber.log.Timber;

public class BillingActivity extends BaseActivity implements BillingProvider {

    private BillingManager mBillingManager;
    private String uid;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_billing);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.billing_title_activity_billing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        }else{
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_LONG).show();
            NavUtils.navigateUpFromSameTask(this);
        }

        // Create and initialize BillingManager which talks to BillingLibrary
        if(mBillingManager == null) {
            mBillingManager = new BillingManager(this, uid);
        }

        if (savedInstanceState == null) {
            BillingFragment fragment = new BillingFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.billing_container, fragment).commit();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBillingManager.destroy();
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

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

}
