package com.example.nagel.io1.view.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nagel.io1.R;
import com.example.nagel.io1.di.Injectable;
import com.example.nagel.io1.service.model.IoState;
import com.example.nagel.io1.view.adapter.TemperatureListAdapter;
import com.example.nagel.io1.viewmodel.ListViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentActivityFragment extends Fragment implements Injectable{

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    private TemperatureListAdapter mAdapter;
    private ListViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public ArrayList<IoState> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment, container, false);
        ButterKnife.bind(this, view);
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter = new TemperatureListAdapter(getActivity().getApplicationContext(), list);
                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        });

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ListViewModel.class);

        mViewModel.getTempList()
                .observe(this, new Observer<List<IoState>>() {
                    @Override
                    public void onChanged(@Nullable List<IoState> newList) {
                        // update UI
                        mAdapter = new TemperatureListAdapter(getActivity().getApplicationContext(), (ArrayList)newList);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
