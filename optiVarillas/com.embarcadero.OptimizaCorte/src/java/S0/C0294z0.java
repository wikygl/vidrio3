package S0;

import android.util.Log;
import com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5;
import com.google.android.material.tabs.d;
import e.C0397f;
import java.util.ArrayList;

/* renamed from: S0.z0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class C0294z0 implements d.b, Q0.e {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ C0397f f2301j;

    public /* synthetic */ C0294z0(C0397f c0397f) {
        this.f2301j = c0397f;
    }

    @Override // Q0.e
    public void b(com.android.billingclient.api.a aVar, ArrayList arrayList) {
        int i4 = ActivityTestBillingV5.f3117Q;
        ((ActivityTestBillingV5) this.f2301j).getClass();
        Log.d("ACTV_BILLING_V5_COPY", "onProductDetailsResponse: billingResult: " + aVar);
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            Log.d("ACTV_BILLING_V5_COPY", "onProductDetailsResponse: item[" + i5 + "]: " + arrayList.get(i5));
            StringBuilder sb = new StringBuilder("onProductDetailsResponse: price: ");
            sb.append(arrayList.get(i5));
            Log.d("ACTV_BILLING_V5_COPY", sb.toString());
        }
    }
}
