package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import java.util.Comparator;
import java.util.function.Consumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class y3 extends z3 implements Spliterator {
    /* JADX INFO: Access modifiers changed from: package-private */
    public y3(Spliterator spliterator, long j4, long j5) {
        super(spliterator, j4, j5, 0L, Math.min(spliterator.estimateSize(), j5));
    }

    /* JADX WARN: Type inference failed for: r10v0, types: [j$.util.stream.z3, j$.util.Spliterator] */
    @Override // j$.util.stream.z3
    protected final Spliterator a(Spliterator spliterator, long j4, long j5, long j6, long j7) {
        return new z3(spliterator, j4, j5, j6, j7);
    }

    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        Objects.requireNonNull(consumer);
        long j4 = this.f4642e;
        long j5 = this.f4638a;
        if (j5 >= j4) {
            return;
        }
        long j6 = this.f4641d;
        if (j6 >= j4) {
            return;
        }
        if (j6 >= j5 && this.f4640c.estimateSize() + j6 <= this.f4639b) {
            this.f4640c.forEachRemaining(consumer);
            this.f4641d = this.f4642e;
            return;
        }
        while (j5 > this.f4641d) {
            this.f4640c.tryAdvance(new C0537e0(9));
            this.f4641d++;
        }
        while (this.f4641d < this.f4642e) {
            this.f4640c.tryAdvance(consumer);
            this.f4641d++;
        }
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

    @Override // j$.util.Spliterator
    public final boolean tryAdvance(Consumer consumer) {
        long j4;
        Objects.requireNonNull(consumer);
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
            this.f4640c.tryAdvance(new C0537e0(8));
            this.f4641d++;
        }
        if (j4 >= this.f4642e) {
            return false;
        }
        this.f4641d = j4 + 1;
        return this.f4640c.tryAdvance(consumer);
    }
}
