/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import timber.log.Timber;

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
            int val;
            boolean isNumber = false;
            try{
               val = (Float.valueOf(state.getVal())).intValue();
               isNumber = true;
            }catch(Throwable t){
               Timber.w(t.getMessage());
               val = 0; // dummy
            }
            if (state.getMax() != null && state.getVal() != null && isNumber) {
                mSlider.setVisibility(View.VISIBLE);
                int max = state.getMax().intValue();
                int min;
                if (state.getMin() != null) {
                    min = state.getMin().intValue();
                } else {
                    min = 0;
                }

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
                                        mSubtitle.setText(R.string.main_syncing_data);
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