package de.nisio.iobroker.ui.base.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.service.DataBus;
import de.nisio.iobroker.service.Events;

public class ButtonViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)
    ImageButton mValue;
    @BindView(R.id.icon)
    ImageView mIcon;

    public ButtonViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(state.getRole());
        mValue.setClickable(state.getWrite());
        mValue.setOnClickListener(new CompoundButton.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          mSubtitle.setText("syncing data...");
                                          DataBus.getBus().post(new Events.SetState(state.getId(), "true"));
                                      }
                                  }
        );
        setImageRessource(state.getRole());
    }

    private void setImageRessource(String role){
        switch (role){
            case State.ROLE_BUTTON:
                mIcon.setImageResource(R.drawable.checkbox_blank_circle);
                break;
            case State.ROLE_BUTTON_START:
                mIcon.setImageResource(R.drawable.play);
                break;
            case State.ROLE_BUTTON_STOP:
                mIcon.setImageResource(R.drawable.stop);
                break;
        }
    }
}