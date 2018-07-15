package de.nisnagel.iogo.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.ui.base.viewholder.ButtonViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.IndicatorViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.LevelViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.CommonViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.SensorViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.SwitchViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.ValueViewHolder;


public class StateListAdapter
        extends RecyclerView.Adapter {

    private EnumViewModel mViewModel;

    private Context context;

    private final List<State> mValues;
    protected final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            State item = (State) view.getTag();
            if(item != null){
                item.setFavorite((item.getFavorite()) == "true" ? "false" : "true");
                mViewModel.saveState(item);

                return true;
            }

            return false;
        }
    };

    private final int C_SENSOR = 1;         //boolean readonly
    private final int C_BUTTON = 2;         //boolean writeonly
    private final int C_VALUE = 3;          //numbers readonly
    private final int C_INDICATOR = 4;      //boolean readonly
    private final int C_LEVEL = 5;          //numbers read-write
    private final int C_SWITCH = 6;         //boolean read-write
    private final int C_COMMON = 99;        //string

    public StateListAdapter(List<State> stateList, EnumViewModel mViewModel, Context context) {
        this.mValues = stateList;
        this.mViewModel = mViewModel;
        this.context = context;
        DataBus.getBus().register(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case C_SENSOR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_sensor, parent, false);
                return new SensorViewHolder(view, context);
            case C_BUTTON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_button, parent, false);
                return new ButtonViewHolder(view, context);
            case C_VALUE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_number, parent, false);
                return new ValueViewHolder(view, context);
            case C_INDICATOR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_indicator, parent, false);
                return new IndicatorViewHolder(view, context);
            case C_LEVEL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_number, parent, false);
                return new LevelViewHolder(view, context);
            case C_SWITCH:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_switch, parent, false);
                return new SwitchViewHolder(view, context);
            case C_COMMON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_common, parent, false);
                return new CommonViewHolder(view, context);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state_common, parent, false);
                return new CommonViewHolder(view, context);
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

        holder.itemView.setOnLongClickListener(mOnLongClickListener);
        holder.itemView.setTag(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        if(mValues == null){
            return 0;
        }
        return mValues.size();
    }


}