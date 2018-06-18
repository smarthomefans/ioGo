package de.nisio.iobroker.ui.function;


import android.arch.lifecycle.LiveData;
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
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.Enum;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.di.Injectable;
import de.nisio.iobroker.ui.base.BaseDetailAdapter;

/**
 * A fragment representing a single Function detail screen.
 * This fragment is either contained in a {@link FunctionListActivity}
 * in two-pane mode (on tablets) or a {@link FunctionDetailActivity}
 * on handsets.
 */
public class FunctionDetailFragment extends Fragment implements Injectable {

    public static final String ARG_FUNCTION_ID = "function_id";

    @BindView(R.id.function_detail_list) RecyclerView mRecyclerView;
    private BaseDetailAdapter mAdapter;

    private LiveData<Enum> mFunction;
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
            mFunction = mViewModel.getEnum(functionId);

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
                mAdapter = new BaseDetailAdapter(mListStates);
                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        });

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(FunctionViewModel.class);
        mViewModel.getEnum(functionId).observe(this, new Observer<Enum>() {
            @Override
            public void onChanged(@Nullable Enum function) {
                // update UI
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(function.getName());
            }
        });
        mViewModel.getStates(functionId)
                .observe(this, new Observer<List<State>>() {
                    @Override
                    public void onChanged(@Nullable List<State> newList) {
                        // update UI
                        mAdapter = new BaseDetailAdapter(newList);
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
