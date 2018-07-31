package de.nisnagel.iogo.ui.main;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import de.nisnagel.iogo.data.model.Enum;
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

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        });

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);
        mViewModel.getEnum(enumId).observe(this, new Observer<Enum>() {
            @Override
            public void onChanged(@Nullable Enum anEnum) {
                // update UI
                if(anEnum != null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(anEnum.getName());
                }
            }
        });

        mViewModel.getStates(enumId)
                .observe(this, new Observer<List<State>>() {
                    @Override
                    public void onChanged(@Nullable List<State> newList) {
                        // update UI
                        mAdapter.clearList();
                        mAdapter.addAll(newList);
                        mAdapter.notifyDataSetChanged();
                    }
                });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
