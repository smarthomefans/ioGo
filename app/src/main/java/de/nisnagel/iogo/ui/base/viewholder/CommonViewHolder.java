package de.nisnagel.iogo.ui.base.viewholder;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.Events;
import de.nisnagel.iogo.ui.base.StateItem;

public class CommonViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener{
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.text)  TextView mText;
    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.icon)
    ImageView mIcon;

    ArrayList<StateItem> stateItems;
    StateItem stateItem;
    State state;
    Boolean init = false;

    public CommonViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        DataBus.getBus().register(this);
        stateItems = new ArrayList<>();
    }

    public void bindState(State state) {
        mTitle.setText(state.getName());
        mSubtitle.setText(state.getRole());

        if(state.getStates() != null && state.getWrite()){
            bindSpinner(state);
        }else{
            bindText(state);
        }

        setImageRessource(state.getRole());
    }

    private void bindSpinner(State state){
        this.state = state;
        mSpinner.setVisibility(View.VISIBLE);
        for (Map.Entry<String, String> entry : state.getStates().entrySet())
        {
            stateItems.add(new StateItem(entry.getKey(), entry.getValue()));
            if(entry.getKey().equals(state.getVal())){
                stateItem = new StateItem(entry.getKey(), entry.getValue());
            }
        }

        ArrayAdapter<StateItem> arrayAdapter = new ArrayAdapter<StateItem>(this.itemView.getContext(), android.R.layout.simple_spinner_item, stateItems);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);
        mSpinner.setSelected(false);
        mSpinner.setPrompt("Select an item:");
        if (stateItem != null) {
            mSpinner.setSelection(arrayAdapter.getPosition(stateItem));
        }
        mSpinner.setOnItemSelectedListener(this);
    }

    private void bindText(State state){
        mText.setVisibility(View.VISIBLE);
        if(state.getStates() != null) {
            mText.setText(state.getStates().get(state.getVal()));
        }else{
            mText.setText(state.getVal());
        }
        if(state.getWrite()) {
            mText.setClickable(true);
            mText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LayoutInflater li = LayoutInflater.from(v.getContext());
                    View promptsView = li.inflate(R.layout.prompts, null);

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

    private void setImageRessource(String role){
        if(role != null) {
            switch (role) {
                case Constants.ROLE_TEXT:
                    mIcon.setImageResource(R.drawable.text);
                    break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(init) {
            StateItem stateItem = (StateItem) parent.getSelectedItem();
            mSubtitle.setText("syncing data...");
            DataBus.getBus().post(new Events.SetState(this.state.getId(), stateItem.getId()));
        }
        init = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}