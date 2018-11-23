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

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class BillingManager implements PurchasesUpdatedListener {

    private final BillingClient mBillingClient;
    private final Activity mActivity;
    private final String mUid;

    // Defining SKU constants from Google Play Developer Console
    private static final HashMap<String, List<String>> SKUS;

    static {
        SKUS = new HashMap<>();
        //SKUS.put(BillingClient.SkuType.INAPP, Arrays.asList("theme1", "theme2"));
        SKUS.put(BillingClient.SkuType.SUBS, Arrays.asList("abo_pro_month", "abo_pro_year"));
    }

    public BillingManager(Activity activity, String uid) {
        mActivity = activity;
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();
        mUid = uid;
    }

    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        Timber.i("onPurchasesUpdated() response: " + responseCode);

        if (responseCode == BillingClient.BillingResponse.OK) {
            //TODO: ((BillingActivity) mActivity).updateUi(purchases);
            consumePurchases(purchases);
        }

    }

    /*
     * By default you cannot buy the same product again and again...after you purchased the item you should cosume it.
     * For testing purpose i am going to consume all the product each and everytim i buy...
     */
    public void consumePurchases(List<Purchase> purchases) {
        try {
            if (purchases != null && purchases.size() > 0) {
                for (final Purchase purchase : purchases) {
                    if (purchase.getPurchaseToken() != null && !purchase.getPurchaseToken().isEmpty()) {
                        mBillingClient.consumeAsync(purchase.getPurchaseToken(), new ConsumeResponseListener() {
                            @Override
                            public void onConsumeResponse(int responseCode, String purchaseToken) {
                                Timber.d("onConsumeResponse: purchased SKU  " + purchase.getSku());
                                Timber.d("onConsumeResponse: purchased getPurchaseToken  " + purchase.getPurchaseToken());
                            }
                        });
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startServiceConnectionIfNeeded(final Runnable executeOnSuccess) {
        if (mBillingClient.isReady()) {
            if (executeOnSuccess != null) {
                executeOnSuccess.run();
            }
        } else {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) {
                    if (billingResponse == BillingClient.BillingResponse.OK) {
                        Timber.i("onBillingSetupFinished() response: " + billingResponse);
                        if (executeOnSuccess != null) {
                            executeOnSuccess.run();
                        }
                    } else {
                        Timber.w("onBillingSetupFinished() error code: " + billingResponse);
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Timber.w("onBillingServiceDisconnected()");
                }
            });
        }
    }

    //to get the list of in-app products which we created in Google play console.
    public void querySkuDetailsAsync(@BillingClient.SkuType final String itemType,
                                     final List<String> skuList, final SkuDetailsResponseListener listener) {
        // Specify a runnable to start when connection to Billing client is established
        Runnable executeOnConnectedService = new Runnable() {
            @Override
            public void run() {
                SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                        .setSkusList(skuList).setType(itemType).build();
                mBillingClient.querySkuDetailsAsync(skuDetailsParams,
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int responseCode,
                                                             List<SkuDetails> skuDetailsList) {
                                listener.onSkuDetailsResponse(responseCode, skuDetailsList);
                            }
                        });
            }
        };

        // If Billing client was disconnected, we retry 1 time and if success, execute the query
        startServiceConnectionIfNeeded(executeOnConnectedService);
    }

    public List<String> getSkus(@BillingClient.SkuType String type) {
        return SKUS.get(type);
    }

    //start the purchase.
    public void startPurchaseFlow(final String skuId, final String billingType, final String uid) {
        // Specify a runnable to start when connection to Billing client is established
        Runnable executeOnConnectedService = new Runnable() {

            @Override
            public void run() {
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setType(billingType)
                        .setSku(skuId)
                        .setDeveloperPayload(mUid)
                        .build();
                mBillingClient.launchBillingFlow(mActivity, billingFlowParams);
            }
        };

        // If Billing client was disconnected, we retry 1 time and if success, execute the query
        if(uid != null) {
            startServiceConnectionIfNeeded(executeOnConnectedService);
        }else{
            Toast.makeText(mActivity,"Error: you are not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * We shoould check purchased products and update them accordingly...
     * in my case, i am trying to consume as if they are not consumed...
     */
    public void checkPurchasedProducts(String billingType) {
        mBillingClient.queryPurchaseHistoryAsync(billingType,
                new PurchaseHistoryResponseListener() {
                    @Override
                    public void onPurchaseHistoryResponse(@BillingClient.BillingResponse int responseCode,
                                                          List<Purchase> purchasesList) {
                        if (responseCode == BillingClient.BillingResponse.OK
                                && purchasesList != null) {
//                            for (Purchase purchase : purchasesList) {
//                                // Process the result.
//                            }
                            consumePurchases(purchasesList);
                            Timber.d("onPurchaseHistoryResponse: purchased items..");
                        }
                    }
                });

    }

    public void destroy() {
        mBillingClient.endConnection();
    }

}