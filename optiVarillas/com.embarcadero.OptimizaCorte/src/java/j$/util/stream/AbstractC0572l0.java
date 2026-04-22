package j$.util.stream;

import j$.util.C0508k;
import j$.util.C0510m;
import j$.util.C0512o;
import j$.util.Objects;
import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

/* renamed from: j$.util.stream.l0 */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class AbstractC0572l0 extends AbstractC0521b implements InterfaceC0587o0 {
    public static /* bridge */ /* synthetic */ j$.util.M U(Spliterator spliterator) {
        return V(spliterator);
    }

    public static j$.util.M V(Spliterator spliterator) {
        if (spliterator instanceof j$.util.M) {
            return (j$.util.M) spliterator;
        }
        if (N3.f4343a) {
            N3.a(AbstractC0521b.class, "using LongStream.adapt(Spliterator<Long> s)");
            throw null;
        }
        throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
    }

    @Override // j$.util.stream.AbstractC0521b
    final L0 B(AbstractC0521b abstractC0521b, Spliterator spliterator, boolean z4, IntFunction intFunction) {
        return AbstractC0637z0.H(abstractC0521b, spliterator, z4);
    }

    @Override // j$.util.stream.AbstractC0521b
    final boolean D(Spliterator spliterator, InterfaceC0599q2 interfaceC0599q2) {
        LongConsumer c0532d0;
        boolean n4;
        j$.util.M V3 = V(spliterator);
        if (interfaceC0599q2 instanceof LongConsumer) {
            c0532d0 = (LongConsumer) interfaceC0599q2;
        } else if (N3.f4343a) {
            N3.a(AbstractC0521b.class, "using LongStream.adapt(Sink<Long> s)");
            throw null;
        } else {
            Objects.requireNonNull(interfaceC0599q2);
            c0532d0 = new C0532d0(interfaceC0599q2);
        }
        do {
            n4 = interfaceC0599q2.n();
            if (n4) {
                break;
            }
        } while (V3.tryAdvance(c0532d0));
        return n4;
    }

    @Override // j$.util.stream.AbstractC0521b
    public final EnumC0545f3 E() {
        return EnumC0545f3.LONG_VALUE;
    }

    @Override // j$.util.stream.AbstractC0521b
    public final D0 J(long j4, IntFunction intFunction) {
        return AbstractC0637z0.V(j4);
    }

    @Override // j$.util.stream.AbstractC0521b
    final Spliterator Q(AbstractC0521b abstractC0521b, Supplier supplier, boolean z4) {
        return new AbstractC0550g3(abstractC0521b, supplier, z4);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final InterfaceC0587o0 a() {
        Objects.requireNonNull(null);
        return new C0628x(this, EnumC0540e3.f4495t, 5);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final F asDoubleStream() {
        return new C0620v(this, EnumC0540e3.f4489n, 5);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final C0510m average() {
        long[] jArr = (long[]) collect(new r(27), new r(28), new r(29));
        long j4 = jArr[0];
        return j4 > 0 ? C0510m.d(jArr[1] / j4) : C0510m.a();
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final InterfaceC0587o0 b() {
        Objects.requireNonNull(null);
        return new C0628x(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, 3);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final Stream boxed() {
        return new C0615u(this, 0, new r(26), 2);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final InterfaceC0587o0 c(C0516a c0516a) {
        Objects.requireNonNull(c0516a);
        return new C0557i0(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n | EnumC0540e3.f4495t, c0516a, 0);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final Object collect(Supplier supplier, ObjLongConsumer objLongConsumer, BiConsumer biConsumer) {
        Objects.requireNonNull(biConsumer);
        C0605s c0605s = new C0605s(biConsumer, 2);
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(objLongConsumer);
        Objects.requireNonNull(c0605s);
        return z(new F1(EnumC0545f3.LONG_VALUE, (BinaryOperator) c0605s, (Object) objLongConsumer, supplier, 0));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final long count() {
        return ((Long) z(new H1(0))).longValue();
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final InterfaceC0587o0 distinct() {
        return ((AbstractC0559i2) ((AbstractC0559i2) boxed()).distinct()).mapToLong(new r(23));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final C0512o findAny() {
        return (C0512o) z(J.f4309d);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final C0512o findFirst() {
        return (C0512o) z(J.f4308c);
    }

    public void forEach(LongConsumer longConsumer) {
        Objects.requireNonNull(longConsumer);
        z(new P(longConsumer, false));
    }

    public void forEachOrdered(LongConsumer longConsumer) {
        Objects.requireNonNull(longConsumer);
        z(new P(longConsumer, true));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final F h() {
        Objects.requireNonNull(null);
        return new C0620v(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, 6);
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final j$.util.A iterator() {
        return Spliterators.h(spliterator());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final boolean j() {
        return ((Boolean) z(AbstractC0637z0.c0(EnumC0625w0.NONE))).booleanValue();
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final InterfaceC0587o0 limit(long j4) {
        if (j4 >= 0) {
            return AbstractC0637z0.b0(this, 0L, j4);
        }
        throw new IllegalArgumentException(Long.toString(j4));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final Stream mapToObj(LongFunction longFunction) {
        Objects.requireNonNull(longFunction);
        return new C0615u(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, longFunction, 2);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final C0512o max() {
        return reduce(new C0537e0(0));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final C0512o min() {
        return reduce(new r(22));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final boolean n() {
        return ((Boolean) z(AbstractC0637z0.c0(EnumC0625w0.ALL))).booleanValue();
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final InterfaceC0587o0 peek(LongConsumer longConsumer) {
        Objects.requireNonNull(longConsumer);
        return new C0557i0(this, longConsumer);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final long reduce(long j4, LongBinaryOperator longBinaryOperator) {
        Objects.requireNonNull(longBinaryOperator);
        return ((Long) z(new B1(EnumC0545f3.LONG_VALUE, longBinaryOperator, j4))).longValue();
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final C0512o reduce(LongBinaryOperator longBinaryOperator) {
        Objects.requireNonNull(longBinaryOperator);
        return (C0512o) z(new D1(EnumC0545f3.LONG_VALUE, longBinaryOperator, 0));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final boolean s() {
        return ((Boolean) z(AbstractC0637z0.c0(EnumC0625w0.ANY))).booleanValue();
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final InterfaceC0587o0 skip(long j4) {
        int i4 = (j4 > 0L ? 1 : (j4 == 0L ? 0 : -1));
        if (i4 >= 0) {
            return i4 == 0 ? this : AbstractC0637z0.b0(this, j4, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j4));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final InterfaceC0587o0 sorted() {
        return new AbstractC0567k0(this, EnumC0540e3.f4492q | EnumC0540e3.f4490o, 0);
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h
    public final j$.util.M spliterator() {
        return V(super.spliterator());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final long sum() {
        return reduce(0L, new C0537e0(1));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final C0508k summaryStatistics() {
        return (C0508k) collect(new C0571l(22), new r(21), new r(24));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final IntStream t() {
        Objects.requireNonNull(null);
        return new C0624w(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, 4);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final long[] toArray() {
        return (long[]) AbstractC0637z0.Q((J0) A(new r(25))).e();
    }
}
