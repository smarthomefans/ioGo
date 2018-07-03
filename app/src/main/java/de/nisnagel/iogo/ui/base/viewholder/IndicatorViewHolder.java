package de.nisnagel.iogo.ui.base.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;

public class IndicatorViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)
    TextView mValue;
    @BindView(R.id.icon)
    ImageView mIcon;

    public IndicatorViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(state.getRole());
        String val = ("true".equals(state.getVal())) ? "open" : "closed";
        mValue.setText(val);
        setImageRessource(state.getRole());
    }

    private void setImageRessource(String role){
        if(role != null) {
            switch (role) {
                case State.ROLE_INDICATOR_CONNECTED:
                    mIcon.setImageResource(R.drawable.lan_connect);
                    break;
                case State.ROLE_INDICATOR_LOWBAT:
                    mIcon.setImageResource(R.drawable.battery_10);
                    break;
            }
        }
    }
}