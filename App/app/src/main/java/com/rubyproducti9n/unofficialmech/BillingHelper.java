package com.rubyproducti9n.unofficialmech;

import android.telephony.SubscriptionPlan;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResult;

import java.util.List;

public class BillingHelper {



    private BillingClient billingClient;

    public BillingHelper(BillingClient billingClient) {
        this.billingClient = billingClient;
    }

}
