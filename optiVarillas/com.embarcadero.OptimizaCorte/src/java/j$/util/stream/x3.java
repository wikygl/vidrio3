package j$.util.stream;

import j$.util.Objects;
import java.util.Comparator;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public abstract class x3 extends z3 implements j$.util.P {
    /* JADX INFO: Access modifiers changed from: package-private */
    public x3(j$.util.P p4, long j4, long j5) {
        super(p4, j4, j5, 0L, Math.min(p4.estimateSize(), j5));
    }

    protected abstract Object b();

    @Override // j$.util.P
    public final void forEachRemaining(Object obj) {
        Objects.requireNonNull(obj);
        long j4 = this.f4642e;
        long j5 = this.f4638a;
        if (j5 >= j4) {
            return;
        }
        long j6 = this.f4641d;
        if (j6 >= j4) {
            return;
        }
        if (j6 >= j5 && ((j$.util.P) this.f4640c).estimateSize() + j6 <= this.f4639b) {
            ((j$.util.P) this.f4640c).forEachRemaining(obj);
            this.f4641d = this.f4642e;
            return;
        }
        while (j5 > this.f4641d) {
            ((j$.util.P) this.f4640c).tryAdvance(b());
            this.f4641d++;
        }
        while (this.f4641d < this.f4642e) {
            ((j$.util.P) this.f4640c).tryAdvance(obj);
            this.f4641d++;
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
        long j4;
        Objects.requireNonNull(obj);
        long j5 = this.f4642e;
        long j6 = this.f4638a;
        if (j6 >= j5) {
            return false;
        }
        while (true) {
            j4 = this.f4641d;
            if (j6 <= j4) {
                break;
            }
            ((j$.util.P) this.f4640c).tryAdvance(b());
            this.f4641d++;
        }
        if (j4 >= this.f4642e) {
            return false;
        }
        this.f4641d = j4 + 1;
        return ((j$.util.P) this.f4640c).tryAdvance(obj);
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
