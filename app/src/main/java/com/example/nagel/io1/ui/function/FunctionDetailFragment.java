package com.example.nagel.io1.ui.function;


import android.arch.lifecycle.LiveData;
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
import com.example.nagel.io1.data.model.Function;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.di.Injectable;
import com.example.nagel.io1.ui.base.BaseDetailAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

/**
 * A fragment representing a single Function detail screen.
 * This fragment is either contained in a {@link FunctionListActivity}
 * in two-pane mode (on tablets) or a {@link FunctionDetailActivity}
 * on handsets.
 */
public class FunctionDetailFragment extends Fragment implements Injectable {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_FUNCTION_ID = "function_id";

    /**
     * The dummy content this fragment is presenting.
     */
    @BindView(R.id.function_detail_list) RecyclerView mRecyclerView;
    private BaseDetailAdapter mAdapter;

    private LiveData<Function> mFunction;
    private List<State> mListStates = new ArrayList<>();
    private FunctionViewModel mViewModel;

    private String functionId;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(FunctionViewModel.class);
        functionId = getArguments().getString(ARG_FUNCTION_ID);
        if (getArguments().containsKey(ARG_FUNCTION_ID)) {
            mFunction = mViewModel.getFunction(functionId);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.function_detail, container, false);
        ButterKnife.bind(this, rootView);
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter = new BaseDetailAdapter(null, mListStates);
                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        });

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(FunctionViewModel.class);

        mViewModel.getStates(functionId)
                .observe(this, new Observer<List<State>>() {
                    @Override
                    public void onChanged(@Nullable List<State> newList) {
                        // update UI
                        mAdapter = new BaseDetailAdapter(null, newList);
                        mRecyclerView.setAdapter(mAdapter);
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
