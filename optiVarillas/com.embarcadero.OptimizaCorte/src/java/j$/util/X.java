package j$.util;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class X implements G {

    /* renamed from: a  reason: collision with root package name */
    private final double[] f4112a;

    /* renamed from: b  reason: collision with root package name */
    private int f4113b;

    /* renamed from: c  reason: collision with root package name */
    private final int f4114c;

    /* renamed from: d  reason: collision with root package name */
    private final int f4115d;

    public X(double[] dArr, int i4, int i5, int i6) {
        this.f4112a = dArr;
        this.f4113b = i4;
        this.f4114c = i5;
        this.f4115d = i6 | 16448;
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return this.f4115d;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        return this.f4114c - this.f4113b;
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        D.a(this, consumer);
    }

    @Override // j$.util.P
    public final void forEachRemaining(DoubleConsumer doubleConsumer) {
        int i4;
        doubleConsumer.getClass();
        double[] dArr = this.f4112a;
        int length = dArr.length;
        int i5 = this.f4114c;
        if (length < i5 || (i4 = this.f4113b) < 0) {
            return;
        }
        this.f4113b = i5;
        if (i4 < i5) {
            do {
                doubleConsumer.accept(dArr[i4]);
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
        return D.f(this, consumer);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(DoubleConsumer doubleConsumer) {
        doubleConsumer.getClass();
        int i4 = this.f4113b;
        if (i4 < 0 || i4 >= this.f4114c) {
            return false;
        }
        this.f4113b = i4 + 1;
        doubleConsumer.accept(this.f4112a[i4]);
        return true;
    }

    @Override // j$.util.Spliterator
    public final G trySplit() {
        int i4 = this.f4113b;
        int i5 = (this.f4114c + i4) >>> 1;
        if (i4 >= i5) {
            return null;
        }
        this.f4113b = i5;
        return new X(this.f4112a, i4, i5, this.f4115d);
    }
}
