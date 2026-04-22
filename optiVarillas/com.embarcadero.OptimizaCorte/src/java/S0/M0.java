package S0;

import a1.InterfaceC0342a;
import android.util.Log;
import b1.C0353a;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import java.util.ArrayList;
import q1.InterfaceC0770b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class M0 implements InterfaceC0342a, InterfaceC0770b.a {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ Object f2161j;

    public /* synthetic */ M0(Object obj) {
        this.f2161j = obj;
    }

    @Override // q1.InterfaceC0770b.a
    public Object a() {
        return ((p1.c) this.f2161j).d();
    }

    @Override // a1.InterfaceC0342a
    public void b(int i4, b1.e eVar) {
        ActivityOptimizacion activityOptimizacion = (ActivityOptimizacion) this.f2161j;
        activityOptimizacion.f3075U.set(i4, eVar);
        C0353a c0353a = activityOptimizacion.f3076V;
        ArrayList<b1.e> arrayList = activityOptimizacion.f3075U;
        boolean z4 = c0353a.f2897x;
        c0353a.f2876b = ActivityOptimizacion.H(arrayList);
    }

    public void c() {
        V0.c cVar = (V0.c) this.f2161j;
        cVar.getClass();
        Log.d("consentUMP", "onConsentInfoUpdateFailure: " + V0.c.f.a());
        cVar.b();
    }
}
