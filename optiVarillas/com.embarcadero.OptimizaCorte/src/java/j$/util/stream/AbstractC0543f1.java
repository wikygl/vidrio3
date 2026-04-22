package j$.util.stream;

import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.f1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public abstract class AbstractC0543f1 implements L0 {
    @Override // j$.util.stream.L0
    public L0 b(int i4) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.L0
    public final long count() {
        return 0L;
    }

    public final void d(Object obj, int i4) {
    }

    public final void f(Object obj) {
    }

    @Override // j$.util.stream.L0
    public /* synthetic */ L0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.w(this, j4, j5, intFunction);
    }

    @Override // j$.util.stream.L0
    public final Object[] o(IntFunction intFunction) {
        return (Object[]) intFunction.apply(0);
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ int q() {
        return 0;
    }
}
