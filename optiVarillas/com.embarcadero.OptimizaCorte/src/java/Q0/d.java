package Q0;

import android.text.TextUtils;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class d {

    /* renamed from: a  reason: collision with root package name */
    public final String f1953a;

    /* renamed from: b  reason: collision with root package name */
    public final JSONObject f1954b;

    /* renamed from: c  reason: collision with root package name */
    public final String f1955c;

    /* renamed from: d  reason: collision with root package name */
    public final String f1956d;

    /* renamed from: e  reason: collision with root package name */
    public final String f1957e;
    public final String f;

    /* renamed from: g  reason: collision with root package name */
    public final String f1958g;

    /* renamed from: h  reason: collision with root package name */
    public final ArrayList f1959h;

    /* renamed from: i  reason: collision with root package name */
    public final ArrayList f1960i;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public final String f1961a;

        public a(JSONObject jSONObject) {
            jSONObject.optString("formattedPrice");
            jSONObject.optLong("priceAmountMicros");
            jSONObject.optString("priceCurrencyCode");
            String optString = jSONObject.optString("offerIdToken");
            this.f1961a = true == optString.isEmpty() ? null : optString;
            jSONObject.optString("offerId").getClass();
            jSONObject.optString("purchaseOptionId").getClass();
            jSONObject.optInt("offerType");
            JSONArray optJSONArray = jSONObject.optJSONArray("offerTags");
            ArrayList arrayList = new ArrayList();
            if (optJSONArray != null) {
                for (int i4 = 0; i4 < optJSONArray.length(); i4++) {
                    arrayList.add(optJSONArray.getString(i4));
                }
            }
            com.google.android.gms.internal.play_billing.h.r(arrayList);
            if (jSONObject.has("fullPriceMicros")) {
                jSONObject.optLong("fullPriceMicros");
            }
            JSONObject optJSONObject = jSONObject.optJSONObject("discountDisplayInfo");
            if (optJSONObject != null) {
                optJSONObject.getInt("percentageDiscount");
            }
            JSONObject optJSONObject2 = jSONObject.optJSONObject("validTimeWindow");
            if (optJSONObject2 != null) {
                optJSONObject2.getLong("startTimeMillis");
                optJSONObject2.getLong("endTimeMillis");
            }
            JSONObject optJSONObject3 = jSONObject.optJSONObject("limitedQuantityInfo");
            if (optJSONObject3 != null) {
                optJSONObject3.getInt("maximumQuantity");
                optJSONObject3.getInt("remainingQuantity");
            }
            JSONObject optJSONObject4 = jSONObject.optJSONObject("preorderDetails");
            if (optJSONObject4 != null) {
                optJSONObject4.getLong("preorderReleaseTimeMillis");
                optJSONObject4.getLong("preorderPresaleEndTimeMillis");
            }
            JSONObject optJSONObject5 = jSONObject.optJSONObject("rentalDetails");
            if (optJSONObject5 != null) {
                optJSONObject5.getString("rentalPeriod");
                optJSONObject5.optString("rentalExpirationPeriod").getClass();
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class b {

        /* renamed from: a  reason: collision with root package name */
        public final String f1962a;

        /* renamed from: b  reason: collision with root package name */
        public final String f1963b;

        public b(JSONObject jSONObject) {
            this.f1963b = jSONObject.optString("billingPeriod");
            jSONObject.optString("priceCurrencyCode");
            this.f1962a = jSONObject.optString("formattedPrice");
            jSONObject.optLong("priceAmountMicros");
            jSONObject.optInt("recurrenceMode");
            jSONObject.optInt("billingCycleCount");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class c {

        /* renamed from: a  reason: collision with root package name */
        public final ArrayList f1964a;

        public c(JSONArray jSONArray) {
            ArrayList arrayList = new ArrayList();
            if (jSONArray != null) {
                for (int i4 = 0; i4 < jSONArray.length(); i4++) {
                    JSONObject optJSONObject = jSONArray.optJSONObject(i4);
                    if (optJSONObject != null) {
                        arrayList.add(new b(optJSONObject));
                    }
                }
            }
            this.f1964a = arrayList;
        }
    }

    /* renamed from: Q0.d$d  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class C0020d {

        /* renamed from: a  reason: collision with root package name */
        public final String f1965a;

        /* renamed from: b  reason: collision with root package name */
        public final c f1966b;

        public C0020d(JSONObject jSONObject) {
            jSONObject.optString("basePlanId");
            jSONObject.optString("offerId").getClass();
            this.f1965a = jSONObject.getString("offerIdToken");
            this.f1966b = new c(jSONObject.getJSONArray("pricingPhases"));
            JSONObject optJSONObject = jSONObject.optJSONObject("installmentPlanDetails");
            if (optJSONObject != null) {
                optJSONObject.getInt("commitmentPaymentsCount");
                optJSONObject.optInt("subsequentCommitmentPaymentsCount");
            }
            JSONObject optJSONObject2 = jSONObject.optJSONObject("transitionPlanDetails");
            if (optJSONObject2 != null) {
                optJSONObject2.getString("productId");
                optJSONObject2.optString("title");
                optJSONObject2.optString("name");
                optJSONObject2.optString("description");
                optJSONObject2.optString("basePlanId");
                JSONObject optJSONObject3 = optJSONObject2.optJSONObject("pricingPhase");
                if (optJSONObject3 != null) {
                    optJSONObject3.optString("billingPeriod");
                    optJSONObject3.optString("priceCurrencyCode");
                    optJSONObject3.optString("formattedPrice");
                    optJSONObject3.optLong("priceAmountMicros");
                    optJSONObject3.optInt("recurrenceMode");
                    optJSONObject3.optInt("billingCycleCount");
                }
            }
            ArrayList arrayList = new ArrayList();
            JSONArray optJSONArray = jSONObject.optJSONArray("offerTags");
            if (optJSONArray != null) {
                for (int i4 = 0; i4 < optJSONArray.length(); i4++) {
                    arrayList.add(optJSONArray.getString(i4));
                }
            }
        }
    }

    public d(String str) {
        ArrayList arrayList;
        this.f1953a = str;
        JSONObject jSONObject = new JSONObject(str);
        this.f1954b = jSONObject;
        String optString = jSONObject.optString("productId");
        this.f1955c = optString;
        String optString2 = jSONObject.optString("type");
        this.f1956d = optString2;
        if (!TextUtils.isEmpty(optString)) {
            if (!TextUtils.isEmpty(optString2)) {
                this.f1957e = jSONObject.optString("title");
                jSONObject.optString("name");
                jSONObject.optString("description");
                jSONObject.optString("packageDisplayName");
                jSONObject.optString("iconUrl");
                this.f = jSONObject.optString("skuDetailsToken");
                this.f1958g = jSONObject.optString("serializedDocid");
                JSONArray optJSONArray = jSONObject.optJSONArray("subscriptionOfferDetails");
                if (optJSONArray != null) {
                    ArrayList arrayList2 = new ArrayList();
                    for (int i4 = 0; i4 < optJSONArray.length(); i4++) {
                        arrayList2.add(new C0020d(optJSONArray.getJSONObject(i4)));
                    }
                    this.f1959h = arrayList2;
                } else {
                    if (!optString2.equals("subs") && !optString2.equals("play_pass_subs")) {
                        arrayList = null;
                    } else {
                        arrayList = new ArrayList();
                    }
                    this.f1959h = arrayList;
                }
                JSONObject optJSONObject = this.f1954b.optJSONObject("oneTimePurchaseOfferDetails");
                JSONArray optJSONArray2 = this.f1954b.optJSONArray("oneTimePurchaseOfferDetailsList");
                ArrayList arrayList3 = new ArrayList();
                if (optJSONArray2 != null) {
                    for (int i5 = 0; i5 < optJSONArray2.length(); i5++) {
                        arrayList3.add(new a(optJSONArray2.getJSONObject(i5)));
                    }
                    this.f1960i = arrayList3;
                    return;
                } else if (optJSONObject != null) {
                    arrayList3.add(new a(optJSONObject));
                    this.f1960i = arrayList3;
                    return;
                } else {
                    this.f1960i = null;
                    return;
                }
            }
            throw new IllegalArgumentException("Product type cannot be empty.");
        }
        throw new IllegalArgumentException("Product id cannot be empty.");
    }

    public final a a() {
        ArrayList arrayList = this.f1960i;
        if (arrayList != null && !arrayList.isEmpty()) {
            return (a) arrayList.get(0);
        }
        return null;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d)) {
            return false;
        }
        return TextUtils.equals(this.f1953a, ((d) obj).f1953a);
    }

    public final int hashCode() {
        return this.f1953a.hashCode();
    }

    public final String toString() {
        String obj = this.f1954b.toString();
        String valueOf = String.valueOf(this.f1959h);
        return "ProductDetails{jsonString='" + this.f1953a + "', parsedJson=" + obj + ", productId='" + this.f1955c + "', productType='" + this.f1956d + "', title='" + this.f1957e + "', productDetailsToken='" + this.f + "', subscriptionOfferDetails=" + valueOf + "}";
    }
}
