package com.example.nagel.io1.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.nagel.io1.R;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BooleanTypeViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)
    Switch mValue;

    public BooleanTypeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(state.getId());
        mValue.setClickable(state.getWrite());
        mValue.setChecked("true".equals(state.getVal()));
        mValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                mSubtitle.setText("syncing data...");
                DataBus.getBus().post(new Events.SetState(state.getId(), (isChecked) ? "true" : "false"));
            }
        });
    }
}