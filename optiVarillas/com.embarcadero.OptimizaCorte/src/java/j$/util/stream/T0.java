package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class T0 extends W0 implements F0 {
    @Override // j$.util.stream.L0
    /* renamed from: a */
    public final /* synthetic */ void i(Double[] dArr, int i4) {
        AbstractC0637z0.n(this, dArr, i4);
    }

    @Override // j$.util.stream.K0
    public final Object c(int i4) {
        return new double[i4];
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0637z0.q(this, consumer);
    }

    @Override // j$.util.stream.L0
    /* renamed from: g */
    public final /* synthetic */ F0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.t(this, j4, j5);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.P, j$.util.stream.p1] */
    @Override // j$.util.stream.L0
    public final j$.util.P spliterator() {
        return new AbstractC0593p1(this);
    }

    @Override // j$.util.stream.L0
    public final Spliterator spliterator() {
        return new AbstractC0593p1(this);
    }
}
