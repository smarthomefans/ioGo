package de.nisnagel.iogo.ui.base.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;

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
        setValue(state.getRole(), state.getVal());
        setImageRessource(state.getRole());
    }

    private void setValue(String role, String val){
        if(role != null) {
            switch (role) {
                case State.ROLE_SENSOR_DOOR:
                    mValue.setText(("true".equals(val)) ? "open" : "closed");
                    break;
                case State.ROLE_SENSOR_WINDOW:
                    mValue.setText(("true".equals(val)) ? "open" : "closed");
                    break;
                default:
                    mValue.setText(val);
                    break;
            }
        }
    }

    private void setImageRessource(String role){
        if(role != null) {
            switch (role) {
                case State.ROLE_SENSOR_DOOR:
                    mIcon.setImageResource(R.drawable.door);
                    break;
                case State.ROLE_SENSOR_WINDOW:
                    mIcon.setImageResource(R.drawable.window_closed);
                    break;
                case State.ROLE_SENSOR_MOTION:
                    mIcon.setImageResource(R.drawable.human_handsup);
                    break;
                case State.ROLE_SENSOR_ALARM:
                    mIcon.setImageResource(R.drawable.alarm_light);
                    break;
                case State.ROLE_SENSOR_ALARM_FIRE:
                    mIcon.setImageResource(R.drawable.fire);
                    break;
                case State.ROLE_SENSOR_ALARM_SECURE:
                    mIcon.setImageResource(R.drawable.security_close);
                    break;
                case State.ROLE_SENSOR_ALARM_FLOOD:
                    mIcon.setImageResource(R.drawable.water);
                    break;
                case State.ROLE_SENSOR_ALARM_POWER:
                    mIcon.setImageResource(R.drawable.flash);
                    break;
                case State.ROLE_SENSOR_LOCK:
                    mIcon.setImageResource(R.drawable.lock);
                    break;
                case State.ROLE_SENSOR_LIGHT:
                    mIcon.setImageResource(R.drawable.lightbulb);
                    break;
                case State.ROLE_SENSOR_RAIN:
                    mIcon.setImageResource(R.drawable.weather_rainy);
                    break;
            }
        }
    }
}