package com.example.nagel.io1.ui.function;

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

public class FunctionDetailAdapter
        extends RecyclerView.Adapter {

    private final FunctionDetailActivity mParentActivity;
    private final List<State> mValues;

    public FunctionDetailAdapter(FunctionDetailActivity parent,
                                 List<State> stateList) {
        mValues = stateList;
        mParentActivity = parent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.temp_listitem, parent, false);
                return new TempTypeViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.switch_listitem, parent, false);
                return new SwitchTypeViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {

        switch (mValues.get(position).getType()) {
            case "number":
                return 0;
            case "boolean":
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        State object = mValues.get(position);
        if (object != null) {
            switch (object.getType()) {
                case "number":
                    ((TempTypeViewHolder) holder).bindState(mValues.get(position));

                    break;
                case "boolean":
                    ((SwitchTypeViewHolder) holder).bindState(mValues.get(position));

                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if(mValues == null){
            return 0;
        }
        return mValues.size();
    }

    public class TempTypeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_title)  TextView mTitle;
        @BindView(R.id.message_subtitle)  TextView mSubtitle;
        @BindView(R.id.value)  TextView mValue;

        public TempTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindState(State state) {
            mTitle.setText(state.getId().replace("javascript.0.",""));
            mSubtitle.setText("Number" + state.getName());
            mValue.setText(state.getVal());
        }
    }

    public class SwitchTypeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_title)  TextView mTitle;
        @BindView(R.id.message_subtitle)  TextView mSubtitle;
        @BindView(R.id.value)  TextView mValue;

        public SwitchTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindState(State state) {
            mTitle.setText(state.getId().replace("javascript.0.",""));
            mSubtitle.setText("Boolean" + state.getName());
            mValue.setText(state.getVal());
        }
    }
}