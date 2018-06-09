package com.example.nagel.io1.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nagel.io1.R;
import com.example.nagel.io1.data.model.State;
import com.example.nagel.io1.service.DataBus;

import java.util.List;

public class BaseDetailAdapter
        extends RecyclerView.Adapter {

    private final List<State> mValues;

    private final int C_STRING_SPINNER = 5;
    private final int C_STRING = 1;
    private final int C_NUMBER = 2;
    private final int C_BOOLEAN_SWITCH = 3;
    private final int C_BOOLEAN_BUTTON = 4;

    public BaseDetailAdapter(List<State> stateList) {
        mValues = stateList;
        DataBus.getBus().register(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case C_BOOLEAN_SWITCH:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_boolean_switch, parent, false);
                return new BooleanTypeViewHolder(view);
            case C_BOOLEAN_BUTTON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_boolean_button, parent, false);
                return new BooleanButtonViewHolder(view);
            case C_STRING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_string, parent, false);
                return new StringTypeViewHolder(view);
            case C_NUMBER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_number, parent, false);
                return new NumberTypeViewHolder(view);
            case C_STRING_SPINNER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_string_spinner, parent, false);
                return new StringSpinnerViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        State object = mValues.get(position);
        if (object != null && object.getType() != null) {
            switch (object.getType()) {
                case State.TYPE_BOOLEAN:
                    if(State.ROLE_BUTTON.equals(object.getRole())){
                        return C_BOOLEAN_BUTTON;
                    }else{
                        return C_BOOLEAN_SWITCH;
                    }

                case State.TYPE_STRING:
                    if(object.getStates() != null && object.getStates().size() > 0){
                        return C_STRING_SPINNER;
                    }else {
                        return C_STRING;
                    }
                case State.TYPE_NUMBER:
                    return C_NUMBER;
                default:
                    return C_STRING;
            }
        }else{
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case C_BOOLEAN_SWITCH:
                ((BooleanTypeViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_BOOLEAN_BUTTON:
                ((BooleanButtonViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_NUMBER:
                ((NumberTypeViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_STRING:
                ((StringTypeViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_STRING_SPINNER:
                ((StringSpinnerViewHolder) holder).bindState(mValues.get(position));
                break;
        }

    }

    @Override
    public int getItemCount() {
        if(mValues == null){
            return 0;
        }
        return mValues.size();
    }


}