package j$.util.stream;

import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/* renamed from: j$.util.stream.b1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0523b1 extends AbstractC0543f1 implements F0 {
    @Override // j$.util.stream.L0
    /* renamed from: a */
    public final /* synthetic */ void i(Double[] dArr, int i4) {
        AbstractC0637z0.n(this, dArr, i4);
    }

    @Override // j$.util.stream.AbstractC0543f1, j$.util.stream.L0
    public final K0 b(int i4) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.AbstractC0543f1, j$.util.stream.L0
    public final /* bridge */ /* synthetic */ L0 b(int i4) {
        b(i4);
        throw null;
    }

    @Override // j$.util.stream.K0
    public final Object e() {
        double[] dArr;
        dArr = AbstractC0637z0.f4636g;
        return dArr;
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0637z0.q(this, consumer);
    }

    @Override // j$.util.stream.AbstractC0543f1, j$.util.stream.L0
    /* renamed from: g */
    public final /* synthetic */ F0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.t(this, j4, j5);
    }

    @Override // j$.util.stream.L0
    public final j$.util.P spliterator() {
        return Spliterators.b();
    }

    @Override // j$.util.stream.L0
    public final Spliterator spliterator() {
        return Spliterators.b();
    }
}
