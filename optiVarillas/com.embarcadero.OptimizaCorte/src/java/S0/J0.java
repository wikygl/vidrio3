package S0;

import a1.InterfaceC0342a;
import b1.C0353a;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import java.util.ArrayList;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class J0 implements InterfaceC0342a {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ Object f2149j;

    public void a(com.android.billingclient.api.a aVar, List list) {
        ((Y0.a) this.f2149j).f(aVar, list);
    }

    @Override // a1.InterfaceC0342a
    public void b(int i4, b1.e eVar) {
        ActivityOptimizacion activityOptimizacion = (ActivityOptimizacion) this.f2149j;
        activityOptimizacion.f3075U.set(i4, eVar);
        C0353a c0353a = activityOptimizacion.f3076V;
        ArrayList<b1.e> arrayList = activityOptimizacion.f3075U;
        boolean z4 = c0353a.f2897x;
        c0353a.f2876b = ActivityOptimizacion.H(arrayList);
    }
}
