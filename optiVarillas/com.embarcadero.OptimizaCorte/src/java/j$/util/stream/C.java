package j$.util.stream;

import j$.util.C0505h;
import j$.util.C0510m;
import j$.util.InterfaceC0515s;
import j$.util.Objects;
import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class C extends AbstractC0521b implements F {
    public static /* bridge */ /* synthetic */ j$.util.G U(Spliterator spliterator) {
        return V(spliterator);
    }

    public static j$.util.G V(Spliterator spliterator) {
        if (spliterator instanceof j$.util.G) {
            return (j$.util.G) spliterator;
        }
        if (N3.f4343a) {
            N3.a(AbstractC0521b.class, "using DoubleStream.adapt(Spliterator<Double> s)");
            throw null;
        }
        throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
    }

    @Override // j$.util.stream.AbstractC0521b
    final L0 B(AbstractC0521b abstractC0521b, Spliterator spliterator, boolean z4, IntFunction intFunction) {
        return AbstractC0637z0.F(abstractC0521b, spliterator, z4);
    }

    @Override // j$.util.stream.AbstractC0521b
    final boolean D(Spliterator spliterator, InterfaceC0599q2 interfaceC0599q2) {
        DoubleConsumer c0596q;
        boolean n4;
        j$.util.G V3 = V(spliterator);
        if (interfaceC0599q2 instanceof DoubleConsumer) {
            c0596q = (DoubleConsumer) interfaceC0599q2;
        } else if (N3.f4343a) {
            N3.a(AbstractC0521b.class, "using DoubleStream.adapt(Sink<Double> s)");
            throw null;
        } else {
            Objects.requireNonNull(interfaceC0599q2);
            c0596q = new C0596q(interfaceC0599q2);
        }
        do {
            n4 = interfaceC0599q2.n();
            if (n4) {
                break;
            }
        } while (V3.tryAdvance(c0596q));
        return n4;
    }

    @Override // j$.util.stream.AbstractC0521b
    public final EnumC0545f3 E() {
        return EnumC0545f3.DOUBLE_VALUE;
    }

    @Override // j$.util.stream.AbstractC0521b
    public final D0 J(long j4, IntFunction intFunction) {
        return AbstractC0637z0.J(j4);
    }

    @Override // j$.util.stream.AbstractC0521b
    final Spliterator Q(AbstractC0521b abstractC0521b, Supplier supplier, boolean z4) {
        return new AbstractC0550g3(abstractC0521b, supplier, z4);
    }

    @Override // j$.util.stream.F
    public final F a() {
        Objects.requireNonNull(null);
        return new C0620v(this, EnumC0540e3.f4495t, 2);
    }

    @Override // j$.util.stream.F
    public final C0510m average() {
        double[] dArr = (double[]) collect(new C0571l(27), new C0571l(3), new C0571l(4));
        if (dArr[2] > 0.0d) {
            Set set = Collectors.f4262a;
            double d4 = dArr[0] + dArr[1];
            double d5 = dArr[dArr.length - 1];
            if (Double.isNaN(d4) && Double.isInfinite(d5)) {
                d4 = d5;
            }
            return C0510m.d(d4 / dArr[2]);
        }
        return C0510m.a();
    }

    @Override // j$.util.stream.F
    public final F b() {
        Objects.requireNonNull(null);
        return new C0620v(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, 0);
    }

    @Override // j$.util.stream.F
    public final Stream boxed() {
        return new C0615u(this, 0, new r(0), 0);
    }

    @Override // j$.util.stream.F
    public final F c(C0516a c0516a) {
        Objects.requireNonNull(c0516a);
        return new C0636z(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n | EnumC0540e3.f4495t, c0516a, 0);
    }

    @Override // j$.util.stream.F
    public final Object collect(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BiConsumer biConsumer) {
        Objects.requireNonNull(biConsumer);
        C0605s c0605s = new C0605s(biConsumer, 0);
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(objDoubleConsumer);
        Objects.requireNonNull(c0605s);
        return z(new F1(EnumC0545f3.DOUBLE_VALUE, (BinaryOperator) c0605s, (Object) objDoubleConsumer, supplier, 1));
    }

    @Override // j$.util.stream.F
    public final long count() {
        return ((Long) z(new H1(1))).longValue();
    }

    @Override // j$.util.stream.F
    public final F distinct() {
        return ((AbstractC0559i2) ((AbstractC0559i2) boxed()).distinct()).mapToDouble(new r(1));
    }

    @Override // j$.util.stream.F
    public final boolean f() {
        return ((Boolean) z(AbstractC0637z0.Y(EnumC0625w0.ALL))).booleanValue();
    }

    @Override // j$.util.stream.F
    public final C0510m findAny() {
        return (C0510m) z(H.f4295d);
    }

    @Override // j$.util.stream.F
    public final C0510m findFirst() {
        return (C0510m) z(H.f4294c);
    }

    public void forEach(DoubleConsumer doubleConsumer) {
        Objects.requireNonNull(doubleConsumer);
        z(new N(doubleConsumer, false));
    }

    public void forEachOrdered(DoubleConsumer doubleConsumer) {
        Objects.requireNonNull(doubleConsumer);
        z(new N(doubleConsumer, true));
    }

    @Override // j$.util.stream.F
    public final InterfaceC0587o0 g() {
        Objects.requireNonNull(null);
        return new C0628x(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, 0);
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final InterfaceC0515s iterator() {
        return Spliterators.f(spliterator());
    }

    @Override // j$.util.stream.F
    public final boolean l() {
        return ((Boolean) z(AbstractC0637z0.Y(EnumC0625w0.ANY))).booleanValue();
    }

    @Override // j$.util.stream.F
    public final F limit(long j4) {
        if (j4 >= 0) {
            return AbstractC0637z0.X(this, 0L, j4);
        }
        throw new IllegalArgumentException(Long.toString(j4));
    }

    @Override // j$.util.stream.F
    public final Stream mapToObj(DoubleFunction doubleFunction) {
        Objects.requireNonNull(doubleFunction);
        return new C0615u(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, doubleFunction, 0);
    }

    @Override // j$.util.stream.F
    public final C0510m max() {
        return reduce(new r(3));
    }

    @Override // j$.util.stream.F
    public final C0510m min() {
        return reduce(new C0571l(26));
    }

    @Override // j$.util.stream.F
    public final F peek(DoubleConsumer doubleConsumer) {
        Objects.requireNonNull(doubleConsumer);
        return new C0636z(this, doubleConsumer);
    }

    @Override // j$.util.stream.F
    public final IntStream q() {
        Objects.requireNonNull(null);
        return new C0624w(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, 0);
    }

    @Override // j$.util.stream.F
    public final double reduce(double d4, DoubleBinaryOperator doubleBinaryOperator) {
        Objects.requireNonNull(doubleBinaryOperator);
        return ((Double) z(new J1(EnumC0545f3.DOUBLE_VALUE, doubleBinaryOperator, d4))).doubleValue();
    }

    @Override // j$.util.stream.F
    public final C0510m reduce(DoubleBinaryOperator doubleBinaryOperator) {
        Objects.requireNonNull(doubleBinaryOperator);
        return (C0510m) z(new D1(EnumC0545f3.DOUBLE_VALUE, doubleBinaryOperator, 1));
    }

    @Override // j$.util.stream.F
    public final F skip(long j4) {
        int i4 = (j4 > 0L ? 1 : (j4 == 0L ? 0 : -1));
        if (i4 >= 0) {
            return i4 == 0 ? this : AbstractC0637z0.X(this, j4, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j4));
    }

    @Override // j$.util.stream.F
    public final F sorted() {
        return new B(this, EnumC0540e3.f4492q | EnumC0540e3.f4490o, 0);
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h
    public final j$.util.G spliterator() {
        return V(super.spliterator());
    }

    @Override // j$.util.stream.F
    public final double sum() {
        double[] dArr = (double[]) collect(new r(4), new C0571l(5), new C0571l(2));
        Set set = Collectors.f4262a;
        double d4 = dArr[0] + dArr[1];
        double d5 = dArr[dArr.length - 1];
        return (Double.isNaN(d4) && Double.isInfinite(d5)) ? d5 : d4;
    }

    @Override // j$.util.stream.F
    public final C0505h summaryStatistics() {
        return (C0505h) collect(new C0571l(18), new C0571l(28), new C0571l(29));
    }

    @Override // j$.util.stream.F
    public final double[] toArray() {
        return (double[]) AbstractC0637z0.O((F0) A(new r(2))).e();
    }

    @Override // j$.util.stream.F
    public final boolean u() {
        return ((Boolean) z(AbstractC0637z0.Y(EnumC0625w0.NONE))).booleanValue();
    }
}
