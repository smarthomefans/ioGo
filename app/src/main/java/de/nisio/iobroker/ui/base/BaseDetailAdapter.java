package de.nisio.iobroker.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.nisio.iobroker.R;
import de.nisio.iobroker.data.model.State;
import de.nisio.iobroker.service.DataBus;
import de.nisio.iobroker.ui.base.viewholder.DefaultBooleanViewHolder;
import de.nisio.iobroker.ui.base.viewholder.ButtonViewHolder;
import de.nisio.iobroker.ui.base.viewholder.DefaultNumberViewHolder;
import de.nisio.iobroker.ui.base.viewholder.DefaultStringViewHolder;
import de.nisio.iobroker.ui.base.viewholder.SensorDoorViewHolder;
import de.nisio.iobroker.ui.base.viewholder.SensorMotionViewHolder;
import de.nisio.iobroker.ui.base.viewholder.SensorWindowViewHolder;
import de.nisio.iobroker.ui.base.viewholder.StringSpinnerViewHolder;
import de.nisio.iobroker.ui.base.viewholder.SwitchViewHolder;
import de.nisio.iobroker.ui.base.viewholder.ValueTemperatureViewHolder;
import de.nisio.iobroker.ui.base.viewholder.ValueViewHolder;


public class BaseDetailAdapter
        extends RecyclerView.Adapter {

    private final List<State> mValues;

    private final int C_STRING = 1;
    private final int C_NUMBER = 2;
    private final int C_BOOLEAN_SWITCH = 3;
    private final int C_BOOLEAN_BUTTON = 4;
    private final int C_STRING_SPINNER = 5;
    private final int C_SENSOR_DOOR = 6;
    private final int C_SENSOR_WINDOW = 7;
    private final int C_SENSOR_MOTION = 8;
    private final int C_SENSOR = 9;
    private final int C_VALUE = 10;
    private final int C_VALUE_TEMPERATURE = 11;

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
                return new SwitchViewHolder(view);
            case C_BOOLEAN_BUTTON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_boolean_button, parent, false);
                return new ButtonViewHolder(view);
            case C_SENSOR_DOOR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_string, parent, false);
                return new SensorDoorViewHolder(view);
            case C_SENSOR_WINDOW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_string, parent, false);
                return new SensorWindowViewHolder(view);
            case C_SENSOR_MOTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_string, parent, false);
                return new SensorMotionViewHolder(view);
            case C_SENSOR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_string, parent, false);
                return new DefaultBooleanViewHolder(view);
            case C_STRING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_string, parent, false);
                return new DefaultStringViewHolder(view);
            case C_NUMBER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_number, parent, false);
                return new DefaultNumberViewHolder(view);
            case C_VALUE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_number, parent, false);
                return new ValueViewHolder(view);
            case C_VALUE_TEMPERATURE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_number, parent, false);
                return new ValueTemperatureViewHolder(view);
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
                    switch (object.getRole()){
                        case State.ROLE_BUTTON:
                            return C_BOOLEAN_BUTTON;
                        case State.ROLE_SENSOR_DOOR:
                            return C_SENSOR_DOOR;
                        case State.ROLE_SENSOR_WINDOW:
                            return C_SENSOR_WINDOW;
                        case State.ROLE_SENSOR_MOTION:
                            return C_SENSOR_MOTION;
                        case State.ROLE_SENSOR:
                            return C_SENSOR;
                        default:
                            return C_BOOLEAN_SWITCH;
                    }

                case State.TYPE_STRING:
                    if(object.getStates() != null && object.getStates().size() > 0){
                        return C_STRING_SPINNER;
                    }else {
                        return C_STRING;
                    }
                case State.TYPE_NUMBER:
                    switch (object.getRole()){
                        case State.ROLE_VALUE_TEMPERATURE:
                            return C_VALUE_TEMPERATURE;
                        case State.ROLE_VALUE:
                            return C_VALUE;
                        default:
                            return C_NUMBER;
                    }
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
                ((SwitchViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_BOOLEAN_BUTTON:
                ((ButtonViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_SENSOR_DOOR:
                ((SensorDoorViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_SENSOR_WINDOW:
                ((SensorWindowViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_SENSOR_MOTION:
                ((SensorMotionViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_SENSOR:
                ((DefaultBooleanViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_NUMBER:
                ((DefaultNumberViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_VALUE:
                ((ValueViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_VALUE_TEMPERATURE:
                ((ValueTemperatureViewHolder) holder).bindState(mValues.get(position));
                break;

            case C_STRING:
                ((DefaultStringViewHolder) holder).bindState(mValues.get(position));
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