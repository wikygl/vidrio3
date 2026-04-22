package C3;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class W extends a0 {

    /* renamed from: o  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f447o = AtomicIntegerFieldUpdater.newUpdater(W.class, "_invoked");
    private volatile int _invoked;

    /* renamed from: n  reason: collision with root package name */
    public final u3.l<Throwable, l3.g> f448n;

    /* JADX WARN: Multi-variable type inference failed */
    public W(u3.l<? super Throwable, l3.g> lVar) {
        this.f448n = lVar;
    }

    @Override // u3.l
    public final /* bridge */ /* synthetic */ l3.g g(Throwable th) {
        o(th);
        return l3.g.f5271a;
    }

    @Override // C3.AbstractC0164n
    public final void o(Throwable th) {
        if (f447o.compareAndSet(this, 0, 1)) {
            this.f448n.g(th);
        }
    }
}
