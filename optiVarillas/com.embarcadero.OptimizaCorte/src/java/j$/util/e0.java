package j$.util;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class e0 implements M {

    /* renamed from: a  reason: collision with root package name */
    private final long[] f4204a;

    /* renamed from: b  reason: collision with root package name */
    private int f4205b;

    /* renamed from: c  reason: collision with root package name */
    private final int f4206c;

    /* renamed from: d  reason: collision with root package name */
    private final int f4207d;

    public e0(long[] jArr, int i4, int i5, int i6) {
        this.f4204a = jArr;
        this.f4205b = i4;
        this.f4206c = i5;
        this.f4207d = i6 | 16448;
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return this.f4207d;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        return this.f4206c - this.f4205b;
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        D.c(this, consumer);
    }

    @Override // j$.util.P
    public final void forEachRemaining(LongConsumer longConsumer) {
        int i4;
        longConsumer.getClass();
        long[] jArr = this.f4204a;
        int length = jArr.length;
        int i5 = this.f4206c;
        if (length < i5 || (i4 = this.f4205b) < 0) {
            return;
        }
        this.f4205b = i5;
        if (i4 < i5) {
            do {
                longConsumer.accept(jArr[i4]);
                i4++;
            } while (i4 < i5);
        }
    }

    @Override // j$.util.Spliterator
    public final Comparator getComparator() {
        if (D.e(this, 4)) {
            return null;
        }
        throw new IllegalStateException();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return D.d(this);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return D.e(this, i4);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return D.h(this, consumer);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(LongConsumer longConsumer) {
        longConsumer.getClass();
        int i4 = this.f4205b;
        if (i4 < 0 || i4 >= this.f4206c) {
            return false;
        }
        this.f4205b = i4 + 1;
        longConsumer.accept(this.f4204a[i4]);
        return true;
    }

    @Override // j$.util.Spliterator
    public final M trySplit() {
        int i4 = this.f4205b;
        int i5 = (this.f4206c + i4) >>> 1;
        if (i4 >= i5) {
            return null;
        }
        this.f4205b = i5;
        return new e0(this.f4204a, i4, i5, this.f4207d);
    }
}
