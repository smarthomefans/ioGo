package de.nisnagel.iogo.ui.base.viewholder;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class SwitchViewHolder extends BaseViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)
    TextView mSubtitle;
    @BindView(R.id.value)
    Switch mValue;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.letter)
    TextView mLetter;

    public SwitchViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        mValue.setChecked("true".equals(state.getVal()));
        if (state.getWrite()) {
            mValue.setClickable(true);
            mValue.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Switch btn = (Switch) v;
                    mSubtitle.setText(R.string.syncing_data);
                    mViewModel.changeState(state.getId(), (btn.isChecked()) ? "true" : "false");
                }
            });

        }
        setImageRessource(state.getRole());
    }

    private void setImageRessource(String role) {
        if (role != null) {
            switch (role) {
                case Constants.ROLE_SWITCH:
                    mIcon.setImageResource(R.drawable.toggle_switch);
                    break;
                case Constants.ROLE_SWITCH_LIGHT:
                    mIcon.setImageResource(R.drawable.lightbulb);
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