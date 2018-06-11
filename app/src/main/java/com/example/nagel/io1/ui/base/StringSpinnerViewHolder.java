package com.example.nagel.io1.ui.base;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nagel.io1.R;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StringSpinnerViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener{
    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)  TextView mSubtitle;
    @BindView(R.id.value)  Spinner mValue;

    ArrayList<StateItem> stateItems;
    StateItem stateItem;
    State state;
    Boolean init = false;

    public StringSpinnerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        DataBus.getBus().register(this);
        stateItems = new ArrayList<>();
    }

    public void bindState(State state) {
        this.state = state;
        mTitle.setText(state.getName());
        mSubtitle.setText("");

        for (Map.Entry<String, String> entry : state.getStates().entrySet())
        {
            stateItems.add(new StateItem(entry.getKey(), entry.getValue()));
            if(entry.getKey().equals(state.getVal())){
                stateItem = new StateItem(entry.getKey(), entry.getValue());
            }
        }

        ArrayAdapter<StateItem> arrayAdapter = new ArrayAdapter<StateItem>(this.itemView.getContext(), android.R.layout.simple_spinner_item, stateItems);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mValue.setAdapter(arrayAdapter);
        mValue.setSelected(false);
        mValue.setPrompt("Select an item:");
        if (stateItem != null) {
            mValue.setSelection(arrayAdapter.getPosition(stateItem));
        }
        mValue.setOnItemSelectedListener(this);
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