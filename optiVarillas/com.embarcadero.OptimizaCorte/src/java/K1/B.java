package K1;

import S0.J0;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import c2.InterfaceC0374a;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.a;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.t7;
import com.google.android.gms.internal.ads.zG;
import com.google.android.gms.internal.play_billing.Y1;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class B implements Callable {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f1285a = 0;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Object f1286b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ Object f1287c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ Object f1288d;

    public /* synthetic */ B(C0208b c0208b, Uri uri, InterfaceC0374a interfaceC0374a) {
        this.f1286b = c0208b;
        this.f1287c = uri;
        this.f1288d = interfaceC0374a;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        zG zGVar;
        Q0.p pVar;
        Bundle t4;
        Q0.q qVar;
        int i4;
        int i5 = 1;
        switch (this.f1285a) {
            case 0:
                Uri uri = (Uri) this.f1287c;
                C0208b c0208b = (C0208b) this.f1286b;
                c0208b.getClass();
                try {
                    boolean booleanValue = ((Boolean) A1.r.f168d.f171c.a(Gb.Ia)).booleanValue();
                    InterfaceC0374a interfaceC0374a = (InterfaceC0374a) this.f1288d;
                    if (booleanValue && (zGVar = c0208b.f1344n) != null) {
                        uri = zGVar.a(uri, c0208b.f1342l, (View) c2.b.p0(interfaceC0374a), (Activity) null);
                    } else {
                        uri = c0208b.f1343m.a(uri, c0208b.f1342l, (View) c2.b.p0(interfaceC0374a), (Activity) null);
                    }
                } catch (t7 e4) {
                    E1.m.h("", e4);
                }
                if (uri.getQueryParameter("ms") != null) {
                    return uri;
                }
                throw new Exception("Failed to append spam signals to click url.");
            default:
                Q0.a aVar = (Q0.a) this.f1288d;
                String str = (String) this.f1286b;
                com.google.android.gms.internal.play_billing.u.d("BillingClient", "Querying owned items, item type: ".concat(String.valueOf(str)));
                ArrayList arrayList = new ArrayList();
                boolean z4 = aVar.f1935v;
                aVar.f1921D.getClass();
                aVar.f1921D.getClass();
                String str2 = aVar.f1925l;
                Bundle bundle = new Bundle();
                bundle.putString("playBillingLibraryVersion", str2);
                if (z4) {
                    bundle.putBoolean("enablePendingPurchases", true);
                }
                String str3 = null;
                while (true) {
                    try {
                        if (aVar.f1935v) {
                            Y1 y12 = aVar.f1930q;
                            if (i5 != aVar.f1918A) {
                                i4 = 9;
                            } else {
                                i4 = 19;
                            }
                            t4 = y12.F3(i4, aVar.f1928o.getPackageName(), str, str3, bundle);
                        } else {
                            t4 = aVar.f1930q.t4(aVar.f1928o.getPackageName(), str, str3);
                        }
                        com.android.billingclient.api.a aVar2 = com.android.billingclient.api.b.h;
                        if (t4 == null) {
                            com.google.android.gms.internal.play_billing.u.e("BillingClient", "getPurchase() got null owned items list");
                            qVar = new Q0.q(54, aVar2);
                        } else {
                            int a4 = com.google.android.gms.internal.play_billing.u.a("BillingClient", t4);
                            String c4 = com.google.android.gms.internal.play_billing.u.c("BillingClient", t4);
                            a.a a5 = com.android.billingclient.api.a.a();
                            a5.a = a4;
                            a5.b = c4;
                            com.android.billingclient.api.a a6 = a5.a();
                            if (a4 != 0) {
                                com.google.android.gms.internal.play_billing.u.e("BillingClient", "getPurchase() failed. Response code: " + a4);
                                qVar = new Q0.q(23, a6);
                            } else if (t4.containsKey("INAPP_PURCHASE_ITEM_LIST") && t4.containsKey("INAPP_PURCHASE_DATA_LIST") && t4.containsKey("INAPP_DATA_SIGNATURE_LIST")) {
                                ArrayList<String> stringArrayList = t4.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                                ArrayList<String> stringArrayList2 = t4.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                                ArrayList<String> stringArrayList3 = t4.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                                if (stringArrayList == null) {
                                    com.google.android.gms.internal.play_billing.u.e("BillingClient", "Bundle returned from getPurchase() contains null SKUs list.");
                                    qVar = new Q0.q(56, aVar2);
                                } else if (stringArrayList2 == null) {
                                    com.google.android.gms.internal.play_billing.u.e("BillingClient", "Bundle returned from getPurchase() contains null purchases list.");
                                    qVar = new Q0.q(57, aVar2);
                                } else if (stringArrayList3 == null) {
                                    com.google.android.gms.internal.play_billing.u.e("BillingClient", "Bundle returned from getPurchase() contains null signatures list.");
                                    qVar = new Q0.q(58, aVar2);
                                } else {
                                    qVar = new Q0.q(i5, com.android.billingclient.api.b.i);
                                }
                            } else {
                                com.google.android.gms.internal.play_billing.u.e("BillingClient", "Bundle returned from getPurchase() doesn't contain required fields.");
                                qVar = new Q0.q(55, aVar2);
                            }
                        }
                        com.android.billingclient.api.a aVar3 = (com.android.billingclient.api.a) qVar.f1995b;
                        if (aVar3 != com.android.billingclient.api.b.i) {
                            aVar.M(Q0.m.a(qVar.f1994a, 9, aVar3));
                            pVar = new Q0.p(aVar3, (ArrayList) null);
                        } else {
                            ArrayList<String> stringArrayList4 = t4.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                            ArrayList<String> stringArrayList5 = t4.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                            ArrayList<String> stringArrayList6 = t4.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                            boolean z5 = false;
                            for (int i6 = 0; i6 < stringArrayList5.size(); i6++) {
                                String str4 = stringArrayList5.get(i6);
                                String str5 = stringArrayList6.get(i6);
                                com.google.android.gms.internal.play_billing.u.d("BillingClient", "Sku is owned: ".concat(String.valueOf(stringArrayList4.get(i6))));
                                try {
                                    Purchase purchase = new Purchase(str4, str5);
                                    JSONObject jSONObject = purchase.c;
                                    if (TextUtils.isEmpty(jSONObject.optString("token", jSONObject.optString("purchaseToken")))) {
                                        com.google.android.gms.internal.play_billing.u.e("BillingClient", "BUG: empty/null token!");
                                        z5 = true;
                                    }
                                    arrayList.add(purchase);
                                    i5 = 1;
                                } catch (JSONException e5) {
                                    com.google.android.gms.internal.play_billing.u.f("BillingClient", "Got an exception trying to decode the purchase!", e5);
                                    com.android.billingclient.api.a aVar4 = com.android.billingclient.api.b.h;
                                    aVar.M(Q0.m.a(51, 9, aVar4));
                                    pVar = new Q0.p(aVar4, (ArrayList) null);
                                }
                            }
                            if (z5) {
                                aVar.M(Q0.m.a(26, 9, com.android.billingclient.api.b.h));
                            }
                            str3 = t4.getString("INAPP_CONTINUATION_TOKEN");
                            com.google.android.gms.internal.play_billing.u.d("BillingClient", "Continuation token: ".concat(String.valueOf(str3)));
                            if (TextUtils.isEmpty(str3)) {
                                pVar = new Q0.p(com.android.billingclient.api.b.i, arrayList);
                            }
                        }
                    } catch (Exception e6) {
                        com.android.billingclient.api.a aVar5 = com.android.billingclient.api.b.j;
                        aVar.M(Q0.m.a(52, 9, aVar5));
                        com.google.android.gms.internal.play_billing.u.f("BillingClient", "Got exception trying to get purchasesm try to reconnect", e6);
                        pVar = new Q0.p(aVar5, (ArrayList) null);
                    }
                }
                List list = (List) pVar.f1992k;
                if (list != null) {
                    ((J0) this.f1287c).a((com.android.billingclient.api.a) pVar.f1993l, list);
                    return null;
                }
                com.google.android.gms.internal.play_billing.f fVar = com.google.android.gms.internal.play_billing.h.k;
                ((J0) this.f1287c).a((com.android.billingclient.api.a) pVar.f1993l, com.google.android.gms.internal.play_billing.m.n);
                return null;
        }
    }

    public B(Q0.a aVar, String str, J0 j02) {
        this.f1286b = str;
        this.f1287c = j02;
        this.f1288d = aVar;
    }
}
