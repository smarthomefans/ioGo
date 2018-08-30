package de.nisnagel.iogo.ui.base.viewholder;

import android.view.View;
import android.widget.Switch;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class SwitchViewHolder extends BaseViewHolder {
    @BindView(R.id.valueSwitch)
    Switch mValue;

    public SwitchViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

    public void bindState(State state) {
        super.bindState(state);
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        mValue.setVisibility(View.VISIBLE);
        mValue.setChecked("true".equals(state.getVal()));
        if (state.getWrite()) {
            mValue.setClickable(true);
            mValue.setOnClickListener(v -> {
                final Switch btn = (Switch) v;
                mSubtitle.setText(R.string.syncing_data);
                mViewModel.changeState(state.getId(), (btn.isChecked()) ? "true" : "false");
            });

        }
        setImageRessource(state.getRole());
    }

}