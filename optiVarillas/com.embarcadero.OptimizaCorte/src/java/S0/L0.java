package S0;

import a1.InterfaceC0342a;
import b1.C0353a;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class L0 implements InterfaceC0342a {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ Object f2158j;

    public /* synthetic */ L0(Object obj) {
        this.f2158j = obj;
    }

    @Override // a1.InterfaceC0342a
    public void b(int i4, b1.e eVar) {
        ActivityOptimizacion activityOptimizacion = (ActivityOptimizacion) this.f2158j;
        activityOptimizacion.f3074T.set(i4, eVar);
        C0353a c0353a = activityOptimizacion.f3076V;
        ArrayList<b1.e> arrayList = activityOptimizacion.f3074T;
        boolean z4 = c0353a.f2897x;
        c0353a.f2875a = ActivityOptimizacion.H(arrayList);
    }
}
