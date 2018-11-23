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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import timber.log.Timber;

/**
 * Displays a screen with various in-app purchase and subscription options
 */
public class BillingFragment extends Fragment {

    @BindView(R.id.list)
    RecyclerView mRecyclerView;
    private SkusAdapter mAdapter;
    @BindView(R.id.screen_wait)
    View mLoadingView;

    @BindView(R.id.error_textview)
    TextView mErrorTextView;

    private BillingProvider mBillingProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_billing, container, false);
        ButterKnife.bind(this, root);

        setWaitScreen(true);

        mBillingProvider = (BillingProvider) getActivity();
        if (mRecyclerView != null) {
            mAdapter = new SkusAdapter(mBillingProvider);
            if (mRecyclerView.getAdapter() == null) {
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
            //check for the products user purchased before load..
            mBillingProvider.getBillingManager().checkPurchasedProducts(BillingClient.SkuType.SUBS);
            loadUiData();
        }

        return root;
    }

    /**
     * Enables or disables "please wait" screen.
     */
    private void setWaitScreen(boolean set) {
        mRecyclerView.setVisibility(set ? View.GONE : View.VISIBLE);
        mLoadingView.setVisibility(set ? View.VISIBLE : View.GONE);
    }

    /**
     * Executes query for SKU details at the background thread
     */
    private void loadUiData() {
        final List<SkuRowData> inList = new ArrayList<>();
        SkuDetailsResponseListener responseListener = new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode,
                                             List<SkuDetails> skuDetailsList) {
                // If we successfully got SKUs, add a header in front of it
                if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                    // Repacking the result for an adapter
                    for (SkuDetails details : skuDetailsList) {
                        Timber.i("Found sku: " + details);
                        inList.add(new SkuRowData(details.getSku(), details.getTitle(),
                                details.getPrice(), details.getDescription(),
                                details.getType()));
                    }
                    if (inList.size() == 0) {
                        displayAnErrorIfNeeded();
                    } else {
                        mAdapter.updateData(inList);
                        setWaitScreen(false);
                    }
                }
            }
        };

        // Start querying for in-app SKUs
        List<String> skus = mBillingProvider.getBillingManager().getSkus(BillingClient.SkuType.SUBS);
        mBillingProvider.getBillingManager().querySkuDetailsAsync(BillingClient.SkuType.SUBS, skus, responseListener);

    }

    private void displayAnErrorIfNeeded() {
        if (getActivity() == null || getActivity().isFinishing()) {
            Timber.i("No need to show an error - activity is finishing already");
            return;
        }

        mLoadingView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mErrorTextView.setText(getText(R.string.error_appsgit_not_finished));

        // TODO: Here you will need to handle various respond codes from BillingManager
    }
}


