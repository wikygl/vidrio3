package j$.util.stream;

import j$.util.C0506i;
import j$.util.C0510m;
import j$.util.C0511n;
import j$.util.C0641u;
import j$.util.C0642v;
import j$.util.InterfaceC0643w;
import j$.util.Spliterator;
import j$.util.stream.Stream;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public interface IntStream extends InterfaceC0551h {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
    public final /* synthetic */ class VivifiedWrapper implements IntStream {

        /* renamed from: a */
        public final /* synthetic */ java.util.stream.IntStream f4306a;

        private /* synthetic */ VivifiedWrapper(java.util.stream.IntStream intStream) {
            this.f4306a = intStream;
        }

        public static /* synthetic */ IntStream convert(java.util.stream.IntStream intStream) {
            if (intStream == null) {
                return null;
            }
            return intStream instanceof Wrapper ? IntStream.this : new VivifiedWrapper(intStream);
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ IntStream a() {
            return convert(this.f4306a.filter(null));
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ F asDoubleStream() {
            return D.w(this.f4306a.asDoubleStream());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ InterfaceC0587o0 asLongStream() {
            return C0577m0.w(this.f4306a.asLongStream());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ C0510m average() {
            return j$.util.D.j(this.f4306a.average());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ IntStream b() {
            return convert(this.f4306a.map(null));
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ Stream boxed() {
            return C0525b3.w(this.f4306a.boxed());
        }

        @Override // java.lang.AutoCloseable
        public final /* synthetic */ void close() {
            this.f4306a.close();
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
            return this.f4306a.collect(supplier, objIntConsumer, biConsumer);
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ long count() {
            return this.f4306a.count();
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ F d() {
            return D.w(this.f4306a.mapToDouble(null));
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ IntStream distinct() {
            return convert(this.f4306a.distinct());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ boolean e() {
            return this.f4306a.noneMatch(null);
        }

        public final /* synthetic */ boolean equals(Object obj) {
            java.util.stream.IntStream intStream = this.f4306a;
            if (obj instanceof VivifiedWrapper) {
                obj = ((VivifiedWrapper) obj).f4306a;
            }
            return intStream.equals(obj);
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ C0511n findAny() {
            return j$.util.D.k(this.f4306a.findAny());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ C0511n findFirst() {
            return j$.util.D.k(this.f4306a.findFirst());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ void forEach(IntConsumer intConsumer) {
            this.f4306a.forEach(intConsumer);
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ void forEachOrdered(IntConsumer intConsumer) {
            this.f4306a.forEachOrdered(intConsumer);
        }

        public final /* synthetic */ int hashCode() {
            return this.f4306a.hashCode();
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ InterfaceC0587o0 i() {
            return C0577m0.w(this.f4306a.mapToLong(null));
        }

        @Override // j$.util.stream.InterfaceC0551h
        public final /* synthetic */ boolean isParallel() {
            return this.f4306a.isParallel();
        }

        @Override // j$.util.stream.IntStream, j$.util.stream.InterfaceC0551h, j$.util.stream.F
        public final /* synthetic */ InterfaceC0643w iterator() {
            return C0641u.a(this.f4306a.iterator());
        }

        @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
        public final /* synthetic */ Iterator iterator() {
            return this.f4306a.iterator();
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ IntStream limit(long j4) {
            return convert(this.f4306a.limit(j4));
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r1v0, types: [j$.util.stream.R0, java.lang.Object, java.util.function.IntFunction] */
        @Override // j$.util.stream.IntStream
        public final IntStream m(R0 r02) {
            java.util.stream.IntStream intStream = this.f4306a;
            ?? obj = new Object();
            obj.f4371a = r02;
            return convert(intStream.flatMap(obj));
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ Stream mapToObj(IntFunction intFunction) {
            return C0525b3.w(this.f4306a.mapToObj(intFunction));
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ C0511n max() {
            return j$.util.D.k(this.f4306a.max());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ C0511n min() {
            return j$.util.D.k(this.f4306a.min());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ boolean o() {
            return this.f4306a.anyMatch(null);
        }

        @Override // j$.util.stream.InterfaceC0551h
        public final /* synthetic */ InterfaceC0551h onClose(Runnable runnable) {
            return C0541f.w(this.f4306a.onClose(runnable));
        }

        @Override // j$.util.stream.IntStream, j$.util.stream.InterfaceC0551h, j$.util.stream.F
        public final /* synthetic */ IntStream parallel() {
            return convert(this.f4306a.parallel());
        }

        @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
        public final /* synthetic */ InterfaceC0551h parallel() {
            return C0541f.w(this.f4306a.parallel());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ IntStream peek(IntConsumer intConsumer) {
            return convert(this.f4306a.peek(intConsumer));
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ boolean r() {
            return this.f4306a.allMatch(null);
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ int reduce(int i4, IntBinaryOperator intBinaryOperator) {
            return this.f4306a.reduce(i4, intBinaryOperator);
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ C0511n reduce(IntBinaryOperator intBinaryOperator) {
            return j$.util.D.k(this.f4306a.reduce(intBinaryOperator));
        }

        @Override // j$.util.stream.IntStream, j$.util.stream.InterfaceC0551h, j$.util.stream.F
        public final /* synthetic */ IntStream sequential() {
            return convert(this.f4306a.sequential());
        }

        @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
        public final /* synthetic */ InterfaceC0551h sequential() {
            return C0541f.w(this.f4306a.sequential());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ IntStream skip(long j4) {
            return convert(this.f4306a.skip(j4));
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ IntStream sorted() {
            return convert(this.f4306a.sorted());
        }

        @Override // j$.util.stream.IntStream, j$.util.stream.InterfaceC0551h
        public final /* synthetic */ j$.util.J spliterator() {
            return j$.util.H.a(this.f4306a.spliterator());
        }

        @Override // j$.util.stream.InterfaceC0551h
        public final /* synthetic */ Spliterator spliterator() {
            return j$.util.Q.a(this.f4306a.spliterator());
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ int sum() {
            return this.f4306a.sum();
        }

        @Override // j$.util.stream.IntStream
        public final C0506i summaryStatistics() {
            this.f4306a.summaryStatistics();
            throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.IntSummaryStatistics");
        }

        @Override // j$.util.stream.IntStream
        public final /* synthetic */ int[] toArray() {
            return this.f4306a.toArray();
        }

        @Override // j$.util.stream.InterfaceC0551h
        public final /* synthetic */ InterfaceC0551h unordered() {
            return C0541f.w(this.f4306a.unordered());
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public final /* synthetic */ class Wrapper implements java.util.stream.IntStream {
        private /* synthetic */ Wrapper() {
            IntStream.this = r1;
        }

        public static /* synthetic */ java.util.stream.IntStream convert(IntStream intStream) {
            if (intStream == null) {
                return null;
            }
            return intStream instanceof VivifiedWrapper ? ((VivifiedWrapper) intStream).f4306a : new Wrapper();
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ boolean allMatch(IntPredicate intPredicate) {
            return IntStream.this.r();
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ boolean anyMatch(IntPredicate intPredicate) {
            return IntStream.this.o();
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ DoubleStream asDoubleStream() {
            return E.w(IntStream.this.asDoubleStream());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ LongStream asLongStream() {
            return C0582n0.w(IntStream.this.asLongStream());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ OptionalDouble average() {
            return j$.util.D.n(IntStream.this.average());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ java.util.stream.Stream boxed() {
            return Stream.Wrapper.convert(IntStream.this.boxed());
        }

        @Override // java.util.stream.BaseStream, java.lang.AutoCloseable
        public final /* synthetic */ void close() {
            IntStream.this.close();
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer) {
            return IntStream.this.collect(supplier, objIntConsumer, biConsumer);
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ long count() {
            return IntStream.this.count();
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ java.util.stream.IntStream distinct() {
            return convert(IntStream.this.distinct());
        }

        public final /* synthetic */ boolean equals(Object obj) {
            IntStream intStream = IntStream.this;
            if (obj instanceof Wrapper) {
                obj = IntStream.this;
            }
            return intStream.equals(obj);
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ java.util.stream.IntStream filter(IntPredicate intPredicate) {
            return convert(IntStream.this.a());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ OptionalInt findAny() {
            return j$.util.D.o(IntStream.this.findAny());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ OptionalInt findFirst() {
            return j$.util.D.o(IntStream.this.findFirst());
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r1v0, types: [j$.util.stream.R0, java.lang.Object] */
        @Override // java.util.stream.IntStream
        public final java.util.stream.IntStream flatMap(IntFunction intFunction) {
            IntStream intStream = IntStream.this;
            ?? obj = new Object();
            obj.f4371a = intFunction;
            return convert(intStream.m(obj));
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ void forEach(IntConsumer intConsumer) {
            IntStream.this.forEach(intConsumer);
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ void forEachOrdered(IntConsumer intConsumer) {
            IntStream.this.forEachOrdered(intConsumer);
        }

        public final /* synthetic */ int hashCode() {
            return IntStream.this.hashCode();
        }

        @Override // java.util.stream.BaseStream
        public final /* synthetic */ boolean isParallel() {
            return IntStream.this.isParallel();
        }

        @Override // java.util.stream.IntStream, java.util.stream.BaseStream
        public final /* synthetic */ Iterator<Integer> iterator() {
            return IntStream.this.iterator();
        }

        @Override // java.util.stream.IntStream, java.util.stream.BaseStream
        /* renamed from: iterator */
        public final /* synthetic */ Iterator<Integer> iterator2() {
            return C0642v.a(IntStream.this.iterator());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ java.util.stream.IntStream limit(long j4) {
            return convert(IntStream.this.limit(j4));
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ java.util.stream.IntStream map(IntUnaryOperator intUnaryOperator) {
            return convert(IntStream.this.b());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ DoubleStream mapToDouble(IntToDoubleFunction intToDoubleFunction) {
            return E.w(IntStream.this.d());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ LongStream mapToLong(IntToLongFunction intToLongFunction) {
            return C0582n0.w(IntStream.this.i());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ java.util.stream.Stream mapToObj(IntFunction intFunction) {
            return Stream.Wrapper.convert(IntStream.this.mapToObj(intFunction));
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ OptionalInt max() {
            return j$.util.D.o(IntStream.this.max());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ OptionalInt min() {
            return j$.util.D.o(IntStream.this.min());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ boolean noneMatch(IntPredicate intPredicate) {
            return IntStream.this.e();
        }

        /* JADX WARN: Type inference failed for: r2v2, types: [java.util.stream.IntStream, java.util.stream.BaseStream] */
        @Override // java.util.stream.BaseStream
        public final /* synthetic */ java.util.stream.IntStream onClose(Runnable runnable) {
            return C0546g.w(IntStream.this.onClose(runnable));
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.IntStream, java.util.stream.BaseStream] */
        @Override // java.util.stream.IntStream, java.util.stream.BaseStream
        public final /* synthetic */ java.util.stream.IntStream parallel() {
            return C0546g.w(IntStream.this.parallel());
        }

        @Override // java.util.stream.IntStream, java.util.stream.BaseStream
        /* renamed from: parallel */
        public final /* synthetic */ java.util.stream.IntStream parallel2() {
            return convert(IntStream.this.parallel());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ java.util.stream.IntStream peek(IntConsumer intConsumer) {
            return convert(IntStream.this.peek(intConsumer));
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ int reduce(int i4, IntBinaryOperator intBinaryOperator) {
            return IntStream.this.reduce(i4, intBinaryOperator);
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ OptionalInt reduce(IntBinaryOperator intBinaryOperator) {
            return j$.util.D.o(IntStream.this.reduce(intBinaryOperator));
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.IntStream, java.util.stream.BaseStream] */
        @Override // java.util.stream.IntStream, java.util.stream.BaseStream
        public final /* synthetic */ java.util.stream.IntStream sequential() {
            return C0546g.w(IntStream.this.sequential());
        }

        @Override // java.util.stream.IntStream, java.util.stream.BaseStream
        /* renamed from: sequential */
        public final /* synthetic */ java.util.stream.IntStream sequential2() {
            return convert(IntStream.this.sequential());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ java.util.stream.IntStream skip(long j4) {
            return convert(IntStream.this.skip(j4));
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ java.util.stream.IntStream sorted() {
            return convert(IntStream.this.sorted());
        }

        @Override // java.util.stream.IntStream, java.util.stream.BaseStream
        public final /* synthetic */ java.util.Spliterator<Integer> spliterator() {
            return j$.util.I.a(IntStream.this.spliterator());
        }

        @Override // java.util.stream.IntStream, java.util.stream.BaseStream
        /* renamed from: spliterator */
        public final /* synthetic */ java.util.Spliterator<Integer> spliterator2() {
            return Spliterator.Wrapper.convert(IntStream.this.spliterator());
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ int sum() {
            return IntStream.this.sum();
        }

        @Override // java.util.stream.IntStream
        public final IntSummaryStatistics summaryStatistics() {
            IntStream.this.summaryStatistics();
            throw new Error("Java 8+ API desugaring (library desugaring) cannot convert to java.util.IntSummaryStatistics");
        }

        @Override // java.util.stream.IntStream
        public final /* synthetic */ int[] toArray() {
            return IntStream.this.toArray();
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.IntStream, java.util.stream.BaseStream] */
        @Override // java.util.stream.BaseStream
        public final /* synthetic */ java.util.stream.IntStream unordered() {
            return C0546g.w(IntStream.this.unordered());
        }
    }

    IntStream a();

    F asDoubleStream();

    InterfaceC0587o0 asLongStream();

    C0510m average();

    IntStream b();

    Stream boxed();

    Object collect(Supplier supplier, ObjIntConsumer objIntConsumer, BiConsumer biConsumer);

    long count();

    F d();

    IntStream distinct();

    boolean e();

    C0511n findAny();

    C0511n findFirst();

    void forEach(IntConsumer intConsumer);

    void forEachOrdered(IntConsumer intConsumer);

    InterfaceC0587o0 i();

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    InterfaceC0643w iterator();

    IntStream limit(long j4);

    IntStream m(R0 r02);

    Stream mapToObj(IntFunction intFunction);

    C0511n max();

    C0511n min();

    boolean o();

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    IntStream parallel();

    IntStream peek(IntConsumer intConsumer);

    boolean r();

    int reduce(int i4, IntBinaryOperator intBinaryOperator);

    C0511n reduce(IntBinaryOperator intBinaryOperator);

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    IntStream sequential();

    IntStream skip(long j4);

    IntStream sorted();

    @Override // j$.util.stream.InterfaceC0551h
    j$.util.J spliterator();

    int sum();

    C0506i summaryStatistics();

    int[] toArray();
}
