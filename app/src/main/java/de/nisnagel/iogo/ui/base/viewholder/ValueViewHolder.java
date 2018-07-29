package de.nisnagel.iogo.ui.base.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class ValueViewHolder extends BaseViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)
    TextView mSubtitle;
    @BindView(R.id.value)
    TextView mValue;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.letter)
    TextView mLetter;

    public ValueViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        setValue(state.getRole(), state.getUnit(), state.getVal(), state.getStates());
        setImageRessource(state.getRole());
    }

    private void setValue(String role, String unit, String val, Map<String, String> states) {
        String value = val;
        if (states != null) {
            value = states.get(val);
        } else if (role != null) {
            switch (role) {
                case Constants.ROLE_VALUE_WINDOW:
                    if ("0".equals(val)) {
                        value = "closed";
                    } else if ("1".equals(val)) {
                        value = "tilted";
                    } else if ("2".equals(val)) {
                        value = "open";
                    }
                    break;
            }
        }

        if (unit != null) {
            value += unit;
        }
        mValue.setText(value);
    }

    private void setImageRessource(String role) {
        if (role != null) {
            switch (role) {
                case Constants.ROLE_VALUE_TEMPERATURE:
                    mIcon.setImageResource(R.drawable.thermometer);
                    break;
                case Constants.ROLE_VALUE_HUMIDITY:
                    mIcon.setImageResource(R.drawable.water_percent);
                    break;
                case Constants.ROLE_VALUE_BRIGHTNESS:
                    mIcon.setImageResource(R.drawable.brightness_5);
                    break;
                case Constants.ROLE_VALUE_BATTERY:
                    mIcon.setImageResource(R.drawable.battery);
                    break;
                case Constants.ROLE_VALUE_BLIND:
                    mIcon.setImageResource(R.drawable.blinds);
                    break;
                default:
                    mIcon.setVisibility(View.GONE);
                    mLetter.setVisibility(View.VISIBLE);
            }
        } else {
            mIcon.setVisibility(View.GONE);
            mLetter.setVisibility(View.VISIBLE);
        }
    }
}