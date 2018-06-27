package de.nisio.iobroker.ui.main;


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
 * A fragment representing a single Room detail screen.
 * This fragment is either contained in a {@link EnumListActivity}
 * in two-pane mode (on tablets) or a {@link EnumDetailActivity}
 * on handsets.
 */
public class EnumDetailFragment extends Fragment implements Injectable {

    public static final String ARG_ENUM_ID = "enum_id";

    @BindView(R.id.enum_detail_list) RecyclerView mRecyclerView;

    private BaseDetailAdapter mAdapter;

    private LiveData<Enum> mRoom;
    private List<State> mListStates = new ArrayList<>();
    private EnumViewModel mViewModel;

    private String enumId;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);

        enumId = getArguments().getString(ARG_ENUM_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.enum_detail, container, false);
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

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);
        mViewModel.getEnum(enumId).observe(this, new Observer<Enum>() {
            @Override
            public void onChanged(@Nullable Enum anEnum) {
                // update UI
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(anEnum.getName());
            }
        });

        mViewModel.getStates(enumId)
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