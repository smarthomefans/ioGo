package de.nisnagel.iogo.ui.base.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class SensorViewHolder extends BaseViewHolder {
    @BindView(R.id.valueText)
    TextView mValue;

    public SensorViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

    @Override
    public void bindState(State state) {
        super.bindState(state);
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        mValue.setVisibility(View.VISIBLE);
        setValue(state.getRole(), state.getVal(), state.getStates());
        setImageRessource(state.getRole());
    }

    private void setValue(String role, String val, Map<String, String> states) {
        if (states != null) {
            mValue.setText(states.get(val));
        } else if (role != null) {
            switch (role) {
                case Constants.ROLE_SENSOR_DOOR:
                    mValue.setText(("true".equals(val)) ? context.getString(R.string.value_open) : context.getString(R.string.value_closed));
                    break;
                case Constants.ROLE_SENSOR_WINDOW:
                    mValue.setText(("true".equals(val)) ? context.getString(R.string.value_open) : context.getString(R.string.value_closed));
                    break;
                default:
                    mValue.setText(val);
                    break;
            }
        } else {
            mValue.setText(val);
        }
    }

    private void setImageRessource(String role) {
        if (role != null) {
            switch (role) {
                case Constants.ROLE_SENSOR_DOOR:
                    mIcon.setImageResource(R.drawable.door);
                    break;
                case Constants.ROLE_SENSOR_WINDOW:
                    mIcon.setImageResource(R.drawable.window_closed);
                    break;
                case Constants.ROLE_SENSOR_MOTION:
                    mIcon.setImageResource(R.drawable.run_fast);
                    break;
                case Constants.ROLE_SENSOR_ALARM:
                    mIcon.setImageResource(R.drawable.alarm_light);
                    break;
                case Constants.ROLE_SENSOR_ALARM_FIRE:
                    mIcon.setImageResource(R.drawable.fire);
                    break;
                case Constants.ROLE_SENSOR_ALARM_SECURE:
                    mIcon.setImageResource(R.drawable.security_close);
                    break;
                case Constants.ROLE_SENSOR_ALARM_FLOOD:
                    mIcon.setImageResource(R.drawable.water);
                    break;
                case Constants.ROLE_SENSOR_ALARM_POWER:
                    mIcon.setImageResource(R.drawable.flash);
                    break;
                case Constants.ROLE_SENSOR_LOCK:
                    mIcon.setImageResource(R.drawable.lock);
                    break;
                case Constants.ROLE_SENSOR_LIGHT:
                    mIcon.setImageResource(R.drawable.lightbulb);
                    break;
                case Constants.ROLE_SENSOR_RAIN:
                    mIcon.setImageResource(R.drawable.weather_rainy);
                    break;
            }
        }
    }
}