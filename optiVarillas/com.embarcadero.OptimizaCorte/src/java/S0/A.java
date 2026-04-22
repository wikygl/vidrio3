package S0;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorteContainer;
import com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas;
import com.google.android.material.tabs.TabLayout;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import q1.InterfaceC0770b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class A implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2107j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2108k;

    public /* synthetic */ A(int i4, Object obj) {
        this.f2107j = i4;
        this.f2108k = obj;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v8, types: [com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas, android.app.Activity] */
    /* JADX WARN: Type inference failed for: r3v1, types: [android.content.DialogInterface$OnClickListener, java.lang.Object] */
    @Override // java.lang.Runnable
    public final void run() {
        Object obj = this.f2108k;
        switch (this.f2107j) {
            case 0:
                ActivityInicio activityInicio = ActivityInicio.this;
                ActivityInicio.A(activityInicio);
                activityInicio.f2986u0.f2897x = false;
                activityInicio.f2967a0.setVisibility(0);
                activityInicio.l0.setVisibility(0);
                Log.d("DIA_BILLING_V5_COPY", "onNotPurchase: ");
                return;
            case 1:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                androidx.fragment.app.p pVar = (ActivityListaCorte) obj;
                pVar.getClass();
                ActivityListaCorte.l0.setProgressStyle(1);
                ActivityListaCorte.l0.setMax(100);
                ActivityListaCorte.l0.setProgress(0);
                ActivityListaCorte.l0.setButton(-2, pVar.getString(2131820699), (DialogInterface.OnClickListener) new Object());
                try {
                    ActivityListaCorte.l0.setProgressNumberFormat("");
                    String string = pVar.getString(2131820708);
                    if (string != null && !string.isEmpty()) {
                        ActivityListaCorte.l0.setMessage(string);
                    } else {
                        Log.e("LC_BILLING_V5_COPY", "btnOptSetOnClickListener: Message is empty or null");
                    }
                } catch (IndexOutOfBoundsException e4) {
                    Log.e("LC_BILLING_V5_COPY", "btnOptSetOnClickListener: IndexOutOfBoundsException", e4);
                } catch (NullPointerException unused) {
                    Log.e("LC_BILLING_V5_COPY", "btnOptSetOnClickListener: NPE progress setmsg");
                }
                if (!pVar.isFinishing() && !pVar.isDestroyed()) {
                    ActivityListaCorte.l0.show();
                    return;
                }
                return;
            case 2:
                ((Y0.b) obj).a();
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ActivityListaCorte.g gVar = (ActivityListaCorte.g) obj;
                ActivityListaCorte activityListaCorte = ActivityListaCorte.this;
                activityListaCorte.f3025e0.f2897x = false;
                activityListaCorte.f3009O.setImageResource(2131165312);
                activityListaCorte.f3009O.setOnClickListener(new View$OnClickListenerC0278r0(2, gVar));
                activityListaCorte.f3020Z.setVisibility(0);
                activityListaCorte.G();
                Log.d("DIA_BILLING_V5_COPY", "onNotLogin: ");
                return;
            case 4:
                ActivityListaCorteContainer activityListaCorteContainer = (ActivityListaCorteContainer) obj;
                TabLayout tabLayout = activityListaCorteContainer.f3042H;
                tabLayout.l(tabLayout.h(tabLayout.getTabCount() - 2), true);
                activityListaCorteContainer.f3043I.post(new M.C(3, activityListaCorteContainer));
                return;
            case 5:
                ?? r02 = ActivityListasGuardadas.this;
                new V0.c(r02, r02.f3048H).a();
                r02.f3054N.f2897x = false;
                r02.f3048H.setVisibility(0);
                Log.d("LG_BILLING_V5_COPY", "onNotPurchase: ");
                return;
            case 6:
                U2.m mVar = (U2.m) obj;
                boolean isPopupShowing = mVar.f2412h.isPopupShowing();
                mVar.t(isPopupShowing);
                mVar.f2417m = isPopupShowing;
                return;
            case 7:
                ((l.f0) obj).a();
                return;
            default:
                final o1.s sVar = (o1.s) obj;
                sVar.getClass();
                sVar.f5485d.a(new InterfaceC0770b.a() { // from class: o1.r
                    @Override // q1.InterfaceC0770b.a
                    public final Object a() {
                        s sVar2 = s.this;
                        for (i1.s sVar3 : sVar2.f5483b.n()) {
                            sVar2.f5484c.b(sVar3, 1);
                        }
                        return null;
                    }
                });
                return;
        }
    }
}
