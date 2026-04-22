package S0;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import com.embarcadero.OptimizaCorte.Activities.ActivityRetales;

/* renamed from: S0.v  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class RunnableC0285v implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2286j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Z0.a f2287k;

    public /* synthetic */ RunnableC0285v(Z0.a aVar, int i4) {
        this.f2286j = i4;
        this.f2287k = aVar;
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion, android.app.Activity] */
    /* JADX WARN: Type inference failed for: r2v0, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio] */
    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2286j) {
            case 0:
                ActivityInicio.b bVar = (ActivityInicio.b) this.f2287k;
                bVar.getClass();
                ?? r22 = ActivityInicio.this;
                new WebView(r22);
                new Handler().postDelayed(new RunnableC0287w(0, bVar), 2000L);
                r22.f2986u0.f2897x = false;
                r22.l0.setVisibility(0);
                r22.f2959S.setText("Version 5.2.9.2 (build 317) FREE");
                r22.f2967a0.setVisibility(8);
                Log.d("INI_BILLING_V5_COPY", "onNotLogin: ");
                return;
            case 1:
                ?? r02 = ActivityOptimizacion.this;
                r02.f3076V.f2897x = false;
                r02.f3064J.setVisibility(0);
                Log.d("DIA_BILLING_V5_COPY", "onNotPurchase prefscount: " + r02.getSharedPreferences("cutsettings", 0).getInt("opencount", 1));
                if (r02.getSharedPreferences("cutsettings", 0).getInt("opencount", 1) % 5 == 0 || r02.f3082b0) {
                    r02.f3082b0 = false;
                    if (!r02.isFinishing() && !r02.isDestroyed()) {
                        Log.d("OP_BILLING_V5_COPY", "onNotPurchase: abriendo dialog...");
                        ActivityOptimizacion.b bVar2 = r02.f3086f0;
                        Y0.a b4 = Y0.a.b(r02, bVar2);
                        r02.f3083c0 = b4;
                        b4.f2825a = bVar2;
                        Y0.b bVar3 = new Y0.b(r02, b4);
                        Log.d("OP_BILLING_V5_COPY", "onNotPurchase: appPaused? " + r02.f3073S);
                        if (!r02.f3073S) {
                            Log.d("OP_BILLING_V5_COPY", "onNotPurchase: abriendo dialog AHORA");
                            r02.runOnUiThread(new A(2, bVar3));
                        }
                    }
                }
                Log.d("OP_BILLING_V5_COPY", "onNotPurchase: ");
                return;
            default:
                ActivityRetales activityRetales = ActivityRetales.this;
                activityRetales.f3112X.f2897x = true;
                activityRetales.f3096H.setVisibility(8);
                Log.d("INI_BILLING_V5_COPY", "onPurchase: ");
                return;
        }
    }
}
