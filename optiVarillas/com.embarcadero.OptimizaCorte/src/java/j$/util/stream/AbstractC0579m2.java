package j$.util.stream;

import j$.util.Objects;
import java.util.function.Consumer;

/* renamed from: j$.util.stream.m2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public abstract class AbstractC0579m2 implements InterfaceC0599q2 {

    /* renamed from: a  reason: collision with root package name */
    protected final InterfaceC0599q2 f4545a;

    public AbstractC0579m2(InterfaceC0599q2 interfaceC0599q2) {
        this.f4545a = (InterfaceC0599q2) Objects.requireNonNull(interfaceC0599q2);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(double d4) {
        AbstractC0637z0.a();
        throw null;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(int i4) {
        AbstractC0637z0.k();
        throw null;
    }

    @Override // j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final /* synthetic */ void accept(long j4) {
        AbstractC0637z0.l();
        throw null;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public void k() {
        this.f4545a.k();
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public void l(long j4) {
        this.f4545a.l(j4);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public boolean n() {
        return this.f4545a.n();
    }
}
