package de.nisio.iobroker.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.service.DataBus;
import de.nisio.iobroker.service.Events;

public class BooleanButtonViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)
    ImageButton mValue;

    public BooleanButtonViewHolder(View itemView) {
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
    }
}