package S0;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import u0.f;

/* renamed from: S0.u0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class C0284u0 implements Q0.e, f.e {
    public static String c(String str, String str2) {
        return str + str2;
    }

    public static void d(int i4, HashMap hashMap, String str, int i5, String str2) {
        hashMap.put(str, Integer.valueOf(i4));
        hashMap.put(str2, Integer.valueOf(i5));
    }

    @Override // u0.f.e
    public void a(f.d dVar, u0.f fVar) {
        dVar.b();
    }

    @Override // Q0.e
    public void b(com.android.billingclient.api.a aVar, ArrayList arrayList) {
        if (aVar.a == 0) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Q0.d dVar = (Q0.d) it.next();
                Log.d("BILLING_V5_COPY", "onBillingSetupFinished: details item from list: " + dVar);
                if (dVar.f1955c.equals("remove_ads")) {
                    Y0.a.f2823d = dVar;
                }
                if (dVar.f1955c.equals("remove_tabs")) {
                    Y0.a.f2824e = dVar;
                }
            }
        }
    }
}
