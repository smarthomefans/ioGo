package de.nisnagel.iogo.ui.base.viewholder;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.Events;

public class ButtonViewHolder extends BaseViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)
    ImageButton mValue;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.letter)
    TextView mLetter;

    public ButtonViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        mValue.setClickable(state.getWrite());
        mValue.setOnClickListener(new CompoundButton.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          mSubtitle.setText(R.string.syncing_data);
                                          DataBus.getBus().post(new Events.SetState(state.getId(), "true"));
                                      }
                                  }
        );
        setImageRessource(state.getRole());
    }

    private void setImageRessource(String role){
        if(role != null) {
            switch (role) {
                case Constants.ROLE_BUTTON:
                    mIcon.setImageResource(R.drawable.checkbox_blank_circle);
                    break;
                case Constants.ROLE_BUTTON_START:
                    mIcon.setImageResource(R.drawable.play);
                    break;
                case Constants.ROLE_BUTTON_STOP:
                    mIcon.setImageResource(R.drawable.stop);
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