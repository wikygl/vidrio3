package i2;

import D1.RunnableC0194n;
import android.app.Application;
import android.app.Dialog;
import android.os.Handler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/* renamed from: i2.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0467m implements X2.a {

    /* renamed from: a  reason: collision with root package name */
    public final Application f3772a;

    /* renamed from: b  reason: collision with root package name */
    public final C0473t f3773b;

    /* renamed from: c  reason: collision with root package name */
    public final C0463i f3774c;

    /* renamed from: d  reason: collision with root package name */
    public final C0470p f3775d;

    /* renamed from: e  reason: collision with root package name */
    public final Z f3776e;
    public Dialog f;

    /* renamed from: g  reason: collision with root package name */
    public r f3777g;

    /* renamed from: h  reason: collision with root package name */
    public final AtomicBoolean f3778h = new AtomicBoolean();

    /* renamed from: i  reason: collision with root package name */
    public final AtomicReference f3779i = new AtomicReference();

    /* renamed from: j  reason: collision with root package name */
    public final AtomicReference f3780j = new AtomicReference();

    /* renamed from: k  reason: collision with root package name */
    public final AtomicReference f3781k = new AtomicReference();

    /* renamed from: l  reason: collision with root package name */
    public boolean f3782l = false;

    public C0467m(Application application, C0473t c0473t, C0463i c0463i, C0470p c0470p, C0472s c0472s) {
        this.f3772a = application;
        this.f3773b = c0473t;
        this.f3774c = c0463i;
        this.f3775d = c0470p;
        this.f3776e = c0472s;
    }

    public final void a(X2.e eVar, X2.d dVar) {
        C0472s c0472s = (C0472s) this.f3776e;
        Handler handler = G.f3679a;
        H.a.t(handler);
        r rVar = new r((C0473t) c0472s.f3800j.a(), handler, ((C0476w) c0472s.f3801k).a());
        this.f3777g = rVar;
        rVar.setBackgroundColor(0);
        rVar.getSettings().setJavaScriptEnabled(true);
        rVar.setWebViewClient(new C0471q(rVar));
        this.f3779i.set(new C0466l(eVar, dVar));
        r rVar2 = this.f3777g;
        C0470p c0470p = this.f3775d;
        rVar2.loadDataWithBaseURL(c0470p.f3793a, c0470p.f3794b, "text/html", "UTF-8", null);
        handler.postDelayed(new RunnableC0194n(1, this), 10000L);
    }

    public final void b() {
        Dialog dialog = this.f;
        if (dialog != null) {
            dialog.dismiss();
            this.f = null;
        }
        this.f3773b.f3802a = null;
        C0464j c0464j = (C0464j) this.f3781k.getAndSet(null);
        if (c0464j != null) {
            c0464j.f3766k.f3772a.unregisterActivityLifecycleCallbacks(c0464j);
        }
    }
}
