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
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.service.Constants;

public class EnumHomeListAdapter
        extends RecyclerView.Adapter<EnumHomeListAdapter.ViewHolder> {

    private List<Enum> mValues;

    private final View.OnClickListener mOnClickListener = view -> {
        Enum item = (Enum) view.getTag();

        Context context = view.getContext();
        Intent intent = new Intent(context, EnumActivity.class);
        intent.putExtra(Constants.ARG_ENUM_ID, item.getId());

        context.startActivity(intent);

    };


    public EnumHomeListAdapter(List<Enum> list, EnumViewModel mViewModel) {
        this.mValues = list;
    }

    public void clearList(){
        this.mValues.clear();
    }

    public void addAll(List<Enum> list) {
        for(Iterator iter = list.iterator();
            iter.hasNext();) {
            Enum item = (Enum) iter.next();
            if(item.isHidden()){
                iter.remove();
            }
        }
        this.mValues = list;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_enum_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bindEnum(mValues.get(position));
        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)  TextView mTitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        void bindEnum(Enum anEnum) {
            mTitle.setText(anEnum.getName());
        }
    }

}