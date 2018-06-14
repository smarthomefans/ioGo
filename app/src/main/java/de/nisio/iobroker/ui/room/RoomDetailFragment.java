package de.nisio.iobroker.ui.room;


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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.Room;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.di.Injectable;
import de.nisio.iobroker.ui.base.BaseDetailAdapter;

/**
 * A fragment representing a single Room detail screen.
 * This fragment is either contained in a {@link RoomListActivity}
 * in two-pane mode (on tablets) or a {@link RoomDetailActivity}
 * on handsets.
 */
public class RoomDetailFragment extends Fragment implements Injectable {

    public static final String ARG_ROOM_ID = "room_id";

    @BindView(R.id.room_detail_list) RecyclerView mRecyclerView;

    private BaseDetailAdapter mAdapter;

    private LiveData<Room> mRoom;
    private List<State> mListStates = new ArrayList<>();
    private RoomViewModel mViewModel;

    private String roomId;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RoomViewModel.class);

        roomId = getArguments().getString(ARG_ROOM_ID);
        if (getArguments().containsKey(ARG_ROOM_ID)) {
            mRoom = mViewModel.getRoom(roomId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.room_detail, container, false);
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

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RoomViewModel.class);
        mViewModel.getRoom(roomId).observe(this, new Observer<Room>() {
            @Override
            public void onChanged(@Nullable Room room) {
                // update UI
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(room.getName());
            }
        });

        mViewModel.getStates(roomId)
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
