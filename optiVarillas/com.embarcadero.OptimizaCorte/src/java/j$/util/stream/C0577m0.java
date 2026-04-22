package j$.util.stream;

import j$.util.C0508k;
import j$.util.C0510m;
import j$.util.C0512o;
import j$.util.C0645y;
import j$.util.Spliterator;
import j$.util.stream.IntStream;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.LongStream;

/* renamed from: j$.util.stream.m0 */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0577m0 implements InterfaceC0587o0 {

    /* renamed from: a */
    public final /* synthetic */ LongStream f4544a;

    private /* synthetic */ C0577m0(LongStream longStream) {
        this.f4544a = longStream;
    }

    public static /* synthetic */ InterfaceC0587o0 w(LongStream longStream) {
        if (longStream == null) {
            return null;
        }
        return longStream instanceof C0582n0 ? ((C0582n0) longStream).f4550a : new C0577m0(longStream);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ InterfaceC0587o0 a() {
        return w(this.f4544a.filter(null));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ F asDoubleStream() {
        return D.w(this.f4544a.asDoubleStream());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ C0510m average() {
        return j$.util.D.j(this.f4544a.average());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ InterfaceC0587o0 b() {
        return w(this.f4544a.map(null));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ Stream boxed() {
        return C0525b3.w(this.f4544a.boxed());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final InterfaceC0587o0 c(C0516a c0516a) {
        LongStream longStream = this.f4544a;
        C0516a c0516a2 = new C0516a(9);
        c0516a2.f4446b = c0516a;
        return w(longStream.flatMap(c0516a2));
    }

    @Override // java.lang.AutoCloseable
    public final /* synthetic */ void close() {
        this.f4544a.close();
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ Object collect(Supplier supplier, ObjLongConsumer objLongConsumer, BiConsumer biConsumer) {
        return this.f4544a.collect(supplier, objLongConsumer, biConsumer);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ long count() {
        return this.f4544a.count();
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ InterfaceC0587o0 distinct() {
        return w(this.f4544a.distinct());
    }

    public final /* synthetic */ boolean equals(Object obj) {
        LongStream longStream = this.f4544a;
        if (obj instanceof C0577m0) {
            obj = ((C0577m0) obj).f4544a;
        }
        return longStream.equals(obj);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ C0512o findAny() {
        return j$.util.D.l(this.f4544a.findAny());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ C0512o findFirst() {
        return j$.util.D.l(this.f4544a.findFirst());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ void forEach(LongConsumer longConsumer) {
        this.f4544a.forEach(longConsumer);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ void forEachOrdered(LongConsumer longConsumer) {
        this.f4544a.forEachOrdered(longConsumer);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ F h() {
        return D.w(this.f4544a.mapToDouble(null));
    }

    public final /* synthetic */ int hashCode() {
        return this.f4544a.hashCode();
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ boolean isParallel() {
        return this.f4544a.isParallel();
    }

    @Override // j$.util.stream.InterfaceC0587o0, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ j$.util.A iterator() {
        return C0645y.a(this.f4544a.iterator());
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ Iterator iterator() {
        return this.f4544a.iterator();
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ boolean j() {
        return this.f4544a.noneMatch(null);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ InterfaceC0587o0 limit(long j4) {
        return w(this.f4544a.limit(j4));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ Stream mapToObj(LongFunction longFunction) {
        return C0525b3.w(this.f4544a.mapToObj(longFunction));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ C0512o max() {
        return j$.util.D.l(this.f4544a.max());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ C0512o min() {
        return j$.util.D.l(this.f4544a.min());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ boolean n() {
        return this.f4544a.allMatch(null);
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ InterfaceC0551h onClose(Runnable runnable) {
        return C0541f.w(this.f4544a.onClose(runnable));
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ InterfaceC0551h parallel() {
        return C0541f.w(this.f4544a.parallel());
    }

    @Override // j$.util.stream.InterfaceC0587o0, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ InterfaceC0587o0 parallel() {
        return w(this.f4544a.parallel());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ InterfaceC0587o0 peek(LongConsumer longConsumer) {
        return w(this.f4544a.peek(longConsumer));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ long reduce(long j4, LongBinaryOperator longBinaryOperator) {
        return this.f4544a.reduce(j4, longBinaryOperator);
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ C0512o reduce(LongBinaryOperator longBinaryOperator) {
        return j$.util.D.l(this.f4544a.reduce(longBinaryOperator));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ boolean s() {
        return this.f4544a.anyMatch(null);
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ InterfaceC0551h sequential() {
        return C0541f.w(this.f4544a.sequential());
    }

    @Override // j$.util.stream.InterfaceC0587o0, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ InterfaceC0587o0 sequential() {
        return w(this.f4544a.sequential());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ InterfaceC0587o0 skip(long j4) {
        return w(this.f4544a.skip(j4));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ InterfaceC0587o0 sorted() {
        return w(this.f4544a.sorted());
    }

    @Override // j$.util.stream.InterfaceC0587o0, j$.util.stream.InterfaceC0551h
    public final /* synthetic */ j$.util.M spliterator() {
        return j$.util.K.a(this.f4544a.spliterator());
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ Spliterator spliterator() {
        return j$.util.Q.a(this.f4544a.spliterator());
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ long sum() {
        return this.f4544a.sum();
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final C0508k summaryStatistics() {
        this.f4544a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.LongSummaryStatistics");
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ IntStream t() {
        return IntStream.VivifiedWrapper.convert(this.f4544a.mapToInt(null));
    }

    @Override // j$.util.stream.InterfaceC0587o0
    public final /* synthetic */ long[] toArray() {
        return this.f4544a.toArray();
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ InterfaceC0551h unordered() {
        return C0541f.w(this.f4544a.unordered());
    }
}
