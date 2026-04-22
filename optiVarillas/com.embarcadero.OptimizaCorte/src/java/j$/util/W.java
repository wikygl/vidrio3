package j$.util;

import java.util.Comparator;
import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class W implements Spliterator {

    /* renamed from: a  reason: collision with root package name */
    private final Object[] f4108a;

    /* renamed from: b  reason: collision with root package name */
    private int f4109b;

    /* renamed from: c  reason: collision with root package name */
    private final int f4110c;

    /* renamed from: d  reason: collision with root package name */
    private final int f4111d;

    public W(Object[] objArr, int i4, int i5, int i6) {
        this.f4108a = objArr;
        this.f4109b = i4;
        this.f4110c = i5;
        this.f4111d = i6 | 16448;
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return this.f4111d;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        return this.f4110c - this.f4109b;
    }

    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        int i4;
        consumer.getClass();
        Object[] objArr = this.f4108a;
        int length = objArr.length;
        int i5 = this.f4110c;
        if (length < i5 || (i4 = this.f4109b) < 0) {
            return;
        }
        this.f4109b = i5;
        if (i4 < i5) {
            do {
                consumer.accept(objArr[i4]);
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
    public final boolean tryAdvance(Consumer consumer) {
        consumer.getClass();
        int i4 = this.f4109b;
        if (i4 < 0 || i4 >= this.f4110c) {
            return false;
        }
        this.f4109b = i4 + 1;
        consumer.accept(this.f4108a[i4]);
        return true;
    }

    @Override // j$.util.Spliterator
    public final Spliterator trySplit() {
        int i4 = this.f4109b;
        int i5 = (this.f4110c + i4) >>> 1;
        if (i4 >= i5) {
            return null;
        }
        this.f4109b = i5;
        return new W(this.f4108a, i4, i5, this.f4111d);
    }
}
