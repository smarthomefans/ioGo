package com.example.nagel.io1.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nagel.io1.R;
import com.example.nagel.io1.service.model.IoState;

import java.util.ArrayList;

public class TemperatureListAdapter extends RecyclerView.Adapter<TemperatureListAdapter.TemperatureViewHolder> {
    private ArrayList<IoState> mList = new ArrayList<>();
    private Context mContext;

    public TemperatureListAdapter(Context context, ArrayList<IoState> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public TemperatureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.temp_listitem, parent, false);
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
        private TextView mNameTextView;
        private TextView mValueTextView;
        private Context mContext;

        public TemperatureViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mNameTextView = itemView.findViewById(R.id.tempNameTextView);
            mValueTextView = itemView.findViewById(R.id.tempValueTextView);
        }

        public void bindTemperature(IoState state) {
            mNameTextView.setText(state.getId().substring(13).replace(".temperature",""));
            mValueTextView.setText(state.getVal());
        }
    }
}