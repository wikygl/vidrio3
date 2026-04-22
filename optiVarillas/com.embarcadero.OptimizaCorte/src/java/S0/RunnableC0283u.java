package S0;

import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import com.google.android.gms.ads.MobileAds;
import y1.InterfaceC0862b;

/* renamed from: S0.u  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class RunnableC0283u implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2284j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2285k;

    public /* synthetic */ RunnableC0283u(int i4, Object obj) {
        this.f2284j = i4;
        this.f2285k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2284j) {
            case 0:
                final ActivityInicio.b bVar = (ActivityInicio.b) this.f2285k;
                bVar.getClass();
                MobileAds.a(ActivityInicio.this, new InterfaceC0862b() { // from class: S0.x
                    @Override // y1.InterfaceC0862b
                    public final void a() {
                        ActivityInicio.b bVar2 = ActivityInicio.b.this;
                        bVar2.getClass();
                        int i4 = ActivityInicio.f2947A0;
                        ActivityInicio.this.D();
                    }
                });
                return;
            default:
                ActivityOptimizacion activityOptimizacion = ((ActivityOptimizacion.h) this.f2285k).f3094j;
                activityOptimizacion.G(activityOptimizacion.f3063I);
                return;
        }
    }
}
