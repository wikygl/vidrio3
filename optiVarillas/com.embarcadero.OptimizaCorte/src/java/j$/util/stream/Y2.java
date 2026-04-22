package j$.util.stream;

import j$.util.Objects;
import java.util.Comparator;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public abstract class Y2 implements j$.util.P {

    /* renamed from: a  reason: collision with root package name */
    int f4436a;

    /* renamed from: b  reason: collision with root package name */
    final int f4437b;

    /* renamed from: c  reason: collision with root package name */
    int f4438c;

    /* renamed from: d  reason: collision with root package name */
    final int f4439d;

    /* renamed from: e  reason: collision with root package name */
    Object f4440e;
    final /* synthetic */ Z2 f;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Y2(Z2 z22, int i4, int i5, int i6, int i7) {
        this.f = z22;
        this.f4436a = i4;
        this.f4437b = i5;
        this.f4438c = i6;
        this.f4439d = i7;
        Object[] objArr = z22.f;
        this.f4440e = objArr == null ? z22.f4444e : objArr[i4];
    }

    abstract void a(int i4, Object obj, Object obj2);

    abstract j$.util.P b(Object obj, int i4, int i5);

    abstract j$.util.P c(int i4, int i5, int i6, int i7);

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return 16464;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        int i4 = this.f4436a;
        int i5 = this.f4439d;
        int i6 = this.f4437b;
        if (i4 == i6) {
            return i5 - this.f4438c;
        }
        long[] jArr = this.f.f4468d;
        return ((jArr[i6] + i5) - jArr[i4]) - this.f4438c;
    }

    @Override // j$.util.P
    public final void forEachRemaining(Object obj) {
        Z2 z22;
        Objects.requireNonNull(obj);
        int i4 = this.f4436a;
        int i5 = this.f4439d;
        int i6 = this.f4437b;
        if (i4 < i6 || (i4 == i6 && this.f4438c < i5)) {
            int i7 = this.f4438c;
            while (true) {
                z22 = this.f;
                if (i4 >= i6) {
                    break;
                }
                Object obj2 = z22.f[i4];
                z22.r(obj2, i7, z22.s(obj2), obj);
                i4++;
                i7 = 0;
            }
            z22.r(this.f4436a == i6 ? this.f4440e : z22.f[i6], i7, i5, obj);
            this.f4436a = i6;
            this.f4438c = i5;
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
        int i4 = this.f4436a;
        int i5 = this.f4437b;
        if (i4 < i5 || (i4 == i5 && this.f4438c < this.f4439d)) {
            Object obj2 = this.f4440e;
            int i6 = this.f4438c;
            this.f4438c = i6 + 1;
            a(i6, obj2, obj);
            int i7 = this.f4438c;
            Object obj3 = this.f4440e;
            Z2 z22 = this.f;
            if (i7 == z22.s(obj3)) {
                this.f4438c = 0;
                int i8 = this.f4436a + 1;
                this.f4436a = i8;
                Object[] objArr = z22.f;
                if (objArr != null && i8 <= i5) {
                    this.f4440e = objArr[i8];
                }
            }
            return true;
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

    @Override // j$.util.P, j$.util.Spliterator
    public /* bridge */ /* synthetic */ j$.util.G trySplit() {
        return (j$.util.G) trySplit();
    }

    @Override // j$.util.P, j$.util.Spliterator
    public /* bridge */ /* synthetic */ j$.util.J trySplit() {
        return (j$.util.J) trySplit();
    }

    @Override // j$.util.P, j$.util.Spliterator
    public /* bridge */ /* synthetic */ j$.util.M trySplit() {
        return (j$.util.M) trySplit();
    }

    @Override // j$.util.Spliterator
    public final j$.util.P trySplit() {
        int i4 = this.f4436a;
        int i5 = this.f4437b;
        if (i4 < i5) {
            int i6 = i5 - 1;
            int i7 = this.f4438c;
            Z2 z22 = this.f;
            j$.util.P c4 = c(i4, i6, i7, z22.s(z22.f[i6]));
            this.f4436a = i5;
            this.f4438c = 0;
            this.f4440e = z22.f[i5];
            return c4;
        } else if (i4 == i5) {
            int i8 = this.f4438c;
            int i9 = (this.f4439d - i8) / 2;
            if (i9 == 0) {
                return null;
            }
            j$.util.P b4 = b(this.f4440e, i8, i9);
            this.f4438c += i9;
            return b4;
        } else {
            return null;
        }
    }
}
