package de.nisio.iobroker.ui.base.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.State;

public class SensorViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)
    TextView mValue;
    @BindView(R.id.icon)
    ImageView mIcon;

    public SensorViewHolder(View itemView) {
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
        switch (role){
            case State.ROLE_SENSOR_DOOR:
                mIcon.setImageResource(R.drawable.fts_door);
                break;
            case State.ROLE_SENSOR_WINDOW:
                mIcon.setImageResource(R.drawable.fts_window_2w);
                break;
            case State.ROLE_SENSOR_MOTION:
                mIcon.setImageResource(R.drawable.fts_motion);
                break;
            default:
                mIcon.setImageResource(R.drawable.fts_door);
                break;
        }
    }
}