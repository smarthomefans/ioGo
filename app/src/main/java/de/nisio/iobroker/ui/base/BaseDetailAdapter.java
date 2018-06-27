package de.nisio.iobroker.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.service.DataBus;
import de.nisio.iobroker.ui.base.viewholder.ButtonViewHolder;
import de.nisio.iobroker.ui.base.viewholder.IndicatorViewHolder;
import de.nisio.iobroker.ui.base.viewholder.LevelViewHolder;
import de.nisio.iobroker.ui.base.viewholder.CommonViewHolder;
import de.nisio.iobroker.ui.base.viewholder.SensorViewHolder;
import de.nisio.iobroker.ui.base.viewholder.SwitchViewHolder;
import de.nisio.iobroker.ui.base.viewholder.ValueViewHolder;


public class BaseDetailAdapter
        extends RecyclerView.Adapter {

    private final List<State> mValues;


    private final int C_SENSOR = 1;         //boolean readonly
    private final int C_BUTTON = 2;         //boolean writeonly
    private final int C_VALUE = 3;          //numbers readonly
    private final int C_INDICATOR = 4;      //boolean readonly
    private final int C_LEVEL = 5;          //numbers read-write
    private final int C_SWITCH = 6;         //boolean read-write
    private final int C_COMMON = 99;        //string

    public BaseDetailAdapter(List<State> stateList) {
        mValues = stateList;
        DataBus.getBus().register(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case C_SENSOR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_sensor, parent, false);
                return new SensorViewHolder(view);
            case C_BUTTON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_button, parent, false);
                return new ButtonViewHolder(view);
            case C_VALUE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_number, parent, false);
                return new ValueViewHolder(view);
            case C_INDICATOR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_indicator, parent, false);
                return new IndicatorViewHolder(view);
            case C_LEVEL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_number, parent, false);
                return new LevelViewHolder(view);
            case C_SWITCH:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_switch, parent, false);
                return new SwitchViewHolder(view);
            case C_COMMON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_common, parent, false);
                return new CommonViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_common, parent, false);
                return new CommonViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        State object = mValues.get(position);
        String role = null;
        if (object != null && object.getRole() != null) {
            role = object.getRole();
        }

        if(role != null){
            if (role.startsWith("sensor")) {
                return C_SENSOR;
            }
            if (role.startsWith("button")) {
                return C_BUTTON;
            }
            if (role.startsWith("value")) {
                return C_VALUE;
            }
            if (role.startsWith("indicator")) {
                return C_INDICATOR;
            }
            if (role.startsWith("level")) {
                return C_LEVEL;
            }
            if (role.startsWith("switch")) {
                return C_SWITCH;
            }
            if (role.equals("state") || role.startsWith("text") || role.startsWith("html") || role.startsWith("json")) {
                return C_COMMON;
            }
        }

        return C_COMMON;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case C_SENSOR:
                ((SensorViewHolder) holder).bindState(mValues.get(position));
                break;
            case C_BUTTON:
                ((ButtonViewHolder) holder).bindState(mValues.get(position));
                break;
            case C_VALUE:
                ((ValueViewHolder) holder).bindState(mValues.get(position));
                break;
            case C_INDICATOR:
                ((IndicatorViewHolder) holder).bindState(mValues.get(position));
                break;
            case C_LEVEL:
                ((LevelViewHolder) holder).bindState(mValues.get(position));
                break;
            case C_SWITCH:
                ((SwitchViewHolder) holder).bindState(mValues.get(position));
                break;
            case C_COMMON:
                ((CommonViewHolder) holder).bindState(mValues.get(position));
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