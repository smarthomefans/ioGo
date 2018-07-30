package de.nisnagel.iogo.ui.base.viewholder;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.Events;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class LevelViewHolder extends BaseViewHolder {
    @BindView(R.id.valueNumber)
    TextView mValue;

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
            mValue.setClickable(true);
            mValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LayoutInflater li = LayoutInflater.from(v.getContext());
                    View promptsView = li.inflate(R.layout.prompts_number, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mSubtitle.setText(R.string.syncing_data);
                                            mViewModel.changeState(state.getId(), userInput.getText().toString());
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            });
        }
    }

    private void setImageRessource(String role) {
        if(role != null) {
            switch (role) {
                case Constants.ROLE_LEVEL_DIMMER:
                    mIcon.setImageResource(R.drawable.lightbulb);
                    break;
                case Constants.ROLE_LEVEL_BLIND:
                    mIcon.setImageResource(R.drawable.blinds);
                    break;
                case Constants.ROLE_LEVEL_TEMPERATURE:
                    mIcon.setImageResource(R.drawable.thermometer);
                    break;
                case Constants.ROLE_VALVE:
                    mIcon.setImageResource(R.drawable.ship_wheel);
                    break;
                case Constants.ROLE_LEVEL_COLOR_RED:
                    mIcon.setImageResource(R.drawable.palette);
                    break;
                case Constants.ROLE_LEVEL_COLOR_GREEN:
                    mIcon.setImageResource(R.drawable.palette);
                    break;
                case Constants.ROLE_LEVEL_COLOR_BLUE:
                    mIcon.setImageResource(R.drawable.palette);
                    break;
                case Constants.ROLE_LEVEL_COLOR_WHITE:
                    mIcon.setImageResource(R.drawable.palette);
                    break;
                case Constants.ROLE_LEVEL_COLOR_HUE:
                    mIcon.setImageResource(R.drawable.palette);
                    break;
                case Constants.ROLE_LEVEL_COLOR_SATURATION:
                    mIcon.setImageResource(R.drawable.palette);
                    break;
                case Constants.ROLE_LEVEL_COLOR_RGB:
                    mIcon.setImageResource(R.drawable.palette);
                    break;
                case Constants.ROLE_LEVEL_COLOR_LUMINANCE:
                    mIcon.setImageResource(R.drawable.palette);
                    break;
                case Constants.ROLE_LEVEL_COLOR_TEMPERATURE:
                    mIcon.setImageResource(R.drawable.palette);
                    break;
                case Constants.ROLE_LEVEL_VOLUME:
                    mIcon.setImageResource(R.drawable.volume_high);
                    break;
                case Constants.ROLE_LEVEL_CURTAIN:
                    mIcon.setImageResource(R.drawable.blinds);
                    break;
                case Constants.ROLE_LEVEL_TILT:
                    mIcon.setImageResource(R.drawable.blinds);
                    break;
            }
        }
    }
}