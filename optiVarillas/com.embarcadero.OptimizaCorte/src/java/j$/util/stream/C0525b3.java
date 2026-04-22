package j$.util.stream;

import j$.util.C0509l;
import j$.util.Spliterator;
import j$.util.stream.IntStream;
import j$.util.stream.Stream;
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

/* renamed from: j$.util.stream.b3 */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0525b3 implements Stream {

    /* renamed from: a */
    public final /* synthetic */ java.util.stream.Stream f4460a;

    private /* synthetic */ C0525b3(java.util.stream.Stream stream) {
        this.f4460a = stream;
    }

    public static /* synthetic */ Stream w(java.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return stream instanceof Stream.Wrapper ? Stream.this : new C0525b3(stream);
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ boolean allMatch(Predicate predicate) {
        return this.f4460a.allMatch(predicate);
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ boolean anyMatch(Predicate predicate) {
        return this.f4460a.anyMatch(predicate);
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream c(C0516a c0516a) {
        return w(this.f4460a.flatMap(AbstractC0637z0.S(c0516a)));
    }

    @Override // java.lang.AutoCloseable
    public final /* synthetic */ void close() {
        this.f4460a.close();
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Object collect(Collector collector) {
        return this.f4460a.collect(C0566k.a(collector));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Object collect(Supplier supplier, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.f4460a.collect(supplier, biConsumer, biConsumer2);
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ long count() {
        return this.f4460a.count();
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream distinct() {
        return w(this.f4460a.distinct());
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream dropWhile(Predicate predicate) {
        return w(this.f4460a.dropWhile(predicate));
    }

    public final /* synthetic */ boolean equals(Object obj) {
        java.util.stream.Stream stream = this.f4460a;
        if (obj instanceof C0525b3) {
            obj = ((C0525b3) obj).f4460a;
        }
        return stream.equals(obj);
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream filter(Predicate predicate) {
        return w(this.f4460a.filter(predicate));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ C0509l findAny() {
        return j$.util.D.i(this.f4460a.findAny());
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ C0509l findFirst() {
        return j$.util.D.i(this.f4460a.findFirst());
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ void forEach(Consumer consumer) {
        this.f4460a.forEach(consumer);
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ void forEachOrdered(Consumer consumer) {
        this.f4460a.forEachOrdered(consumer);
    }

    public final /* synthetic */ int hashCode() {
        return this.f4460a.hashCode();
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ boolean isParallel() {
        return this.f4460a.isParallel();
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ Iterator iterator() {
        return this.f4460a.iterator();
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ InterfaceC0587o0 k(C0516a c0516a) {
        return C0577m0.w(this.f4460a.flatMapToLong(AbstractC0637z0.S(c0516a)));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream limit(long j4) {
        return w(this.f4460a.limit(j4));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream map(Function function) {
        return w(this.f4460a.map(function));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ F mapToDouble(ToDoubleFunction toDoubleFunction) {
        return D.w(this.f4460a.mapToDouble(toDoubleFunction));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ IntStream mapToInt(ToIntFunction toIntFunction) {
        return IntStream.VivifiedWrapper.convert(this.f4460a.mapToInt(toIntFunction));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ InterfaceC0587o0 mapToLong(ToLongFunction toLongFunction) {
        return C0577m0.w(this.f4460a.mapToLong(toLongFunction));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ C0509l max(Comparator comparator) {
        return j$.util.D.i(this.f4460a.max(comparator));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ C0509l min(Comparator comparator) {
        return j$.util.D.i(this.f4460a.min(comparator));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ boolean noneMatch(Predicate predicate) {
        return this.f4460a.noneMatch(predicate);
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ InterfaceC0551h onClose(Runnable runnable) {
        return C0541f.w(this.f4460a.onClose(runnable));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ IntStream p(C0516a c0516a) {
        return IntStream.VivifiedWrapper.convert(this.f4460a.flatMapToInt(AbstractC0637z0.S(c0516a)));
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ InterfaceC0551h parallel() {
        return C0541f.w(this.f4460a.parallel());
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream peek(Consumer consumer) {
        return w(this.f4460a.peek(consumer));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ C0509l reduce(BinaryOperator binaryOperator) {
        return j$.util.D.i(this.f4460a.reduce(binaryOperator));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Object reduce(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
        return this.f4460a.reduce(obj, biFunction, binaryOperator);
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Object reduce(Object obj, BinaryOperator binaryOperator) {
        return this.f4460a.reduce(obj, binaryOperator);
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ InterfaceC0551h sequential() {
        return C0541f.w(this.f4460a.sequential());
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream skip(long j4) {
        return w(this.f4460a.skip(j4));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream sorted() {
        return w(this.f4460a.sorted());
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream sorted(Comparator comparator) {
        return w(this.f4460a.sorted(comparator));
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ Spliterator spliterator() {
        return j$.util.Q.a(this.f4460a.spliterator());
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Stream takeWhile(Predicate predicate) {
        return w(this.f4460a.takeWhile(predicate));
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Object[] toArray() {
        return this.f4460a.toArray();
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ Object[] toArray(IntFunction intFunction) {
        return this.f4460a.toArray(intFunction);
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ InterfaceC0551h unordered() {
        return C0541f.w(this.f4460a.unordered());
    }

    @Override // j$.util.stream.Stream
    public final /* synthetic */ F v(C0516a c0516a) {
        return D.w(this.f4460a.flatMapToDouble(AbstractC0637z0.S(c0516a)));
    }
}
