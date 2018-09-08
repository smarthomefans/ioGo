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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.di.Injectable;


public class HomeFragment extends Fragment implements Injectable {

    private EnumHomeListAdapter mEnumAdapter;
    private StateListAdapter mStateAdapter;
    private EnumViewModel mViewModel;

    @BindView(R.id.adView)
    AdView mAdView;
    @BindView(R.id.favorite_enums)
    RecyclerView rvEnums;
    @BindView(R.id.favorite_states)
    RecyclerView rvStates;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private ArrayList<Enum> enumList = new ArrayList<>();
    private ArrayList<State> stateList = new ArrayList<>();

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

        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);

        mEnumAdapter = new EnumHomeListAdapter(enumList, mViewModel);
        mStateAdapter = new StateListAdapter(stateList, mViewModel, getActivity());

        getActivity().runOnUiThread(() -> {

            rvEnums.setAdapter(mEnumAdapter);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            rvEnums.setLayoutManager(mLayoutManager);

            rvStates.setAdapter(mStateAdapter);
        });

        mViewModel.getFavoriteEnums()
                .observe(this, newList -> {
                    // update UI
                    mEnumAdapter.clearList();
                    mEnumAdapter.addAll(newList);
                    mEnumAdapter.notifyDataSetChanged();
                });

        mViewModel.getFavoriteStates()
                .observe(this, newList -> {
                    // update UI
                    mStateAdapter.clearList();
                    mStateAdapter.addAll(newList);
                    mStateAdapter.notifyDataSetChanged();
                });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
