package j$.util.stream;

import java.util.function.Consumer;

/* renamed from: j$.util.stream.b2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class AbstractC0524b2 extends W1 implements V1 {

    /* renamed from: b  reason: collision with root package name */
    long f4459b;

    public /* synthetic */ void accept(double d4) {
        AbstractC0637z0.a();
        throw null;
    }

    public /* synthetic */ void accept(int i4) {
        AbstractC0637z0.k();
        throw null;
    }

    public /* synthetic */ void accept(long j4) {
        AbstractC0637z0.l();
        throw null;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4459b = 0L;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }
}
