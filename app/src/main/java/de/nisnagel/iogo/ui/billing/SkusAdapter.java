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

package de.nisnagel.iogo.ui.billing;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.nisnagel.iogo.R;

public class SkusAdapter extends RecyclerView.Adapter<RowViewHolder>
        implements RowViewHolder.OnButtonClickListener {
    private List<SkuRowData> mListData;
    private BillingProvider mBillingProvider;

    public SkusAdapter(BillingProvider billingProvider) {
        mBillingProvider = billingProvider;
    }

    void updateData(List<SkuRowData> data) {
        mListData = data;
        notifyDataSetChanged();
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sku_details_row, parent, false);
        return new RowViewHolder(item, this);
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        SkuRowData data = getData(position);
        if (data != null) {
            holder.title.setText(data.getTitle());
            holder.description.setText(data.getDescription());
            holder.button.setEnabled(true);
            holder.button.setText(holder.itemView.getContext().getString(R.string.billing_buy, data.getPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public void onButtonClicked(int position) {
        SkuRowData data = getData(position);
        mBillingProvider.getBillingManager().startPurchaseFlow(data.getSku(),
                data.getBillingType(), "");

    }

    private SkuRowData getData(int position) {
        return mListData == null ? null : mListData.get(position);
    }
}
