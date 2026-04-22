package S0;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Trace;
import android.util.Log;
import androidx.activity.ComponentActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.emoji2.text.f;
import androidx.emoji2.text.m;
import b1.C0353a;
import b2.C0355a;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import com.embarcadero.OptimizaCorte.Activities.ActivityRetales;
import com.google.android.material.textfield.TextInputLayout;
import java.nio.MappedByteBuffer;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: S0.v0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class RunnableC0286v0 implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2288j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2289k;

    public /* synthetic */ RunnableC0286v0(int i4, Object obj) {
        this.f2288j = i4;
        this.f2289k = obj;
    }

    /* JADX WARN: Type inference failed for: r0v26, types: [com.embarcadero.OptimizaCorte.Activities.ActivityRetales, android.app.Activity] */
    /* JADX WARN: Type inference failed for: r4v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, android.app.Activity] */
    @Override // java.lang.Runnable
    public final void run() {
        androidx.appcompat.view.menu.h hVar = null;
        switch (this.f2288j) {
            case 0:
                ActivityListaCorte.f fVar = (ActivityListaCorte.f) this.f2289k;
                ?? r4 = ActivityListaCorte.this;
                new V0.c(r4, r4.f3020Z, r4.getString(2131820579), r4.f3022b0).a();
                r4.f3025e0.f2897x = false;
                r4.f3020Z.setVisibility(0);
                r4.f3009O.setImageResource(2131165312);
                r4.f3009O.setOnClickListener(new r(1, fVar));
                Log.d("DIA_BILLING_V5_COPY", "onNotPurchase prefscount: " + r4.getSharedPreferences("cutsettings", 0).getInt("opencountmain", 1));
                if (r4.getSharedPreferences("cutsettings", 0).getInt("opencountmain", 1) % 3 == 0) {
                    Log.d("LC_BILLING_V5_COPY", "onNotPurchase: getPrefsOpenCount() % 3 == 0");
                    if (!r4.isFinishing() && !r4.isDestroyed()) {
                        Log.d("LC_BILLING_V5_COPY", "onNotPurchase: abriendo dialog...");
                        ActivityListaCorte.g gVar = r4.f3031k0;
                        Y0.a b4 = Y0.a.b(r4, gVar);
                        r4.f3027g0 = b4;
                        b4.f2825a = gVar;
                        Y0.b bVar = new Y0.b(r4, b4);
                        Log.d("LC_BILLING_V5_COPY", "onNotPurchase: appPaused? " + r4.f3024d0);
                        if (!r4.f3024d0) {
                            Log.d("LC_BILLING_V5_COPY", "onNotPurchase: abriendo dialog AHORA");
                            r4.runOnUiThread(new A(2, bVar));
                        }
                    }
                }
                Log.d("LC_BILLING_V5_COPY", "onNotPurchase: ");
                return;
            case 1:
                ActivityListaCorte.g gVar2 = (ActivityListaCorte.g) this.f2289k;
                gVar2.getClass();
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                ActivityListaCorte activityListaCorte = ActivityListaCorte.this;
                activityListaCorte.G();
                activityListaCorte.f3025e0.f2897x = true;
                activityListaCorte.f3009O.setOnClickListener(new r(2, gVar2));
                activityListaCorte.F();
                activityListaCorte.f3020Z.setVisibility(8);
                Log.d("DIA_BILLING_V5_COPY", "onPurchase: ");
                super/*android.app.Activity*/.recreate();
                return;
            case 2:
                ActivityOptimizacion activityOptimizacion = ActivityOptimizacion.this;
                activityOptimizacion.f3076V.f2897x = false;
                activityOptimizacion.f3064J.setVisibility(0);
                Log.d("OP_BILLING_V5_COPY", "onNotLogin: ");
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ?? r02 = ActivityRetales.this;
                new V0.c(r02, r02.f3096H).a();
                C0353a c0353a = r02.f3112X;
                c0353a.f2896w = true;
                c0353a.f2897x = false;
                r02.f3096H.setVisibility(0);
                Log.d("INI_BILLING_V5_COPY", "onNotLogin: ");
                return;
            case 4:
                ((TextInputLayout) this.f2289k).m.requestLayout();
                return;
            case 5:
                ComponentActivity.e eVar = (ComponentActivity.e) this.f2289k;
                Runnable runnable = eVar.k;
                if (runnable != null) {
                    runnable.run();
                    eVar.k = null;
                    return;
                }
                return;
            case 6:
                m.b bVar2 = (m.b) this.f2289k;
                synchronized (bVar2.d) {
                    try {
                        if (bVar2.h != null) {
                            try {
                                J.l d4 = bVar2.d();
                                int i4 = d4.f1185e;
                                if (i4 == 2) {
                                    synchronized (bVar2.d) {
                                    }
                                }
                                if (i4 == 0) {
                                    int i5 = I.l.f1141a;
                                    Trace.beginSection("EmojiCompat.FontRequestEmojiCompatConfig.buildTypeface");
                                    m.a aVar = bVar2.c;
                                    Context context = bVar2.a;
                                    aVar.getClass();
                                    Typeface b5 = E.e.f810a.b(context, new J.l[]{d4}, 0);
                                    MappedByteBuffer e4 = E.m.e(bVar2.a, d4.f1181a);
                                    if (e4 != null && b5 != null) {
                                        try {
                                            Trace.beginSection("EmojiCompat.MetadataRepo.create");
                                            androidx.emoji2.text.n nVar = new androidx.emoji2.text.n(b5, C0355a.d(e4));
                                            Trace.endSection();
                                            Trace.endSection();
                                            synchronized (bVar2.d) {
                                                f.i iVar = bVar2.h;
                                                if (iVar != null) {
                                                    iVar.b(nVar);
                                                }
                                            }
                                            bVar2.b();
                                            return;
                                        } catch (Throwable th) {
                                            int i6 = I.l.f1141a;
                                            Trace.endSection();
                                            throw th;
                                        }
                                    }
                                    throw new RuntimeException("Unable to open file.");
                                }
                                throw new RuntimeException("fetchFonts result is not OK. (" + i4 + ")");
                            } catch (Throwable th2) {
                                synchronized (bVar2.d) {
                                    try {
                                        f.i iVar2 = bVar2.h;
                                        if (iVar2 != null) {
                                            iVar2.a(th2);
                                        }
                                        bVar2.b();
                                        return;
                                    } finally {
                                    }
                                }
                            }
                        }
                        return;
                    } finally {
                    }
                }
            default:
                Toolbar.f fVar2 = ((Toolbar) this.f2289k).U;
                if (fVar2 != null) {
                    hVar = fVar2.k;
                }
                if (hVar != null) {
                    hVar.collapseActionView();
                    return;
                }
                return;
        }
    }
}
