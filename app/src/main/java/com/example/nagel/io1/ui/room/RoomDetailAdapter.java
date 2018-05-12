package com.example.nagel.io1.ui.room;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nagel.io1.R;
import com.example.nagel.io1.data.model.State;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomDetailAdapter
        extends RecyclerView.Adapter<RoomDetailAdapter.StateViewHolder> {

    private final RoomDetailActivity mParentActivity;
    private final List<State> mValues;

    public RoomDetailAdapter(RoomDetailActivity parent,
                             List<State> stateList) {
        mValues = stateList;
        mParentActivity = parent;
    }

    @Override
    public StateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.material_list, parent, false);
        return new StateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StateViewHolder holder, int position) {
        holder.bindState(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        if(mValues == null){
            return 0;
        }
        return mValues.size();
    }

    public class StateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_title)  TextView mTitle;
        @BindView(R.id.message_subtitle)  TextView mSubtitle;
        @BindView(R.id.value)  TextView mValue;

        public StateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindState(State state) {
            mTitle.setText(state.getId().replace("javascript.0.",""));
            mSubtitle.setText(state.getName());

            if(state.getUnit() != null) {
                mValue.setText(state.getVal() + state.getUnit());
            }else{
                mValue.setText(state.getVal());
            }
        }
    }
}