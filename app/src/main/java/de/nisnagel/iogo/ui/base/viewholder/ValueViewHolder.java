package de.nisnagel.iogo.ui.base.viewholder;

import android.view.View;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class ValueViewHolder extends BaseViewHolder {
    @BindView(R.id.valueNumber)
    TextView mValue;

    public ValueViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

    public void bindState(State state) {
        super.bindState(state);
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        mValue.setVisibility(View.VISIBLE);
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
                    switch (val) {
                        case "0":
                            value = context.getString(R.string.value_closed);
                            break;
                        case "1":
                            value = context.getString(R.string.value_tilted);
                            break;
                        case "2":
                            value = context.getString(R.string.value_open);
                            break;
                    }
                    break;
            }
        }

        if (unit != null) {
            value += unit;
        }
        mValue.setText(value);
    }

}