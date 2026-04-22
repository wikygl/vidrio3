package j$.util.stream;

import j$.util.C0509l;
import j$.util.Objects;
import j$.util.Spliterator;
import j$.util.Spliterators;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/* renamed from: j$.util.stream.i2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class AbstractC0559i2 extends AbstractC0521b implements Stream {
    @Override // j$.util.stream.AbstractC0521b
    final L0 B(AbstractC0521b abstractC0521b, Spliterator spliterator, boolean z4, IntFunction intFunction) {
        return AbstractC0637z0.E(abstractC0521b, spliterator, z4, intFunction);
    }

    @Override // j$.util.stream.AbstractC0521b
    final boolean D(Spliterator spliterator, InterfaceC0599q2 interfaceC0599q2) {
        boolean n4;
        do {
            n4 = interfaceC0599q2.n();
            if (n4) {
                break;
            }
        } while (spliterator.tryAdvance(interfaceC0599q2));
        return n4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final EnumC0545f3 E() {
        return EnumC0545f3.REFERENCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final D0 J(long j4, IntFunction intFunction) {
        return AbstractC0637z0.D(j4, intFunction);
    }

    @Override // j$.util.stream.AbstractC0521b
    final Spliterator Q(AbstractC0521b abstractC0521b, Supplier supplier, boolean z4) {
        return new AbstractC0550g3(abstractC0521b, supplier, z4);
    }

    @Override // j$.util.stream.Stream
    public final boolean allMatch(Predicate predicate) {
        return ((Boolean) z(AbstractC0637z0.d0(EnumC0625w0.ALL, predicate))).booleanValue();
    }

    @Override // j$.util.stream.Stream
    public final boolean anyMatch(Predicate predicate) {
        return ((Boolean) z(AbstractC0637z0.d0(EnumC0625w0.ANY, predicate))).booleanValue();
    }

    @Override // j$.util.stream.Stream
    public final Stream c(C0516a c0516a) {
        Objects.requireNonNull(c0516a);
        return new C0544f2(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n | EnumC0540e3.f4495t, c0516a, 1);
    }

    @Override // j$.util.stream.Stream
    public final Object collect(Collector collector) {
        Object z4;
        if (isParallel() && collector.characteristics().contains(EnumC0556i.CONCURRENT) && (!H() || collector.characteristics().contains(EnumC0556i.UNORDERED))) {
            z4 = collector.supplier().get();
            forEach(new C0597q0(3, collector.accumulator(), z4));
        } else {
            Supplier supplier = ((Collector) Objects.requireNonNull(collector)).supplier();
            z4 = z(new M1(EnumC0545f3.REFERENCE, collector.combiner(), collector.accumulator(), supplier, collector));
        }
        return collector.characteristics().contains(EnumC0556i.IDENTITY_FINISH) ? z4 : collector.finisher().apply(z4);
    }

    @Override // j$.util.stream.Stream
    public final Object collect(Supplier supplier, BiConsumer biConsumer, BiConsumer biConsumer2) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(biConsumer);
        Objects.requireNonNull(biConsumer2);
        return z(new F1(EnumC0545f3.REFERENCE, biConsumer2, biConsumer, supplier, 3));
    }

    @Override // j$.util.stream.Stream
    public final long count() {
        return ((Long) z(new H1(2))).longValue();
    }

    @Override // j$.util.stream.Stream
    public final Stream distinct() {
        return new AbstractC0554h2(this, EnumC0540e3.f4488m | EnumC0540e3.f4495t, 0);
    }

    @Override // j$.util.stream.Stream
    public final Stream dropWhile(Predicate predicate) {
        int i4 = U3.f4424a;
        Objects.requireNonNull(predicate);
        return new Q3(this, U3.f4425b, predicate);
    }

    @Override // j$.util.stream.Stream
    public final Stream filter(Predicate predicate) {
        Objects.requireNonNull(predicate);
        return new C0615u(this, EnumC0540e3.f4495t, predicate, 4);
    }

    @Override // j$.util.stream.Stream
    public final C0509l findAny() {
        return (C0509l) z(K.f4315d);
    }

    @Override // j$.util.stream.Stream
    public final C0509l findFirst() {
        return (C0509l) z(K.f4314c);
    }

    public void forEach(Consumer consumer) {
        Objects.requireNonNull(consumer);
        z(new Q(consumer, false));
    }

    public void forEachOrdered(Consumer consumer) {
        Objects.requireNonNull(consumer);
        z(new Q(consumer, true));
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final Iterator iterator() {
        return Spliterators.i(spliterator());
    }

    @Override // j$.util.stream.Stream
    public final InterfaceC0587o0 k(C0516a c0516a) {
        Objects.requireNonNull(c0516a);
        return new C0557i0(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n | EnumC0540e3.f4495t, c0516a, 2);
    }

    @Override // j$.util.stream.Stream
    public final Stream limit(long j4) {
        if (j4 >= 0) {
            return AbstractC0637z0.e0(this, 0L, j4);
        }
        throw new IllegalArgumentException(Long.toString(j4));
    }

    @Override // j$.util.stream.Stream
    public final Stream map(Function function) {
        Objects.requireNonNull(function);
        return new C0544f2(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, function, 0);
    }

    @Override // j$.util.stream.Stream
    public final F mapToDouble(ToDoubleFunction toDoubleFunction) {
        Objects.requireNonNull(toDoubleFunction);
        return new C0636z(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, toDoubleFunction, 2);
    }

    @Override // j$.util.stream.Stream
    public final IntStream mapToInt(ToIntFunction toIntFunction) {
        Objects.requireNonNull(toIntFunction);
        return new X(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, toIntFunction, 2);
    }

    @Override // j$.util.stream.Stream
    public final InterfaceC0587o0 mapToLong(ToLongFunction toLongFunction) {
        Objects.requireNonNull(toLongFunction);
        return new C0557i0(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n, toLongFunction, 3);
    }

    @Override // j$.util.stream.Stream
    public final C0509l max(Comparator comparator) {
        Objects.requireNonNull(comparator);
        return reduce(new j$.util.function.a(comparator, 0));
    }

    @Override // j$.util.stream.Stream
    public final C0509l min(Comparator comparator) {
        Objects.requireNonNull(comparator);
        return reduce(new j$.util.function.a(comparator, 1));
    }

    @Override // j$.util.stream.Stream
    public final boolean noneMatch(Predicate predicate) {
        return ((Boolean) z(AbstractC0637z0.d0(EnumC0625w0.NONE, predicate))).booleanValue();
    }

    @Override // j$.util.stream.Stream
    public final IntStream p(C0516a c0516a) {
        Objects.requireNonNull(c0516a);
        return new X(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n | EnumC0540e3.f4495t, c0516a, 3);
    }

    @Override // j$.util.stream.Stream
    public final Stream peek(Consumer consumer) {
        Objects.requireNonNull(consumer);
        return new C0615u(this, consumer);
    }

    @Override // j$.util.stream.Stream
    public final C0509l reduce(BinaryOperator binaryOperator) {
        Objects.requireNonNull(binaryOperator);
        return (C0509l) z(new D1(EnumC0545f3.REFERENCE, binaryOperator, 2));
    }

    @Override // j$.util.stream.Stream
    public final Object reduce(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
        Objects.requireNonNull(biFunction);
        Objects.requireNonNull(binaryOperator);
        return z(new F1(EnumC0545f3.REFERENCE, binaryOperator, biFunction, obj, 2));
    }

    @Override // j$.util.stream.Stream
    public final Object reduce(Object obj, BinaryOperator binaryOperator) {
        Objects.requireNonNull(binaryOperator);
        Objects.requireNonNull(binaryOperator);
        return z(new F1(EnumC0545f3.REFERENCE, binaryOperator, binaryOperator, obj, 2));
    }

    @Override // j$.util.stream.Stream
    public final Stream skip(long j4) {
        int i4 = (j4 > 0L ? 1 : (j4 == 0L ? 0 : -1));
        if (i4 >= 0) {
            return i4 == 0 ? this : AbstractC0637z0.e0(this, j4, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j4));
    }

    @Override // j$.util.stream.Stream
    public final Stream sorted() {
        return new L2(this);
    }

    @Override // j$.util.stream.Stream
    public final Stream sorted(Comparator comparator) {
        return new L2(this, comparator);
    }

    @Override // j$.util.stream.Stream
    public final Stream takeWhile(Predicate predicate) {
        int i4 = U3.f4424a;
        Objects.requireNonNull(predicate);
        return new O3(this, U3.f4424a, predicate);
    }

    @Override // j$.util.stream.Stream
    public final Object[] toArray() {
        return toArray(new C0537e0(4));
    }

    @Override // j$.util.stream.Stream
    public final Object[] toArray(IntFunction intFunction) {
        return AbstractC0637z0.N(A(intFunction), intFunction).o(intFunction);
    }

    @Override // j$.util.stream.Stream
    public final F v(C0516a c0516a) {
        Objects.requireNonNull(c0516a);
        return new C0636z(this, EnumC0540e3.f4491p | EnumC0540e3.f4489n | EnumC0540e3.f4495t, c0516a, 3);
    }
}
