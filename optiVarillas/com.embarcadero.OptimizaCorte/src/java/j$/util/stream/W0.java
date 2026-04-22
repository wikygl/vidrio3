package j$.util.stream;

import java.util.function.IntFunction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class W0 extends N0 implements K0 {
    @Override // j$.util.stream.K0
    public final void d(Object obj, int i4) {
        L0 l0 = this.f4335a;
        ((K0) l0).d(obj, i4);
        ((K0) this.f4336b).d(obj, i4 + ((int) ((K0) l0).count()));
    }

    @Override // j$.util.stream.K0
    public final Object e() {
        long count = count();
        if (count < 2147483639) {
            Object c4 = c((int) count);
            d(c4, 0);
            return c4;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    @Override // j$.util.stream.K0
    public final void f(Object obj) {
        ((K0) this.f4335a).f(obj);
        ((K0) this.f4336b).f(obj);
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ Object[] o(IntFunction intFunction) {
        return AbstractC0637z0.m(this, intFunction);
    }

    public final String toString() {
        int i4 = (count() > 32L ? 1 : (count() == 32L ? 0 : -1));
        String name = getClass().getName();
        return i4 < 0 ? String.format("%s[%s.%s]", name, this.f4335a, this.f4336b) : String.format("%s[size=%d]", name, Long.valueOf(count()));
    }
}
