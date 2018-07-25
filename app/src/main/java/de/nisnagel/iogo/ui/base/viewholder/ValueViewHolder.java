package de.nisnagel.iogo.ui.base.viewholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;

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

    public ValueViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        setValue(state.getRole(), state.getUnit(), state.getVal());

        setImageRessource(state.getRole());
    }

    private void setValue(String role, String unit, String val) {
        String value = val;
        if (unit != null) {
            value += unit;
        } else {
            if (role != null) {
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