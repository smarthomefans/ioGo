package de.nisio.iobroker.ui.room;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.Room;

public class RoomListAdapter
        extends RecyclerView.Adapter<RoomListAdapter.ViewHolder> {

    private final List<Room> mValues;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Room item = (Room) view.getTag();

            Context context = view.getContext();
            Intent intent = new Intent(context, RoomDetailActivity.class);
            intent.putExtra(RoomDetailFragment.ARG_ROOM_ID, item.getId());

            context.startActivity(intent);

        }
    };

    public RoomListAdapter(List<Room> roomList) {
        mValues = roomList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_room, parent, false);
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