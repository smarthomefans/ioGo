package de.nisnagel.iogo.ui.base.viewholder;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class LevelViewHolder extends BaseViewHolder {
    @BindView(R.id.valueNumber)
    TextView mValue;
    @BindView(R.id.valueSlider)
    SeekBar mSlider;

    public LevelViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

    public void bindState(State state) {
        super.bindState(state);
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        mValue.setVisibility(View.VISIBLE);
        String value = state.getVal();
        if (state.getUnit() != null) {
            value += state.getUnit();
        }
        mValue.setText(value);
        setImageRessource(state.getRole());

        if (state.getWrite()) {
            if (state.getMax() != null && state.getVal() != null) {
                mSlider.setVisibility(View.VISIBLE);
                int max = state.getMax().intValue();
                int min;
                if (state.getMin() != null) {
                    min = state.getMin().intValue();
                } else {
                    min = 0;
                }
                int val = (Float.valueOf(state.getVal())).intValue();
                mSlider.setMax(max - min);
                mSlider.setProgress(val - min);
                mSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mViewModel.changeState(state.getId(), String.valueOf(seekBar.getProgress() + min));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        String value = String.valueOf(progress + min);
                        if (state.getUnit() != null) {
                            value += state.getUnit();
                        }
                        mValue.setText(value);
                    }
                });
            } else {
                mValue.setClickable(true);
                mValue.setOnClickListener(v -> {

                    LayoutInflater li = LayoutInflater.from(v.getContext());
                    View promptsView = li.inflate(R.layout.prompts_number, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    (dialog, id) -> {
                                        mSubtitle.setText(R.string.syncing_data);
                                        mViewModel.changeState(state.getId(), userInput.getText().toString());
                                    })
                            .setNegativeButton("Cancel",
                                    (dialog, id) -> dialog.cancel());

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                });
            }
        }
    }

}