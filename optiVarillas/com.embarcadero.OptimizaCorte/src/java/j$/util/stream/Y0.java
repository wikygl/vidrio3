package j$.util.stream;

import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public class Y0 implements F0 {

    /* renamed from: a  reason: collision with root package name */
    final double[] f4434a;

    /* renamed from: b  reason: collision with root package name */
    int f4435b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Y0(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4434a = new double[(int) j4];
        this.f4435b = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Y0(double[] dArr) {
        this.f4434a = dArr;
        this.f4435b = dArr.length;
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
        return this.f4435b;
    }

    @Override // j$.util.stream.K0
    public final void d(Object obj, int i4) {
        int i5 = this.f4435b;
        System.arraycopy(this.f4434a, 0, (double[]) obj, i4, i5);
    }

    @Override // j$.util.stream.K0
    public final Object e() {
        double[] dArr = this.f4434a;
        int length = dArr.length;
        int i4 = this.f4435b;
        return length == i4 ? dArr : Arrays.copyOf(dArr, i4);
    }

    @Override // j$.util.stream.K0
    public final void f(Object obj) {
        DoubleConsumer doubleConsumer = (DoubleConsumer) obj;
        for (int i4 = 0; i4 < this.f4435b; i4++) {
            doubleConsumer.accept(this.f4434a[i4]);
        }
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0637z0.q(this, consumer);
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
    public final /* synthetic */ void i(Double[] dArr, int i4) {
        AbstractC0637z0.n(this, dArr, i4);
    }

    @Override // j$.util.stream.L0
    /* renamed from: s */
    public final /* synthetic */ F0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.t(this, j4, j5);
    }

    @Override // j$.util.stream.K0, j$.util.stream.L0
    public final j$.util.P spliterator() {
        return Spliterators.j(this.f4434a, 0, this.f4435b);
    }

    @Override // j$.util.stream.L0
    public final Spliterator spliterator() {
        return Spliterators.j(this.f4434a, 0, this.f4435b);
    }

    public String toString() {
        double[] dArr = this.f4434a;
        return String.format("DoubleArrayNode[%d][%s]", Integer.valueOf(dArr.length - this.f4435b), Arrays.toString(dArr));
    }
}
