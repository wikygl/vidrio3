package V1;

import V1.ComponentCallbacks2C0296b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class r implements ComponentCallbacks2C0296b.a {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ C0298d f2599a;

    public r(C0298d c0298d) {
        this.f2599a = c0298d;
    }

    @Override // V1.ComponentCallbacks2C0296b.a
    public final void a(boolean z4) {
        g2.g gVar = this.f2599a.f2587v;
        gVar.sendMessage(gVar.obtainMessage(1, Boolean.valueOf(z4)));
    }
}
