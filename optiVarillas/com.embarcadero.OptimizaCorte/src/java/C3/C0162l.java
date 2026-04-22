package C3;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/* renamed from: C3.l  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class C0162l {

    /* renamed from: b  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f489b = AtomicIntegerFieldUpdater.newUpdater(C0162l.class, "_handled");
    private volatile int _handled;

    /* renamed from: a  reason: collision with root package name */
    public final Throwable f490a;

    public C0162l(Throwable th, boolean z4) {
        this.f490a = th;
        this._handled = z4 ? 1 : 0;
    }

    public final String toString() {
        return getClass().getSimpleName() + '[' + this.f490a + ']';
    }
}
