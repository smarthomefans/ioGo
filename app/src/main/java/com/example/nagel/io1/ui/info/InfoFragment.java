package com.example.nagel.io1.ui.info;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nagel.io1.R;
import com.example.nagel.io1.di.Injectable;
import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.ui.room.RoomViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.AndroidSupportInjection;

/**
 * A placeholder fragment containing a simple view.
 */
public class InfoFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.countRooms)
    TextView mCountRooms;

    @BindView(R.id.countFunctions)
    TextView mCountFunctions;

    @BindView(R.id.countStates)
    TextView mCountStates;

    private InfoViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(InfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info, container, false);
        ButterKnife.bind(this, rootView);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(InfoViewModel.class);

        mViewModel.countRooms().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                // update UI
                mCountRooms.setText(value.toString());
            }
        });

        mViewModel.countFunctions().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                // update UI
                mCountFunctions.setText(value.toString());
            }
        });

        mViewModel.countStates().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                // update UI
                mCountStates.setText(value.toString());
            }
        });

        return rootView;
    }

    @OnClick(R.id.syncObjects)
    public void onClickSyncObjects(){
        DataBus.getBus().post(new Events.SyncObjects());
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
