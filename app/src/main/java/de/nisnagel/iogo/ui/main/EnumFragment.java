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
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.di.Injectable;
import de.nisnagel.iogo.service.Constants;


public class EnumFragment extends Fragment implements Injectable {

    @BindView(R.id.enum_detail_list) RecyclerView mRecyclerView;

    private StateListAdapter mAdapter;

    private List<State> mListStates = new ArrayList<>();
    private EnumViewModel mViewModel;

    private String enumId;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);

        enumId = getArguments().getString(Constants.ARG_ENUM_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_enum, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new StateListAdapter(mListStates, mViewModel, getActivity());

        getActivity().runOnUiThread(() -> {
            mRecyclerView.setAdapter(mAdapter);
            RecyclerView.LayoutManager layoutManager =
                    new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);
        });

        mViewModel.getEnum(enumId).observe(this, anEnum -> {
            // update UI
            if(anEnum != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(anEnum.getName());
            }
        });

        mViewModel.getStates(enumId)
                .observe(this, newList -> {
                    // update UI
                    mAdapter.clearList();
                    mAdapter.addAll(newList);
                    mAdapter.notifyDataSetChanged();
                });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
