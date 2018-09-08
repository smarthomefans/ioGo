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

package de.nisnagel.iogo.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.Constants;

class SettingsAdapter
        extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<SettingItem> mValues;

    private final View.OnClickListener mOnClickListener = view -> {
        SettingItem item = (SettingItem) view.getTag();

        Context context = view.getContext();

        Intent intent = new Intent(context, SettingsMainActivity.class);
        intent.putExtra(Constants.ARG_CLASS, item.intent);
        context.startActivity(intent);
    };

    SettingsAdapter(List<SettingItem> list) {
        this.mValues = list;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_setting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bindItem(mValues.get(position));
        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.summary)
        TextView mSummary;
        @BindView(R.id.icon)
        ImageView mIcon;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        void bindItem(SettingItem item) {
            mTitle.setText(item.title);
            mSummary.setText(item.summary);
            mIcon.setImageResource(item.iconRessource);
        }
    }

    public static class SettingItem {
        private int iconRessource;
        private String title;
        private String summary;
        private String intent;

        public SettingItem(int iconRessource, String title, String summary, String intent) {
            this.iconRessource = iconRessource;
            this.title = title;
            this.summary = summary;
            this.intent = intent;
        }
    }


}