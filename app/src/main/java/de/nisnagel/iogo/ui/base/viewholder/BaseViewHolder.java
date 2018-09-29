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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.service.util.ImageUtils;
import de.nisnagel.iogo.ui.main.EnumViewModel;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.message_title)
    TextView mTitle;
    @BindView(R.id.message_subtitle)
    TextView mSubtitle;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.sync)
    ProgressBar mSync;

    Context context;
    EnumViewModel mViewModel;

    private SharedPreferences sharedPreferences;

    BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    void bindState(State state) {
        if (state.isSync()) {
            mSync.setVisibility(View.GONE);
            mIcon.setVisibility(View.VISIBLE);
        } else {
            mSync.setVisibility(View.VISIBLE);
            mIcon.setVisibility(View.GONE);
        }
    }

    String getSubtitle(State state) {
        if (state.isSync()) {
            String state_subtitle = sharedPreferences.getString(context.getString(R.string.pref_layout_state_subtitle), "none");
            if (state_subtitle.equals("role")) {
                return state.getRole();
            }
            if (state_subtitle.equals("ts")) {
                Calendar cal = Calendar.getInstance(Locale.GERMAN);
                cal.setTimeInMillis(state.getTs());
                String date;
                date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
                return date;
            }
            if (state_subtitle.equals("lc")) {
                Calendar cal = Calendar.getInstance(Locale.GERMAN);
                cal.setTimeInMillis(state.getLc());
                String date;
                date = DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString();
                return date;
            }

        } else {
            return context.getResources().getText(R.string.syncing_data).toString();
        }
        return "";
    }

    void setImageRessource(String role) {
        if (role != null) {
            int drawable = ImageUtils.getRoleImage(role);
            mIcon.setImageResource(drawable);
        }
    }
}
