package com.example.nagel.io1.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nagel.io1.R;
import com.example.nagel.io1.service.model.Room;
import com.example.nagel.io1.service.model.State;
import com.example.nagel.io1.view.ui.RoomDetailActivity;
import com.example.nagel.io1.view.ui.RoomDetailFragment;
import com.example.nagel.io1.view.ui.RoomListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomListAdapter
        extends RecyclerView.Adapter<RoomListAdapter.ViewHolder> {

    private final RoomListActivity mParentActivity;
    private final List<Room> mValues;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Room item = (Room) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(RoomDetailFragment.ARG_ROOM_ID, item.getId());
                RoomDetailFragment fragment = new RoomDetailFragment();
                fragment.setArguments(arguments);
                //mParentActivity.getSupportFragmentManager().beginTransaction()
                //        .replace(R.id.room_detail_container, fragment)
                //        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, RoomDetailActivity.class);
                intent.putExtra(RoomDetailFragment.ARG_ROOM_ID, item.getId());

                context.startActivity(intent);
            }
        }
    };

    public RoomListAdapter(RoomListActivity parent,
                    List<Room> roomList,
                    boolean twoPane) {
        mValues = roomList;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bindRoom(mValues.get(position));
        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)  TextView mTitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        public void bindRoom(Room room) {
            mTitle.setText(room.getName());
        }
    }

}