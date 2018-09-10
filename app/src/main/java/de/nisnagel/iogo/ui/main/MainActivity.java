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

package de.nisnagel.iogo.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.Events;
import de.nisnagel.iogo.ui.base.BaseActivity;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements HasSupportFragmentInjector, BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.bottombar) BottomNavigationView bottombar;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitleMarginStart(120);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.test48);

        DataBus.getBus().register(this);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        PreferenceManager.setDefaultValues(this, R.xml.settings_connection, false);
        PreferenceManager.setDefaultValues(this, R.xml.settings_design, false);
        PreferenceManager.setDefaultValues(this, R.xml.settings_error, false);
        PreferenceManager.setDefaultValues(this, R.xml.settings_notification, false);

        //loading the default fragment
        setTitle(R.string.title_activity_home);
        loadFragment(new HomeFragment());

        //getting bottom navigation view and attaching the listener
        bottombar.setOnNavigationItemSelectedListener(this);

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        //MobileAds.initialize(this, "@string/ad_unit_id");

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this, instanceIdResult -> {
                                String newToken = instanceIdResult.getToken();
                                Timber.d("newToken" + newToken);

                                String uniqueId = user.getUid();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("users");
                                myRef.child(uniqueId).child("token").setValue(newToken);

                                String fcm_user = sharedPreferences.getString("fcm_user", null);
                                if(fcm_user != null) {
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            DataBus.getBus().post(new Events.SetState("iogo.0." + fcm_user + ".token", newToken));
                                        }
                                    }, 10000);
                                }

                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.w("signInAnonymously:failure", task.getException());
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.showHome:
                fragment = new HomeFragment();
                setTitle(R.string.title_activity_home);
                break;

            case R.id.showFunctionList:
                fragment = new FunctionFragment();
                setTitle(R.string.title_activity_function_list);
                break;

            case R.id.showRoomList:
                fragment = new RoomFragment();
                setTitle(R.string.title_activity_room_list);
                break;
        }

        return loadFragment(fragment);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
