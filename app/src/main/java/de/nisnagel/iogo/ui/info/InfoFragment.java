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

package de.nisnagel.iogo.ui.info;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.di.Injectable;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.Events;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class InfoFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.account_state)
    TextView mAccountState;

    @BindView(R.id.countRooms)
    TextView mCountRooms;

    @BindView(R.id.countFunctions)
    TextView mCountFunctions;

    @BindView(R.id.countStates)
    TextView mCountStates;

    @BindView(R.id.socketState)
    TextView mSocketState;

    @BindView(R.id.appVersion)
    TextView mAppVersion;

    private InfoViewModel mViewModel;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(InfoViewModel.class);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getActivity()));
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, rootView);
        mAccountState.setText("unknown");

        mAuthListener = firebaseAuth -> {
            FirebaseUser mUser = firebaseAuth.getCurrentUser();
            if(mUser != null) {
                if (mUser.isAnonymous()) {
                    mAccountState.setText("anonymous logged in");
                } else if (mUser.isEmailVerified()) {
                    mAccountState.setText("loggen in");
                } else if (mUser.isEmailVerified()) {
                    mAccountState.setText("email not verified");
                }
            }
        };

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(InfoViewModel.class);

        mViewModel.countRooms().observe(this, value -> {
            // update UI
            if (value != null) {
                mCountRooms.setText(value.toString());
            }
        });

        mViewModel.countFunctions().observe(this, value -> {
            // update UI
            if (value != null) {
                mCountFunctions.setText(value.toString());
            }
        });

        mViewModel.countStates().observe(this, value -> {
            // update UI
            if (value != null) {
                mCountStates.setText(value.toString());
            }
        });

        mViewModel.getSocketState().observe(this, value -> {
            // update UI
            if (value != null) {
                mSocketState.setText(value);
            }
        });

        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            mAppVersion.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return rootView;
    }

    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @OnClick(R.id.syncObjects)
    public void onClickSyncObjects() {
        DataBus.getBus().post(new Events.SyncObjects());
        mFirebaseAnalytics.logEvent("sync_ojects", null);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
