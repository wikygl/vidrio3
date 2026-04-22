package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import java.util.Comparator;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class D3 extends G3 implements j$.util.P {
    protected abstract void e(Object obj);

    protected abstract AbstractC0570k3 f(int i4);

    @Override // j$.util.P
    public final void forEachRemaining(Object obj) {
        Objects.requireNonNull(obj);
        AbstractC0570k3 abstractC0570k3 = null;
        while (true) {
            F3 d4 = d();
            if (d4 == F3.NO_MORE) {
                return;
            }
            F3 f32 = F3.MAYBE_MORE;
            Spliterator spliterator = this.f4289a;
            if (d4 != f32) {
                ((j$.util.P) spliterator).forEachRemaining(obj);
                return;
            }
            int i4 = this.f4291c;
            if (abstractC0570k3 == null) {
                abstractC0570k3 = f(i4);
            } else {
                abstractC0570k3.f4535b = 0;
            }
            long j4 = 0;
            while (((j$.util.P) spliterator).tryAdvance(abstractC0570k3)) {
                j4++;
                if (j4 >= i4) {
                    break;
                }
            }
            if (j4 == 0) {
                return;
            }
            abstractC0570k3.b(obj, b(j4));
        }
    }

    public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        forEachRemaining((Object) doubleConsumer);
    }

    public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        forEachRemaining((Object) intConsumer);
    }

    public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        forEachRemaining((Object) longConsumer);
    }

    @Override // j$.util.Spliterator
    public final Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return j$.util.D.d(this);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return j$.util.D.e(this, i4);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(Object obj) {
        Objects.requireNonNull(obj);
        while (d() != F3.NO_MORE && ((j$.util.P) this.f4289a).tryAdvance(this)) {
            if (b(1L) == 1) {
                e(obj);
                return true;
            }
        }
        return false;
    }

    public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
        return tryAdvance((Object) doubleConsumer);
    }

    public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return tryAdvance((Object) intConsumer);
    }

    public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
        return tryAdvance((Object) longConsumer);
    }
}
