package M;

import S0.RunnableC0281t;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.activity.ComponentActivity;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorteContainer;
import com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class C implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1514j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f1515k;

    public /* synthetic */ C(int i4, Object obj) {
        this.f1514j = i4;
        this.f1515k = obj;
    }

    /* JADX WARN: Type inference failed for: r0v19, types: [com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas, android.app.Activity] */
    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f1514j) {
            case 0:
                View view = (View) this.f1515k;
                ((InputMethodManager) view.getContext().getSystemService("input_method")).showSoftInput(view, 0);
                return;
            case 1:
                ActivityInicio activityInicio = ActivityInicio.this;
                activityInicio.f2986u0.f2897x = true;
                activityInicio.l0.setVisibility(8);
                activityInicio.f2967a0.setVisibility(8);
                activityInicio.f2959S.setText("Version 5.2.9.2 (build 317) PREMIUM");
                Log.d("INI_BILLING_V5_COPY", "onPurchase: ");
                return;
            case 2:
                ActivityInicio activityInicio2 = ActivityInicio.this;
                activityInicio2.f2986u0.f2897x = false;
                activityInicio2.l0.setVisibility(0);
                ActivityInicio.A(activityInicio2);
                activityInicio2.f2967a0.setVisibility(8);
                Log.d("DIA_BILLING_V5_COPY", "onNotLogin: ");
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ActivityListaCorteContainer activityListaCorteContainer = (ActivityListaCorteContainer) this.f1515k;
                activityListaCorteContainer.f3043I.setCurrentItem(activityListaCorteContainer.f3042H.getTabCount() - 2);
                activityListaCorteContainer.f3043I.x.getClass();
                return;
            case 4:
                ?? r02 = ((ActivityListasGuardadas.a) this.f1515k).f3060a;
                new V0.c(r02, r02.f3048H).a();
                r02.f3054N.f2897x = false;
                r02.f3048H.setVisibility(0);
                Log.d("LG_BILLING_V5_COPY", "onNotLogin: ");
                return;
            case 5:
                ((ComponentActivity) this.f1515k).invalidateOptionsMenu();
                return;
            default:
                new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue()).execute(new RunnableC0281t(1, (Context) this.f1515k));
                return;
        }
    }
}
