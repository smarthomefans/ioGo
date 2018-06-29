package de.nisio.iobroker.ui.base.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.service.DataBus;
import de.nisio.iobroker.service.Events;

public class SwitchViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)
    Switch mValue;
    @BindView(R.id.icon)
    ImageView mIcon;

    public SwitchViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(state.getRole());
        mValue.setChecked("true".equals(state.getVal()));
        if(state.getWrite()) {
            mValue.setClickable(true);
            mValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    mSubtitle.setText("syncing data...");
                    DataBus.getBus().post(new Events.SetState(state.getId(), (isChecked) ? "true" : "false"));
                }
            });
        }
        setImageRessource(state.getRole());
    }

    private void setImageRessource(String role){
        if(role != null) {
            switch (role) {
                case State.ROLE_SWITCH:
                    mIcon.setImageResource(R.drawable.toggle_switch);
                    break;
                case State.ROLE_SWITCH_LIGHT:
                    mIcon.setImageResource(R.drawable.lightbulb);
                    break;
            }
        }
    }
}