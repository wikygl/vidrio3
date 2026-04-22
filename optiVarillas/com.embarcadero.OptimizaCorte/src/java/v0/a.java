package V0;

import D1.RunnableC0186f;
import X2.a;
import X2.e;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import i2.C0464j;
import i2.C0467m;
import i2.C0475v;
import i2.G;
import i2.b0;
import i2.r;
import j$.util.Objects;
import p1.d;
import q1.InterfaceC0770b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class a implements e, InterfaceC0770b.a {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ Object f2523j;

    public /* synthetic */ a(Object obj) {
        this.f2523j = obj;
    }

    @Override // q1.InterfaceC0770b.a
    public Object a() {
        return Integer.valueOf(((d) this.f2523j).c());
    }

    @Override // X2.e
    public void c(X2.a aVar) {
        String str;
        final c cVar = (c) this.f2523j;
        cVar.getClass();
        int a4 = c.f.a();
        Activity activity = cVar.f2525a;
        boolean z4 = false;
        if (a4 == 2) {
            Log.d("consentUMP", "loadNonPersonalizedAds: ");
            cVar.c(true);
            if (!cVar.f2529e) {
                cVar.e(true, cVar.f2527c);
            }
            a.InterfaceC0034a interfaceC0034a = new a.InterfaceC0034a() { // from class: V0.b
                @Override // X2.a.InterfaceC0034a
                public final void a() {
                    c.this.d();
                }
            };
            C0467m c0467m = (C0467m) aVar;
            c0467m.getClass();
            Handler handler = G.f3679a;
            if (Looper.myLooper() == Looper.getMainLooper()) {
                if (!c0467m.f3778h.compareAndSet(false, true)) {
                    if (true != c0467m.f3782l) {
                        str = "ConsentForm#show can only be invoked once.";
                    } else {
                        str = "Privacy options form is being loading. Please try again later.";
                    }
                    new b0(str, 3).a();
                    interfaceC0034a.a();
                } else {
                    r rVar = c0467m.f3777g;
                    C0475v c0475v = rVar.f3798k;
                    Objects.requireNonNull(c0475v);
                    rVar.f3797j.post(new RunnableC0186f(4, c0475v));
                    C0464j c0464j = new C0464j(c0467m, activity);
                    c0467m.f3772a.registerActivityLifecycleCallbacks(c0464j);
                    c0467m.f3781k.set(c0464j);
                    c0467m.f3773b.f3802a = activity;
                    Dialog dialog = new Dialog(activity, 16973840);
                    dialog.setContentView(c0467m.f3777g);
                    dialog.setCancelable(false);
                    Window window = dialog.getWindow();
                    if (window == null) {
                        new b0("Activity with null windows is passed in.", 3).a();
                        interfaceC0034a.a();
                    } else {
                        window.setLayout(-1, -1);
                        window.setBackgroundDrawable(new ColorDrawable(0));
                        window.setFlags(16777216, 16777216);
                        c0467m.f3780j.set(interfaceC0034a);
                        dialog.show();
                        c0467m.f = dialog;
                        c0467m.f3777g.a("UMP_messagePresented", "");
                    }
                }
            } else {
                throw new IllegalStateException("Method must be call on main thread.");
            }
        } else if (A3.d.b(activity) && A3.d.c(activity)) {
            Log.d("consentUMP", "loadPersonalizedAds: ");
            cVar.c(false);
            if (!cVar.f2529e) {
                cVar.e(false, cVar.f2527c);
            }
        } else {
            Log.d("consentUMP", "loadForm: reset called");
            cVar.f();
        }
        StringBuilder sb = new StringBuilder("loadForm: isGDPR? ");
        if (PreferenceManager.getDefaultSharedPreferences(activity).getInt("IABTCF_gdprApplies", 0) == 1) {
            z4 = true;
        }
        sb.append(z4);
        Log.d("consentUMP", sb.toString());
        Log.d("consentUMP", "loadForm: canShowAds? " + A3.d.b(activity));
        Log.d("consentUMP", "loadForm: canShowPersonalizedAds? " + A3.d.c(activity));
        Log.d("consentUMP", "onConsentInfoUpdateSuccess: " + c.f.a());
        Log.d("consent", "onConsentFormLoadSuccess CONSENT: " + c.f.a());
    }
}
