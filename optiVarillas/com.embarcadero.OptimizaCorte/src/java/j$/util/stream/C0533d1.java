package j$.util.stream;

import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/* renamed from: j$.util.stream.d1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0533d1 extends AbstractC0543f1 implements J0 {
    @Override // j$.util.stream.L0
    /* renamed from: a */
    public final /* synthetic */ void i(Long[] lArr, int i4) {
        AbstractC0637z0.p(this, lArr, i4);
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
        long[] jArr;
        jArr = AbstractC0637z0.f;
        return jArr;
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0637z0.s(this, consumer);
    }

    @Override // j$.util.stream.AbstractC0543f1, j$.util.stream.L0
    /* renamed from: g */
    public final /* synthetic */ J0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.v(this, j4, j5);
    }

    @Override // j$.util.stream.L0
    public final j$.util.P spliterator() {
        return Spliterators.d();
    }

    @Override // j$.util.stream.L0
    public final Spliterator spliterator() {
        return Spliterators.d();
    }
}
