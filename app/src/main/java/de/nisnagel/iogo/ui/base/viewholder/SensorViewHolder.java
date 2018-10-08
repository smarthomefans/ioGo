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

import android.view.View;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class SensorViewHolder extends BaseViewHolder {
    @BindView(R.id.valueText)
    TextView mValue;

    public SensorViewHolder(View itemView, EnumViewModel viewModel) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mViewModel = viewModel;
    }

    @Override
    public void bindState(State state) {
        super.bindState(state);
        mTitle.setText(state.getName());
        mSubtitle.setText(getSubtitle(state));
        mValue.setVisibility(View.VISIBLE);
        setValue(state.getRole(), state.getVal(), state.getStates());
        setImageRessource(state.getRole());
    }

    private void setValue(String role, String val, Map<String, String> states) {
        if (states != null) {
            mValue.setText(states.get(val));
        } else if (role != null) {
            switch (role) {
                case Constants.ROLE_SENSOR_DOOR:
                    mValue.setText(("true".equals(val)) ? context.getString(R.string.main_value_open) : context.getString(R.string.main_value_closed));
                    break;
                case Constants.ROLE_SENSOR_WINDOW:
                    mValue.setText(("true".equals(val)) ? context.getString(R.string.main_value_open) : context.getString(R.string.main_value_closed));
                    break;
                default:
                    mValue.setText(val);
                    break;
            }
        } else {
            mValue.setText(val);
        }
    }

}