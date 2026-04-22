package B;

import B.i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class f implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ i.a f219j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f220k;

    public f(i.a aVar, Object obj) {
        this.f219j = aVar;
        this.f220k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f219j.f232j = this.f220k;
    }
}
