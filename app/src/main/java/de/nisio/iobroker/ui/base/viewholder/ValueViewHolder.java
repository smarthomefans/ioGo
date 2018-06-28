package de.nisio.iobroker.ui.base.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.State;

public class ValueViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)  TextView mValue;
    @BindView(R.id.icon)
    ImageView mIcon;

    public ValueViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(state.getRole());
        String value = state.getVal();
        if(state.getUnit() != null){
            value += state.getUnit();
        }
        mValue.setText(value);
        setImageRessource(state.getRole());
    }

    private void setImageRessource(String role){
        switch (role){
            case State.ROLE_VALUE_TEMPERATURE:
                mIcon.setImageResource(R.drawable.thermometer);
                break;
            default:
                mIcon.setImageResource(R.drawable.circle);
                break;
        }
    }
}