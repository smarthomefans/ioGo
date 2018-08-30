package de.nisnagel.iogo.ui.base.viewholder;

import android.view.View;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class IndicatorViewHolder extends BaseViewHolder {
    @BindView(R.id.valueText)
    TextView mValue;

    public IndicatorViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

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
        } else {
            mValue.setText(val);
        }
    }

}