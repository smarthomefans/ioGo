package de.nisnagel.iogo.ui.base.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class IndicatorViewHolder extends BaseViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)
    TextView mValue;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.letter)
    TextView mLetter;

    public IndicatorViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        mValue.setText(state.getVal());
        setImageRessource(state.getRole());
    }

    private void setImageRessource(String role){
        if(role != null) {
            switch (role) {
                case Constants.ROLE_INDICATOR_CONNECTED:
                    mIcon.setImageResource(R.drawable.lan_connect);
                    break;
                case Constants.ROLE_INDICATOR_LOWBAT:
                    mIcon.setImageResource(R.drawable.battery_10);
                    break;
                default:
                    mIcon.setVisibility(View.GONE);
                    mLetter.setVisibility(View.VISIBLE);
            }
        }else{
            mIcon.setVisibility(View.GONE);
            mLetter.setVisibility(View.VISIBLE);
        }
    }
}