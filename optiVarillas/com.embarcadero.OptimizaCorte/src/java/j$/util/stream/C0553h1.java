package j$.util.stream;

import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.h1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public class C0553h1 implements H0 {

    /* renamed from: a  reason: collision with root package name */
    final int[] f4521a;

    /* renamed from: b  reason: collision with root package name */
    int f4522b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0553h1(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4521a = new int[(int) j4];
        this.f4522b = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0553h1(int[] iArr) {
        this.f4521a = iArr;
        this.f4522b = iArr.length;
    }

    @Override // j$.util.stream.K0, j$.util.stream.L0
    public final K0 b(int i4) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.L0
    public final /* bridge */ /* synthetic */ L0 b(int i4) {
        b(i4);
        throw null;
    }

    @Override // j$.util.stream.L0
    public final long count() {
        return this.f4522b;
    }

    @Override // j$.util.stream.K0
    public final void d(Object obj, int i4) {
        int i5 = this.f4522b;
        System.arraycopy(this.f4521a, 0, (int[]) obj, i4, i5);
    }

    @Override // j$.util.stream.K0
    public final Object e() {
        int[] iArr = this.f4521a;
        int length = iArr.length;
        int i4 = this.f4522b;
        return length == i4 ? iArr : Arrays.copyOf(iArr, i4);
    }

    @Override // j$.util.stream.K0
    public final void f(Object obj) {
        IntConsumer intConsumer = (IntConsumer) obj;
        for (int i4 = 0; i4 < this.f4522b; i4++) {
            intConsumer.accept(this.f4521a[i4]);
        }
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0637z0.r(this, consumer);
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ Object[] o(IntFunction intFunction) {
        return AbstractC0637z0.m(this, intFunction);
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ int q() {
        return 0;
    }

    @Override // j$.util.stream.L0
    /* renamed from: r */
    public final /* synthetic */ void i(Integer[] numArr, int i4) {
        AbstractC0637z0.o(this, numArr, i4);
    }

    @Override // j$.util.stream.L0
    /* renamed from: s */
    public final /* synthetic */ H0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.u(this, j4, j5);
    }

    @Override // j$.util.stream.K0, j$.util.stream.L0
    public final j$.util.P spliterator() {
        return Spliterators.k(this.f4521a, 0, this.f4522b);
    }

    @Override // j$.util.stream.L0
    public final Spliterator spliterator() {
        return Spliterators.k(this.f4521a, 0, this.f4522b);
    }

    public String toString() {
        int[] iArr = this.f4521a;
        return String.format("IntArrayNode[%d][%s]", Integer.valueOf(iArr.length - this.f4522b), Arrays.toString(iArr));
    }
}
