package com.example.nagel.io1.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nagel.io1.R;
import com.example.nagel.io1.service.repository.State;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TemperatureListAdapter extends RecyclerView.Adapter<TemperatureListAdapter.TemperatureViewHolder> {
    private ArrayList<State> mList = new ArrayList<>();
    private Context mContext;

    public TemperatureListAdapter(Context context, ArrayList<State> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public TemperatureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_list, parent, false);
        TemperatureViewHolder viewHolder = new TemperatureViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TemperatureViewHolder holder, int position) {
        holder.bindTemperature(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class TemperatureViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_title)  TextView mTitle;
        @BindView(R.id.message_subtitle)  TextView mSubtitle;
        @BindView(R.id.value)  TextView mValue;
        private Context mContext;

        public TemperatureViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);
        }

        public void bindTemperature(State state) {
            mTitle.setText(state.id.substring(13).replace(".temperature",""));
            mSubtitle.setText("null");
            mValue.setText(state.val + " °C");
        }
    }
}