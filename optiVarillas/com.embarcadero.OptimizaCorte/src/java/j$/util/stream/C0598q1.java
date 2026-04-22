package j$.util.stream;

import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.q1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public class C0598q1 implements J0 {

    /* renamed from: a  reason: collision with root package name */
    final long[] f4570a;

    /* renamed from: b  reason: collision with root package name */
    int f4571b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0598q1(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4570a = new long[(int) j4];
        this.f4571b = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0598q1(long[] jArr) {
        this.f4570a = jArr;
        this.f4571b = jArr.length;
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
        return this.f4571b;
    }

    @Override // j$.util.stream.K0
    public final void d(Object obj, int i4) {
        int i5 = this.f4571b;
        System.arraycopy(this.f4570a, 0, (long[]) obj, i4, i5);
    }

    @Override // j$.util.stream.K0
    public final Object e() {
        long[] jArr = this.f4570a;
        int length = jArr.length;
        int i4 = this.f4571b;
        return length == i4 ? jArr : Arrays.copyOf(jArr, i4);
    }

    @Override // j$.util.stream.K0
    public final void f(Object obj) {
        LongConsumer longConsumer = (LongConsumer) obj;
        for (int i4 = 0; i4 < this.f4571b; i4++) {
            longConsumer.accept(this.f4570a[i4]);
        }
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0637z0.s(this, consumer);
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
    public final /* synthetic */ void i(Long[] lArr, int i4) {
        AbstractC0637z0.p(this, lArr, i4);
    }

    @Override // j$.util.stream.L0
    /* renamed from: s */
    public final /* synthetic */ J0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.v(this, j4, j5);
    }

    @Override // j$.util.stream.K0, j$.util.stream.L0
    public final j$.util.P spliterator() {
        return Spliterators.l(this.f4570a, 0, this.f4571b);
    }

    @Override // j$.util.stream.L0
    public final Spliterator spliterator() {
        return Spliterators.l(this.f4570a, 0, this.f4571b);
    }

    public String toString() {
        long[] jArr = this.f4570a;
        return String.format("LongArrayNode[%d][%s]", Integer.valueOf(jArr.length - this.f4571b), Arrays.toString(jArr));
    }
}
