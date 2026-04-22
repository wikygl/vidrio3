package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class X0 extends N0 {
    @Override // j$.util.stream.L0
    public final void forEach(Consumer consumer) {
        this.f4335a.forEach(consumer);
        this.f4336b.forEach(consumer);
    }

    @Override // j$.util.stream.L0
    public final L0 h(long j4, long j5, IntFunction intFunction) {
        if (j4 == 0 && j5 == count()) {
            return this;
        }
        long count = this.f4335a.count();
        if (j4 >= count) {
            return this.f4336b.h(j4 - count, j5 - count, intFunction);
        } else if (j5 <= count) {
            return this.f4335a.h(j4, j5, intFunction);
        } else {
            return AbstractC0637z0.I(EnumC0545f3.REFERENCE, this.f4335a.h(j4, count, intFunction), this.f4336b.h(0L, j5 - count, intFunction));
        }
    }

    @Override // j$.util.stream.L0
    public final void i(Object[] objArr, int i4) {
        Objects.requireNonNull(objArr);
        L0 l0 = this.f4335a;
        l0.i(objArr, i4);
        this.f4336b.i(objArr, i4 + ((int) l0.count()));
    }

    @Override // j$.util.stream.L0
    public final Object[] o(IntFunction intFunction) {
        long count = count();
        if (count < 2147483639) {
            Object[] objArr = (Object[]) intFunction.apply((int) count);
            i(objArr, 0);
            return objArr;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    @Override // j$.util.stream.L0
    public final Spliterator spliterator() {
        return new AbstractC0593p1(this);
    }

    public final String toString() {
        return count() < 32 ? String.format("ConcNode[%s.%s]", this.f4335a, this.f4336b) : String.format("ConcNode[size=%d]", Long.valueOf(count()));
    }
}
