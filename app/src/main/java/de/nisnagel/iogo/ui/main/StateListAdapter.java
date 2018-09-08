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

package de.nisnagel.iogo.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.ui.base.viewholder.ButtonViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.CommonViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.IndicatorViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.LevelViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.SensorViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.SwitchViewHolder;
import de.nisnagel.iogo.ui.base.viewholder.ValueViewHolder;
import de.nisnagel.iogo.ui.detail.StateActivity;
import de.nisnagel.iogo.ui.history.HistoryActivity;

class StateListAdapter
        extends RecyclerView.Adapter {

    private EnumViewModel mViewModel;
    private List<State> mValues;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            State item = (State) view.getTag();

            Context context = view.getContext();

            if (item.hasHistory() && "number".equals(item.getType())) {
                Intent intent = new Intent(context, HistoryActivity.class);
                intent.putExtra(Constants.ARG_STATE_ID, item.getId());
                context.startActivity(intent);
            } else if (item.getStates() != null && item.getWrite()) {
                Intent intent = new Intent(context, StateActivity.class);
                intent.putExtra(Constants.ARG_STATE_ID, item.getId());
                intent.putExtra(Constants.ARG_ENUM_ID, mViewModel.getEnumId());
                context.startActivity(intent);
            }
        }
    };

    private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            State item = (State) view.getTag();
            if (item != null) {
                if ("true".equals(item.getFavorite())) {
                    item.setFavorite("false");
                    Toast.makeText(view.getContext(), "unstarred", Toast.LENGTH_SHORT).show();
                } else {
                    item.setFavorite("true");
                    Toast.makeText(view.getContext(), "starred", Toast.LENGTH_SHORT).show();
                }
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

    StateListAdapter(List<State> stateList, EnumViewModel mViewModel, Context context) {
        this.mValues = stateList;
        this.mViewModel = mViewModel;
        DataBus.getBus().register(this);
    }

    public void clearList() {
        this.mValues.clear();
    }

    public void addAll(List<State> stateList) {
        this.mValues = stateList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case C_SENSOR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state, parent, false);
                return new SensorViewHolder(view, mViewModel);
            case C_BUTTON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state, parent, false);
                return new ButtonViewHolder(view, mViewModel);
            case C_VALUE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state, parent, false);
                return new ValueViewHolder(view, mViewModel);
            case C_INDICATOR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state, parent, false);
                return new IndicatorViewHolder(view, mViewModel);
            case C_LEVEL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state, parent, false);
                return new LevelViewHolder(view, mViewModel);
            case C_SWITCH:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state, parent, false);
                return new SwitchViewHolder(view, mViewModel);
            case C_COMMON:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state, parent, false);
                return new CommonViewHolder(view, mViewModel);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_state, parent, false);
                return new CommonViewHolder(view, mViewModel);
        }
    }

    @Override
    public int getItemViewType(int position) {
        State object = mValues.get(position);
        String role = null;
        if (object != null && object.getRole() != null) {
            role = object.getRole();
        }

        if (role != null) {
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
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnLongClickListener(mOnLongClickListener);
        holder.itemView.setTag(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        if (mValues == null) {
            return 0;
        }
        return mValues.size();
    }


}