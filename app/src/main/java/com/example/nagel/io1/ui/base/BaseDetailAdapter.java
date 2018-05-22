package com.example.nagel.io1.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.nagel.io1.R;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.ui.function.FunctionDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseDetailAdapter
        extends RecyclerView.Adapter {

    private final FunctionDetailActivity mParentActivity;
    private final List<State> mValues;

    public BaseDetailAdapter(FunctionDetailActivity parent,
                             List<State> stateList) {
        mValues = stateList;
        mParentActivity = parent;
        DataBus.getBus().register(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.switch_listitem, parent, false);
                return new BooleanTypeViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.number_listitem, parent, false);
                return new NumberTypeViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        State object = mValues.get(position);
        if (object != null && object.getType() != null) {
            switch (object.getType()) {
                case State.TYPE_BOOLEAN:
                    return 0;
                case State.TYPE_STRING:
                    return 1;
                case State.TYPE_NUMBER:
                    return 1;
                default:
                    return 1;
            }
        }else{
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        State object = mValues.get(position);
        if (object != null && object.getType() != null) {
            switch (object.getType()) {
                case State.TYPE_NUMBER:
                    ((NumberTypeViewHolder) holder).bindState(mValues.get(position));

                    break;
                case State.TYPE_BOOLEAN:
                    ((BooleanTypeViewHolder) holder).bindState(mValues.get(position));

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

    public class NumberTypeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_title)  TextView mTitle;
        @BindView(R.id.message_subtitle)  TextView mSubtitle;
        @BindView(R.id.value)  TextView mValue;

        public NumberTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindState(State state) {
            mTitle.setText(state.getId().replace("javascript.0.",""));
            mSubtitle.setText(state.getName());
            mValue.setText(state.getVal() + state.getUnit());
        }
    }

    public class BooleanTypeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_title)  TextView mTitle;
        @BindView(R.id.message_subtitle)  TextView mSubtitle;
        @BindView(R.id.value)  Switch mValue;

        public BooleanTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindState(State state) {
            mTitle.setText(state.getId().replace("javascript.0.",""));
            mSubtitle.setText(state.getName());
            mValue.setClickable(state.getWrite());
            mValue.setChecked("true".equals(state.getVal()));
            mValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    // TODO: handle your switch toggling logic here
                    mSubtitle.setText("syncing data...");
                    DataBus.getBus().post(new Events.SetState(state.getId(), (isChecked) ? "true" : "false"));
                }
            });
        }
    }
}