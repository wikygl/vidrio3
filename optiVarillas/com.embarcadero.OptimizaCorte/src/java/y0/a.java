package Y0;

import A1.I;
import D1.g0;
import Q0.d;
import Q0.e;
import Q0.f;
import Q0.g;
import Q0.h;
import Q0.m;
import Q0.v;
import S0.J0;
import Z2.c;
import Z2.d;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.billingclient.api.Purchase;
import com.google.android.gms.internal.play_billing.u;
import e0.C0405a;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class a implements f, Q0.b {

    /* renamed from: c  reason: collision with root package name */
    public static final d f2822c;

    /* renamed from: d  reason: collision with root package name */
    public static Q0.d f2823d;

    /* renamed from: e  reason: collision with root package name */
    public static Q0.d f2824e;
    public static a f;

    /* renamed from: a  reason: collision with root package name */
    public Z0.a f2825a;

    /* renamed from: b  reason: collision with root package name */
    public Q0.a f2826b;

    /* JADX WARN: Type inference failed for: r3v0, types: [Q0.g$b$a, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r5v1, types: [Q0.g$b$a, java.lang.Object] */
    static {
        ?? obj = new Object();
        obj.f1972b = "subs";
        obj.f1971a = "remove_ads";
        g.b a4 = obj.a();
        ?? obj2 = new Object();
        obj2.f1972b = "subs";
        obj2.f1971a = "remove_tabs";
        g.b a5 = obj2.a();
        c.a aVar = c.f2846k;
        Object[] objArr = {a4, a5};
        for (int i4 = 0; i4 < 2; i4++) {
            if (objArr[i4] == null) {
                throw new NullPointerException(C0405a.c("at index ", i4));
            }
        }
        f2822c = new d(2, objArr);
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [Y0.a, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r3v1, types: [java.lang.Object, B2.a] */
    public static a b(Context context, Z0.a aVar) {
        if (f == null) {
            Log.d("BILLING_V5_COPY", "getInstance null, new instance");
            ?? obj = new Object();
            obj.f2825a = aVar;
            ?? obj2 = new Object();
            if (context != null) {
                obj.f2826b = new Q0.a(obj2, context, obj);
                f = obj;
            } else {
                throw new IllegalArgumentException("Please provide a valid Context.");
            }
        }
        return f;
    }

    public static String c(Q0.d dVar) {
        ArrayList arrayList;
        if (dVar != null && (arrayList = dVar.f1959h) != null && !arrayList.isEmpty()) {
            Iterator it = ((d.C0020d) arrayList.get(0)).f1966b.f1964a.iterator();
            while (it.hasNext()) {
                d.b bVar = (d.b) it.next();
                if (bVar.f1963b.equals("P1M")) {
                    return bVar.f1962a;
                }
            }
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:108:0x020b, code lost:
        if (r9.f1945g == false) goto L115;
     */
    /* JADX WARN: Removed duplicated region for block: B:234:0x04f4  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x04f7  */
    /* JADX WARN: Removed duplicated region for block: B:241:0x0536 A[Catch: Exception -> 0x0547, CancellationException -> 0x0549, TimeoutException -> 0x054b, TRY_ENTER, TryCatch #4 {CancellationException -> 0x0549, TimeoutException -> 0x054b, Exception -> 0x0547, blocks: (B:241:0x0536, B:248:0x054d, B:250:0x0561, B:253:0x057d, B:254:0x058a), top: B:275:0x0534 }] */
    /* JADX WARN: Removed duplicated region for block: B:248:0x054d A[Catch: Exception -> 0x0547, CancellationException -> 0x0549, TimeoutException -> 0x054b, TryCatch #4 {CancellationException -> 0x0549, TimeoutException -> 0x054b, Exception -> 0x0547, blocks: (B:241:0x0536, B:248:0x054d, B:250:0x0561, B:253:0x057d, B:254:0x058a), top: B:275:0x0534 }] */
    /* JADX WARN: Type inference failed for: r4v0, types: [Q0.c$a$a, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r5v11, types: [Q0.c$b, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r9v1, types: [Q0.c, java.lang.Object] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void g(android.app.Activity r26, Y0.a r27) {
        /*
            Method dump skipped, instructions count: 1546
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: Y0.a.g(android.app.Activity, Y0.a):void");
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [Q0.h$a, java.lang.Object] */
    public final void a() {
        Log.d("BILLING_V5_COPY", "queryPurchases: quering purchases....");
        Q0.a aVar = this.f2826b;
        ?? obj = new Object();
        obj.f1974a = "subs";
        aVar.G(new h(obj), new J0(this));
    }

    public final boolean d() {
        StringBuilder sb = new StringBuilder("isReady: ");
        Q0.a aVar = this.f2826b;
        sb.append(aVar.F());
        Log.d("BILLING_V5_COPY", sb.toString());
        return aVar.F();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v2, types: [Q0.h$a, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r4v5, types: [Q0.e, java.lang.Object] */
    public final void e(com.android.billingclient.api.a aVar) {
        Log.d("BILLING_V5_COPY", "onBillingSetupFinished: setupBillingResult code: " + aVar);
        int i4 = aVar.a;
        if (i4 == 0) {
            StringBuilder sb = new StringBuilder("onBillingSetupFinished: productList: ");
            Z2.d dVar = f2822c;
            sb.append(dVar);
            Log.d("BILLING_V5_COPY", sb.toString());
            h(dVar, new Object());
            Log.d("BILLING_V5_COPY", "onBillingSetupFinished: queryPurchases");
            J0 j02 = new J0(this);
            Log.d("BILLING_V5_COPY", "queryPurchases: quering purchases....");
            Q0.a aVar2 = this.f2826b;
            ?? obj = new Object();
            obj.f1974a = "subs";
            aVar2.G(new h(obj), j02);
        } else if (i4 == 3) {
            Log.d("BILLING_V5_COPY", "onBillingSetupFinished: Billing unavailable, " + aVar);
            this.f2825a.a();
        }
    }

    /* JADX WARN: Type inference failed for: r1v20, types: [A1.I, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r2v5, types: [B0.v, java.lang.Object] */
    public final void f(com.android.billingclient.api.a aVar, List<Purchase> list) {
        char c4;
        Log.d("BILLING_V5_COPY", "onPurchasesUpdated: billingResult: " + aVar);
        Log.d("BILLING_V5_COPY", "onPurchasesUpdated: list: " + list);
        if (list == null) {
            Log.d("BILLING_V5_COPY", "onPurchasesUpdated: list es null!");
            this.f2825a.b();
            return;
        }
        Log.d("BILLING_V5_COPY", "onPurchasesUpdated: list size ? " + list.size());
        if (list.isEmpty()) {
            Log.d("BILLING_V5_COPY", "onPurchasesUpdated: list size es 0, se enviará null forzado al callback!");
            this.f2825a.b();
            return;
        }
        for (Purchase purchase : list) {
            int i4 = 1;
            if (purchase.c.optInt("purchaseState", 1) != 4) {
                c4 = 1;
            } else {
                c4 = 2;
            }
            JSONObject jSONObject = purchase.c;
            if (c4 == 1) {
                Log.d("BILLING_V5_COPY", "onPurchasesUpdated: Purchase.PurchaseState.PURCHASED, purchase = " + purchase);
                if (!jSONObject.optBoolean("acknowledged", true)) {
                    Log.d("BILLING_V5_COPY", "onPurchasesUpdated: !purchase.isAcknowledged()");
                    Log.d("BILLING_V5_COPY", "acknowledgePurchase: ");
                    String optString = jSONObject.optString("token", jSONObject.optString("purchaseToken"));
                    if (optString != null) {
                        final ?? obj = new Object();
                        obj.f293j = optString;
                        final ?? obj2 = new Object();
                        final Q0.a aVar2 = this.f2826b;
                        if (!aVar2.F()) {
                            com.android.billingclient.api.a aVar3 = com.android.billingclient.api.b.j;
                            aVar2.M(m.a(2, 3, aVar3));
                            I.c(aVar3);
                        } else if (TextUtils.isEmpty((String) obj.f293j)) {
                            u.e("BillingClient", "Please provide a valid purchase token.");
                            com.android.billingclient.api.a aVar4 = com.android.billingclient.api.b.g;
                            aVar2.M(m.a(26, 3, aVar4));
                            I.c(aVar4);
                        } else if (!aVar2.f1935v) {
                            com.android.billingclient.api.a aVar5 = com.android.billingclient.api.b.b;
                            aVar2.M(m.a(27, 3, aVar5));
                            I.c(aVar5);
                        } else if (aVar2.L(new Callable() { // from class: Q0.u
                            @Override // java.util.concurrent.Callable
                            public final Object call() {
                                a aVar6 = a.this;
                                B0.v vVar = obj;
                                I i5 = obj2;
                                aVar6.getClass();
                                try {
                                    String str = aVar6.f1925l;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("playBillingLibraryVersion", str);
                                    Bundle g12 = aVar6.f1930q.g1(aVar6.f1928o.getPackageName(), (String) vVar.f293j, bundle);
                                    com.android.billingclient.api.a a4 = com.android.billingclient.api.b.a(com.google.android.gms.internal.play_billing.u.c("BillingClient", g12), com.google.android.gms.internal.play_billing.u.a("BillingClient", g12));
                                    i5.getClass();
                                    I.c(a4);
                                    return null;
                                } catch (Exception e4) {
                                    com.google.android.gms.internal.play_billing.u.f("BillingClient", "Error acknowledge purchase!", e4);
                                    com.android.billingclient.api.a aVar7 = com.android.billingclient.api.b.j;
                                    aVar6.M(m.a(28, 3, aVar7));
                                    i5.getClass();
                                    I.c(aVar7);
                                    return null;
                                }
                            }
                        }, 30000L, new v(aVar2, 0, obj2), aVar2.I()) == null) {
                            com.android.billingclient.api.a K3 = aVar2.K();
                            aVar2.M(m.a(25, 3, K3));
                            I.c(K3);
                        }
                    } else {
                        throw new IllegalArgumentException("Purchase token must be set");
                    }
                }
                Log.d("BILLING_V5_COPY", "onPurchasesUpdated: purchase.getproducts? >> " + purchase.a());
                this.f2825a.c(purchase.a());
            } else {
                Z0.a aVar6 = this.f2825a;
                purchase.a();
                aVar6.b();
                jSONObject.optInt("purchaseState", 1);
                StringBuilder sb = new StringBuilder("onPurchasesUpdated: Purchase.PurchaseState.notpurchased, getPurchaseState: ");
                if (jSONObject.optInt("purchaseState", 1) == 4) {
                    i4 = 2;
                }
                sb.append(i4);
                Log.d("BILLING_V5_COPY", sb.toString());
            }
        }
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [Q0.g$a, java.lang.Object] */
    public final void h(Z2.d dVar, e eVar) {
        if (!d()) {
            i();
        }
        Log.d("BILLING_V5_COPY", "queryProductDetails: products: " + dVar);
        Log.d("BILLING_V5_COPY", "queryProductDetails: responseListener: " + eVar);
        Q0.a aVar = this.f2826b;
        ?? obj = new Object();
        if (dVar != null && !dVar.isEmpty()) {
            HashSet hashSet = new HashSet();
            c.a listIterator = dVar.listIterator(0);
            while (listIterator.hasNext()) {
                g.b bVar = (g.b) listIterator.next();
                if (!"play_pass_subs".equals(bVar.f1970b)) {
                    hashSet.add(bVar.f1970b);
                }
            }
            if (hashSet.size() <= 1) {
                obj.f1968a = com.google.android.gms.internal.play_billing.h.r(dVar);
                g gVar = new g(obj);
                if (!aVar.F()) {
                    com.android.billingclient.api.a aVar2 = com.android.billingclient.api.b.j;
                    aVar.M(m.a(2, 7, aVar2));
                    eVar.b(aVar2, new ArrayList());
                    return;
                } else if (!aVar.f1939z) {
                    u.e("BillingClient", "Querying product details is not supported.");
                    com.android.billingclient.api.a aVar3 = com.android.billingclient.api.b.o;
                    aVar.M(m.a(20, 7, aVar3));
                    eVar.b(aVar3, new ArrayList());
                    return;
                } else if (aVar.L(new K1.I(aVar, gVar, eVar, 1), 30000L, new g0(aVar, 1, eVar), aVar.I()) == null) {
                    com.android.billingclient.api.a K3 = aVar.K();
                    aVar.M(m.a(25, 7, K3));
                    eVar.b(K3, new ArrayList());
                    return;
                } else {
                    return;
                }
            }
            throw new IllegalArgumentException("All products should be of the same product type.");
        }
        throw new IllegalArgumentException("Product list cannot be empty.");
    }

    public final void i() {
        Log.d("BILLING_V5_COPY", "startConnection");
        if (!d()) {
            Log.d("BILLING_V5_COPY", "startConnection: not ready, starting connection");
            this.f2826b.H(this);
        }
    }
}
