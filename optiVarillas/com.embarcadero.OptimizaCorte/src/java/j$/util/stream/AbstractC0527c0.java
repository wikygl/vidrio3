package j$.util.stream;

import j$.util.C0506i;
import j$.util.C0510m;
import j$.util.C0511n;
import j$.util.InterfaceC0643w;
import j$.util.Objects;
import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

/* renamed from: j$.util.stream.c0 */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class AbstractC0527c0 extends AbstractC0521b implements IntStream {
    public static /* bridge */ /* synthetic */ j$.util.J U(Spliterator spliterator) {
        return V(spliterator);
    }

    public static j$.util.J V(Spliterator spliterator) {
        if (spliterator instanceof j$.util.J) {
            return (j$.util.J) spliterator;
        }
        if (N3.f4343a) {
            N3.a(AbstractC0521b.class, "using IntStream.adapt(Spliterator<Integer> s)");
            throw null;
        }
        throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
    }

    @Override // j$.util.stream.AbstractC0521b
    final L0 B(AbstractC0521b abstractC0521b, Spliterator spliterator, boolean z4, IntFunction intFunction) {
        return AbstractC0637z0.G(abstractC0521b, spliterator, z4);
    }

    @Override // j$.util.stream.AbstractC0521b
    final boolean D(Spliterator spliterator, InterfaceC0599q2 interfaceC0599q2) {
        IntConsumer v4;
        boolean n4;
        j$.util.J V3 = V(spliterator);
        if (interfaceC0599q2 instanceof IntConsumer) {
            v4 = (IntConsumer) interfaceC0599q2;
        } else if (N3.f4343a) {
            N3.a(AbstractC0521b.class, "using IntStream.adapt(Sink<Integer> s)");
            throw null;
        } else {
            Objects.requireNonNull(interfaceC0599q2);
            v4 = new V(interfaceC0599q2);
        }
        do {
            n4 = interfaceC0599q2.n();
            if (n4) {
                break;
            }
        } while (V3.tryAdvance(v4));
        return n4;
    }

    @Override // j$.util.stream.AbstractC0521b
    public final EnumC0545f3 E() {
        return EnumC0545f3.INT_VALUE;
    }

    @Override // j$.util.stream.AbstractC0521b
    public final D0 J(long j4, IntFunction intFunction) {
        return AbstractC0637z0.T(j4);
    }

    @Override // j$.util.stream.AbstractC0521b
    final Spliterator Q(AbstractC0521b abstractC0521b, Supplier supplier, boolean z4) {
        return new AbstractC0550g3(abstractC0521b, supplier, z4);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream a() {
        Objects.requireNonNull(null);
        return new C0624w(this, EnumC0540e3.f4495t, 3);
    }

    @Override // j$.util.stream.IntStream
    public final F asDoubleStream() {
        return new C0620v(this, 0, 3);
    }

    @Override // j$.util.stream.IntStream
    public final InterfaceC0587o0 asLongStream() {
        return new C0628x(this, 0, 1);
    }

    @Override // j$.util.stream.IntStream
    public final C0510m average() {
        long[] jArr = (long[]) collect(new r(18), new r(19), new r(20));
        long j4 = jArr[0];
        return j4 > 0 ? C0510m.d(jArr[1] / j4) : C0510m.a();
    }

    @Override // j$.util.stream.IntStream
    public final IntStream b() {
        Objects.requireNonNull(null);
        return new C0624w(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, 1);
    }

    @Override // j$.util.stream.IntStream
    public final Stream boxed() {
        return new C0615u(this, 0, new r(12), 1);
    }

    @Override // j$.util.stream.IntStream
    public final Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
        Objects.requireNonNull(biConsumer);
        C0605s c0605s = new C0605s(biConsumer, 1);
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(objIntConsumer);
        Objects.requireNonNull(c0605s);
        return z(new F1(EnumC0545f3.INT_VALUE, (BinaryOperator) c0605s, (Object) objIntConsumer, supplier, 4));
    }

    @Override // j$.util.stream.IntStream
    public final long count() {
        return ((Long) z(new H1(3))).longValue();
    }

    @Override // j$.util.stream.IntStream
    public final F d() {
        Objects.requireNonNull(null);
        return new C0620v(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, 4);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream distinct() {
        return ((AbstractC0559i2) ((AbstractC0559i2) boxed()).distinct()).mapToInt(new r(11));
    }

    @Override // j$.util.stream.IntStream
    public final boolean e() {
        return ((Boolean) z(AbstractC0637z0.a0(EnumC0625w0.NONE))).booleanValue();
    }

    @Override // j$.util.stream.IntStream
    public final C0511n findAny() {
        return (C0511n) z(I.f4301d);
    }

    @Override // j$.util.stream.IntStream
    public final C0511n findFirst() {
        return (C0511n) z(I.f4300c);
    }

    public void forEach(IntConsumer intConsumer) {
        Objects.requireNonNull(intConsumer);
        z(new O(intConsumer, false));
    }

    public void forEachOrdered(IntConsumer intConsumer) {
        Objects.requireNonNull(intConsumer);
        z(new O(intConsumer, true));
    }

    @Override // j$.util.stream.IntStream
    public final InterfaceC0587o0 i() {
        Objects.requireNonNull(null);
        return new C0628x(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, 2);
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final InterfaceC0643w iterator() {
        return Spliterators.g(spliterator());
    }

    @Override // j$.util.stream.IntStream
    public final IntStream limit(long j4) {
        if (j4 >= 0) {
            return AbstractC0637z0.Z(this, 0L, j4);
        }
        throw new IllegalArgumentException(Long.toString(j4));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream m(R0 r02) {
        Objects.requireNonNull(r02);
        return new X(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n | EnumC0540e3.f4495t, r02, 1);
    }

    @Override // j$.util.stream.IntStream
    public final Stream mapToObj(IntFunction intFunction) {
        Objects.requireNonNull(intFunction);
        return new C0615u(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, intFunction, 1);
    }

    @Override // j$.util.stream.IntStream
    public final C0511n max() {
        return reduce(new r(17));
    }

    @Override // j$.util.stream.IntStream
    public final C0511n min() {
        return reduce(new r(13));
    }

    @Override // j$.util.stream.IntStream
    public final boolean o() {
        return ((Boolean) z(AbstractC0637z0.a0(EnumC0625w0.ANY))).booleanValue();
    }

    @Override // j$.util.stream.IntStream
    public final IntStream peek(IntConsumer intConsumer) {
        Objects.requireNonNull(intConsumer);
        return new X(this, intConsumer);
    }

    @Override // j$.util.stream.IntStream
    public final boolean r() {
        return ((Boolean) z(AbstractC0637z0.a0(EnumC0625w0.ALL))).booleanValue();
    }

    @Override // j$.util.stream.IntStream
    public final int reduce(int i4, IntBinaryOperator intBinaryOperator) {
        Objects.requireNonNull(intBinaryOperator);
        return ((Integer) z(new Q1(EnumC0545f3.INT_VALUE, intBinaryOperator, i4))).intValue();
    }

    @Override // j$.util.stream.IntStream
    public final C0511n reduce(IntBinaryOperator intBinaryOperator) {
        Objects.requireNonNull(intBinaryOperator);
        return (C0511n) z(new D1(EnumC0545f3.INT_VALUE, intBinaryOperator, 3));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream skip(long j4) {
        int i4 = (j4 > 0L ? 1 : (j4 == 0L ? 0 : -1));
        if (i4 >= 0) {
            return i4 == 0 ? this : AbstractC0637z0.Z(this, j4, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j4));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream sorted() {
        return new AbstractC0522b0(this, EnumC0540e3.f4492q | EnumC0540e3.f4490o, 0);
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h
    public final j$.util.J spliterator() {
        return V(super.spliterator());
    }

    @Override // j$.util.stream.IntStream
    public final int sum() {
        return reduce(0, new r(16));
    }

    @Override // j$.util.stream.IntStream
    public final C0506i summaryStatistics() {
        return (C0506i) collect(new C0571l(21), new r(14), new r(15));
    }

    @Override // j$.util.stream.IntStream
    public final int[] toArray() {
        return (int[]) AbstractC0637z0.P((H0) A(new r(10))).e();
    }
}
