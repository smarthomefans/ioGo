package de.nisnagel.iogo.ui.base.viewholder;

import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class ButtonViewHolder extends BaseViewHolder {
    @BindView(R.id.valueButton)
    Button mValue;

    public ButtonViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

    public void bindState(State state) {
        super.bindState(state);
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        mValue.setVisibility(View.VISIBLE);
        mValue.setClickable(state.getWrite());
        mValue.setOnClickListener(v -> {
            mSubtitle.setText(R.string.syncing_data);
            mViewModel.changeState(state.getId(), "true");
        }
        );
        setImageRessource(state.getRole());
    }

}