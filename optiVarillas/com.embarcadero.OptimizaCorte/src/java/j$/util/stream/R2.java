package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.Comparator;
import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class R2 implements Spliterator {

    /* renamed from: a  reason: collision with root package name */
    int f4375a;

    /* renamed from: b  reason: collision with root package name */
    final int f4376b;

    /* renamed from: c  reason: collision with root package name */
    int f4377c;

    /* renamed from: d  reason: collision with root package name */
    final int f4378d;

    /* renamed from: e  reason: collision with root package name */
    Object[] f4379e;
    final /* synthetic */ C0520a3 f;

    /* JADX INFO: Access modifiers changed from: package-private */
    public R2(C0520a3 c0520a3, int i4, int i5, int i6, int i7) {
        this.f = c0520a3;
        this.f4375a = i4;
        this.f4376b = i5;
        this.f4377c = i6;
        this.f4378d = i7;
        Object[][] objArr = c0520a3.f;
        this.f4379e = objArr == null ? c0520a3.f4447e : objArr[i4];
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return 16464;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        int i4 = this.f4375a;
        int i5 = this.f4378d;
        int i6 = this.f4376b;
        if (i4 == i6) {
            return i5 - this.f4377c;
        }
        long[] jArr = this.f.f4468d;
        return ((jArr[i6] + i5) - jArr[i4]) - this.f4377c;
    }

    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        C0520a3 c0520a3;
        Objects.requireNonNull(consumer);
        int i4 = this.f4375a;
        int i5 = this.f4378d;
        int i6 = this.f4376b;
        if (i4 < i6 || (i4 == i6 && this.f4377c < i5)) {
            int i7 = this.f4377c;
            while (true) {
                c0520a3 = this.f;
                if (i4 >= i6) {
                    break;
                }
                Object[] objArr = c0520a3.f[i4];
                while (i7 < objArr.length) {
                    consumer.accept(objArr[i7]);
                    i7++;
                }
                i4++;
                i7 = 0;
            }
            Object[] objArr2 = this.f4375a == i6 ? this.f4379e : c0520a3.f[i6];
            while (i7 < i5) {
                consumer.accept(objArr2[i7]);
                i7++;
            }
            this.f4375a = i6;
            this.f4377c = i5;
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
        int i4 = this.f4375a;
        int i5 = this.f4376b;
        if (i4 < i5 || (i4 == i5 && this.f4377c < this.f4378d)) {
            Object[] objArr = this.f4379e;
            int i6 = this.f4377c;
            this.f4377c = i6 + 1;
            consumer.accept(objArr[i6]);
            if (this.f4377c == this.f4379e.length) {
                this.f4377c = 0;
                int i7 = this.f4375a + 1;
                this.f4375a = i7;
                Object[][] objArr2 = this.f.f;
                if (objArr2 != null && i7 <= i5) {
                    this.f4379e = objArr2[i7];
                }
            }
            return true;
        }
        return false;
    }

    @Override // j$.util.Spliterator
    public final Spliterator trySplit() {
        int i4 = this.f4375a;
        int i5 = this.f4376b;
        if (i4 < i5) {
            int i6 = i5 - 1;
            int i7 = this.f4377c;
            C0520a3 c0520a3 = this.f;
            R2 r22 = new R2(c0520a3, i4, i6, i7, c0520a3.f[i6].length);
            this.f4375a = i5;
            this.f4377c = 0;
            this.f4379e = c0520a3.f[i5];
            return r22;
        } else if (i4 == i5) {
            int i8 = this.f4377c;
            int i9 = (this.f4378d - i8) / 2;
            if (i9 == 0) {
                return null;
            }
            Spliterator m4 = Spliterators.m(this.f4379e, i8, i8 + i9);
            this.f4377c += i9;
            return m4;
        } else {
            return null;
        }
    }
}
