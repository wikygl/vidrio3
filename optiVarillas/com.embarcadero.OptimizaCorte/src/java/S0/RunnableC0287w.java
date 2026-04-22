package S0;

import android.util.Log;
import androidx.lifecycle.f;
import b1.C0353a;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import com.embarcadero.OptimizaCorte.Activities.ActivityRetales;
import com.google.android.gms.ads.MobileAds;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import y1.InterfaceC0862b;

/* renamed from: S0.w  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class RunnableC0287w implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2290j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2291k;

    public /* synthetic */ RunnableC0287w(int i4, Object obj) {
        this.f2290j = i4;
        this.f2291k = obj;
    }

    /* JADX WARN: Type inference failed for: r0v17, types: [com.embarcadero.OptimizaCorte.Activities.ActivityRetales, android.app.Activity] */
    @Override // java.lang.Runnable
    public final void run() {
        String str;
        switch (this.f2290j) {
            case 0:
                final ActivityInicio.b bVar = (ActivityInicio.b) this.f2291k;
                bVar.getClass();
                MobileAds.a(ActivityInicio.this, new InterfaceC0862b() { // from class: S0.y
                    @Override // y1.InterfaceC0862b
                    public final void a() {
                        ActivityInicio.b bVar2 = ActivityInicio.b.this;
                        bVar2.getClass();
                        int i4 = ActivityInicio.f2947A0;
                        ActivityInicio.this.D();
                    }
                });
                return;
            case 1:
                ActivityListaCorte activityListaCorte = ActivityListaCorte.this;
                activityListaCorte.D(activityListaCorte.f3022b0);
                String str2 = "Not null";
                if (activityListaCorte.f3021a0 != null) {
                    str = "Not null";
                } else {
                    str = "Null";
                }
                Log.d("INTERSTITIAL_TIMER", "run: PRE interstitial status: ".concat(str));
                Log.d("INTERSTITIAL_TIMER", "run: intersticial recargado");
                if (activityListaCorte.f3021a0 == null) {
                    str2 = "Null";
                }
                Log.d("INTERSTITIAL_TIMER", "run: interstitial status: ".concat(str2));
                return;
            case 2:
                ActivityOptimizacion activityOptimizacion = ActivityOptimizacion.this;
                activityOptimizacion.f3076V.f2897x = true;
                activityOptimizacion.f3064J.setVisibility(8);
                Log.d("OP_BILLING_V5_COPY", "onPurchase: ");
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ?? r02 = ActivityRetales.this;
                new V0.c(r02, r02.f3096H).a();
                C0353a c0353a = r02.f3112X;
                c0353a.f2896w = true;
                c0353a.f2897x = false;
                r02.f3096H.setVisibility(0);
                Log.d("INI_BILLING_V5_COPY", "onNotPurchase: ");
                return;
            default:
                androidx.lifecycle.s sVar = (androidx.lifecycle.s) this.f2291k;
                v3.h.e(sVar, "this$0");
                int i4 = sVar.k;
                androidx.lifecycle.l lVar = sVar.o;
                if (i4 == 0) {
                    sVar.l = true;
                    lVar.e(f.a.ON_PAUSE);
                }
                if (sVar.j == 0 && sVar.l) {
                    lVar.e(f.a.ON_STOP);
                    sVar.m = true;
                    return;
                }
                return;
        }
    }
}
