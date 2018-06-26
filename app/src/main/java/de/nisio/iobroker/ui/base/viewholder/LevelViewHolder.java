package de.nisio.iobroker.ui.base.viewholder;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.service.DataBus;
import de.nisio.iobroker.service.Events;

public class LevelViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)  TextView mValue;
    @BindView(R.id.icon)
    ImageView mIcon;

    public LevelViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(state.getRole());
        String value = state.getVal();
        if(state.getUnit() != null){
            value += state.getUnit();
        }
        mValue.setText(value);
        if(state.getWrite()) {
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
                                            // get user input and set it to result
                                            // edit text
                                            mSubtitle.setText("syncing data...");
                                            DataBus.getBus().post(new Events.SetState(state.getId(), userInput.getText().toString()));
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
}