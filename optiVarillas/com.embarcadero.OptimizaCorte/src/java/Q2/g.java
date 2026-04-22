package Q2;

import android.util.Log;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import com.google.android.material.sidesheet.SideSheetBehavior;
import l.f0;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class g implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2027j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2028k;

    public /* synthetic */ g(int i4, Object obj) {
        this.f2027j = i4;
        this.f2028k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2027j) {
            case 0:
                SideSheetBehavior.c cVar = (SideSheetBehavior.c) this.f2028k;
                cVar.b = false;
                SideSheetBehavior sideSheetBehavior = cVar.d;
                V.c cVar2 = sideSheetBehavior.i;
                if (cVar2 != null && cVar2.f()) {
                    cVar.a(cVar.a);
                    return;
                } else if (sideSheetBehavior.h == 2) {
                    sideSheetBehavior.s(cVar.a);
                    return;
                } else {
                    return;
                }
            case 1:
                ActivityInicio activityInicio = ActivityInicio.this;
                ActivityInicio.A(activityInicio);
                activityInicio.f2986u0.f2897x = true;
                activityInicio.F();
                activityInicio.l0.setVisibility(8);
                activityInicio.f2967a0.setVisibility(8);
                Log.d("DIA_BILLING_V5_COPY", "onPurchase: ");
                super/*android.app.Activity*/.recreate();
                return;
            case 2:
                ActivityListasGuardadas activityListasGuardadas = ((ActivityListasGuardadas.a) this.f2028k).f3060a;
                activityListasGuardadas.f3054N.f2897x = true;
                activityListasGuardadas.f3048H.setVisibility(8);
                Log.d("LG_BILLING_V5_COPY", "onPurchase: ");
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ActivityOptimizacion activityOptimizacion = ((ActivityOptimizacion.b) this.f2028k).f3088a;
                activityOptimizacion.f3076V.f2897x = false;
                activityOptimizacion.f3064J.setVisibility(0);
                Log.d("DIA_BILLING_V5_COPY", "onNotLogin: ");
                return;
            case 4:
                ((U2.f) this.f2028k).t(true);
                return;
            case 5:
                ((com.google.android.material.timepicker.c) this.f2028k).f();
                return;
            default:
                ((f0) this.f2028k).c(false);
                return;
        }
    }
}
