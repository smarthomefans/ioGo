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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.Events;
import de.nisnagel.iogo.ui.base.StateItem;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class CommonViewHolder extends BaseViewHolder {
    @BindView(R.id.valueText)
    TextView mValue;

    private String selected = null;

    public CommonViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

    public void bindState(State state) {
        super.bindState(state);
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        mValue.setVisibility(View.VISIBLE);
        if (state.getStates() != null) {
            bindSpinner(state);
        } else {
            bindText(state);
        }

        setImageRessource(state.getRole());
    }

    private void bindSpinner(State state) {
        if (state.getStates() != null) {
            mValue.setText(state.getStates().get(state.getVal()));
        } else {
            mValue.setText(state.getVal());
        }
        if (state.getWrite()) {
            mValue.setClickable(true);
            mValue.setOnClickListener(v -> {
                LayoutInflater li = LayoutInflater.from(v.getContext());
                View promptsView = li.inflate(R.layout.prompts_spinner, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

                alertDialogBuilder.setView(promptsView);

                final Spinner userInput = promptsView
                        .findViewById(R.id.spinner);

                ArrayList<StateItem> stateItems = new ArrayList<>();
                StateItem stateItem = null;

                for (Map.Entry<String, String> entry : state.getStates().entrySet()) {
                    stateItems.add(new StateItem(entry.getKey(), entry.getValue()));
                    if (entry.getKey().equals(state.getVal())) {
                        stateItem = new StateItem(entry.getKey(), entry.getValue());
                    }
                }

                ArrayAdapter<StateItem> arrayAdapter = new ArrayAdapter<>(promptsView.getContext(), android.R.layout.simple_spinner_item, stateItems);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userInput.setAdapter(arrayAdapter);
                userInput.setSelected(false);
                if (stateItem != null) {
                    userInput.setSelection(arrayAdapter.getPosition(stateItem));
                }
                userInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        StateItem stateItem = (StateItem) parent.getSelectedItem();
                        selected = stateItem.getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog, id) -> {
                                    mSubtitle.setText(R.string.syncing_data);
                                    mViewModel.changeState(state.getId(), selected);
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

    private void bindText(State state) {
        if (state.getStates() != null) {
            mValue.setText(state.getStates().get(state.getVal()));
        } else {
            mValue.setText(state.getVal());
        }
        if (state.getWrite()) {
            mValue.setClickable(true);
            mValue.setOnClickListener(v -> {
                LayoutInflater li = LayoutInflater.from(v.getContext());
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog, id) -> {
                                    // get user input and set it to result
                                    // edit text
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