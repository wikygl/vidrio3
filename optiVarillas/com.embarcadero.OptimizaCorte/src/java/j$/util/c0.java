package j$.util;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class c0 implements J {

    /* renamed from: a  reason: collision with root package name */
    private final int[] f4124a;

    /* renamed from: b  reason: collision with root package name */
    private int f4125b;

    /* renamed from: c  reason: collision with root package name */
    private final int f4126c;

    /* renamed from: d  reason: collision with root package name */
    private final int f4127d;

    public c0(int[] iArr, int i4, int i5, int i6) {
        this.f4124a = iArr;
        this.f4125b = i4;
        this.f4126c = i5;
        this.f4127d = i6 | 16448;
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return this.f4127d;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        return this.f4126c - this.f4125b;
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        D.b(this, consumer);
    }

    @Override // j$.util.P
    public final void forEachRemaining(IntConsumer intConsumer) {
        int i4;
        intConsumer.getClass();
        int[] iArr = this.f4124a;
        int length = iArr.length;
        int i5 = this.f4126c;
        if (length < i5 || (i4 = this.f4125b) < 0) {
            return;
        }
        this.f4125b = i5;
        if (i4 < i5) {
            do {
                intConsumer.accept(iArr[i4]);
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
        return D.g(this, consumer);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(IntConsumer intConsumer) {
        intConsumer.getClass();
        int i4 = this.f4125b;
        if (i4 < 0 || i4 >= this.f4126c) {
            return false;
        }
        this.f4125b = i4 + 1;
        intConsumer.accept(this.f4124a[i4]);
        return true;
    }

    @Override // j$.util.Spliterator
    public final J trySplit() {
        int i4 = this.f4125b;
        int i5 = (this.f4126c + i4) >>> 1;
        if (i4 >= i5) {
            return null;
        }
        this.f4125b = i5;
        return new c0(this.f4124a, i4, i5, this.f4127d);
    }
}
