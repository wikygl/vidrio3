package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import java.util.Comparator;
import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class E3 extends G3 implements Spliterator, Consumer {
    Object f;

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f = obj;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Spliterator, j$.util.stream.G3] */
    @Override // j$.util.stream.G3
    protected final Spliterator c(Spliterator spliterator) {
        return new G3(spliterator, this);
    }

    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        Objects.requireNonNull(consumer);
        C0575l3 c0575l3 = null;
        while (true) {
            F3 d4 = d();
            if (d4 == F3.NO_MORE) {
                return;
            }
            F3 f32 = F3.MAYBE_MORE;
            Spliterator spliterator = this.f4289a;
            if (d4 != f32) {
                spliterator.forEachRemaining(consumer);
                return;
            }
            int i4 = this.f4291c;
            if (c0575l3 == null) {
                c0575l3 = new C0575l3(i4);
            } else {
                c0575l3.f4546a = 0;
            }
            long j4 = 0;
            while (spliterator.tryAdvance(c0575l3)) {
                j4++;
                if (j4 >= i4) {
                    break;
                }
            }
            if (j4 == 0) {
                return;
            }
            long b4 = b(j4);
            for (int i5 = 0; i5 < b4; i5++) {
                consumer.accept(c0575l3.f4538b[i5]);
            }
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
        Objects.requireNonNull(consumer);
        while (d() != F3.NO_MORE && this.f4289a.tryAdvance(this)) {
            if (b(1L) == 1) {
                consumer.accept(this.f);
                this.f = null;
                return true;
            }
        }
        return false;
    }
}
