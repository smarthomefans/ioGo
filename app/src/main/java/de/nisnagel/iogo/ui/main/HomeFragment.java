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

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.di.Injectable;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.ui.auth.SignupActivity;
import de.nisnagel.iogo.ui.settings.SettingsMainActivity;


public class HomeFragment extends Fragment implements Injectable {

    private EnumHomeListAdapter mEnumAdapter;
    private StateListAdapter mStateAdapter;
    private EnumViewModel mViewModel;
    private ArrayList<Enum> enumList = new ArrayList<>();
    private ArrayList<State> stateList = new ArrayList<>();

    @BindView(R.id.favorite_enums)
    RecyclerView rvEnums;
    @BindView(R.id.favorite_states)
    RecyclerView rvStates;
    @BindView(R.id.welcome)
    LinearLayout mWelcome;
    @BindView(R.id.btnRegister)
    Button mRegister;
    @BindView(R.id.btnConnect)
    Button mConnect;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mEnumAdapter = new EnumHomeListAdapter(enumList, mViewModel);
        mStateAdapter = new StateListAdapter(stateList, mViewModel, getActivity());
        rvEnums.setVisibility(View.GONE);
        rvStates.setVisibility(View.GONE);

        getActivity().runOnUiThread(() -> {

            rvEnums.setAdapter(mEnumAdapter);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            rvEnums.setLayoutManager(mLayoutManager);

            rvStates.setAdapter(mStateAdapter);
        });

        mViewModel.getFavoriteEnums()
                .observe(this, newList -> {
                    // update UI
                    if (newList.size() > 0) {
                        mWelcome.setVisibility(View.GONE);
                        rvEnums.setVisibility(View.VISIBLE);
                    }
                    mEnumAdapter.clearList();
                    mEnumAdapter.addAll(newList);
                    mEnumAdapter.notifyDataSetChanged();
                });

        mViewModel.getFavoriteStates()
                .observe(this, newList -> {
                    // update UI
                    if (newList.size() > 0) {
                        mWelcome.setVisibility(View.GONE);
                        rvStates.setVisibility(View.VISIBLE);
                    }
                    mStateAdapter.clearList();
                    mStateAdapter.addAll(newList);
                    mStateAdapter.notifyDataSetChanged();
                });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mRegister.setVisibility(View.GONE);
        }

        if(mViewModel.hasConnection()){
            mConnect.setVisibility(View.GONE);
        }

        return rootView;
    }

    @OnClick(R.id.btnRegister)
    public void onClickRegister() {
        startActivity(new Intent(getActivity(), SignupActivity.class));
    }

    @OnClick(R.id.btnConnect)
    public void onClickConnect() {
        Intent i = new Intent(getActivity(), SettingsMainActivity.class);
        i.putExtra(Constants.ARG_CLASS, SettingsMainActivity.intentServer);
        startActivity(i);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
