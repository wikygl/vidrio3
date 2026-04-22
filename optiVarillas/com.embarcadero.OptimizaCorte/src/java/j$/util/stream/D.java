package j$.util.stream;

import j$.util.C0505h;
import j$.util.C0510m;
import j$.util.C0514q;
import j$.util.InterfaceC0515s;
import j$.util.Spliterator;
import j$.util.stream.IntStream;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class D implements F {

    /* renamed from: a */
    public final /* synthetic */ DoubleStream f4263a;

    private /* synthetic */ D(DoubleStream doubleStream) {
        this.f4263a = doubleStream;
    }

    public static /* synthetic */ F w(DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof E ? ((E) doubleStream).f4267a : new D(doubleStream);
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ F a() {
        return w(this.f4263a.filter(null));
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ C0510m average() {
        return j$.util.D.j(this.f4263a.average());
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ F b() {
        return w(this.f4263a.map(null));
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ Stream boxed() {
        return C0525b3.w(this.f4263a.boxed());
    }

    @Override // j$.util.stream.F
    public final F c(C0516a c0516a) {
        DoubleStream doubleStream = this.f4263a;
        C0516a c0516a2 = new C0516a(7);
        c0516a2.f4446b = c0516a;
        return w(doubleStream.flatMap(c0516a2));
    }

    @Override // java.lang.AutoCloseable
    public final /* synthetic */ void close() {
        this.f4263a.close();
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ Object collect(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BiConsumer biConsumer) {
        return this.f4263a.collect(supplier, objDoubleConsumer, biConsumer);
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ long count() {
        return this.f4263a.count();
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ F distinct() {
        return w(this.f4263a.distinct());
    }

    public final /* synthetic */ boolean equals(Object obj) {
        DoubleStream doubleStream = this.f4263a;
        if (obj instanceof D) {
            obj = ((D) obj).f4263a;
        }
        return doubleStream.equals(obj);
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ boolean f() {
        return this.f4263a.allMatch(null);
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ C0510m findAny() {
        return j$.util.D.j(this.f4263a.findAny());
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ C0510m findFirst() {
        return j$.util.D.j(this.f4263a.findFirst());
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ void forEach(DoubleConsumer doubleConsumer) {
        this.f4263a.forEach(doubleConsumer);
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ void forEachOrdered(DoubleConsumer doubleConsumer) {
        this.f4263a.forEachOrdered(doubleConsumer);
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ InterfaceC0587o0 g() {
        return C0577m0.w(this.f4263a.mapToLong(null));
    }

    public final /* synthetic */ int hashCode() {
        return this.f4263a.hashCode();
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ boolean isParallel() {
        return this.f4263a.isParallel();
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ InterfaceC0515s iterator() {
        return C0514q.a(this.f4263a.iterator());
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ Iterator iterator() {
        return this.f4263a.iterator();
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ boolean l() {
        return this.f4263a.anyMatch(null);
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ F limit(long j4) {
        return w(this.f4263a.limit(j4));
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ Stream mapToObj(DoubleFunction doubleFunction) {
        return C0525b3.w(this.f4263a.mapToObj(doubleFunction));
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ C0510m max() {
        return j$.util.D.j(this.f4263a.max());
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ C0510m min() {
        return j$.util.D.j(this.f4263a.min());
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ InterfaceC0551h onClose(Runnable runnable) {
        return C0541f.w(this.f4263a.onClose(runnable));
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ F parallel() {
        return w(this.f4263a.parallel());
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ InterfaceC0551h parallel() {
        return C0541f.w(this.f4263a.parallel());
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ F peek(DoubleConsumer doubleConsumer) {
        return w(this.f4263a.peek(doubleConsumer));
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ IntStream q() {
        return IntStream.VivifiedWrapper.convert(this.f4263a.mapToInt(null));
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ double reduce(double d4, DoubleBinaryOperator doubleBinaryOperator) {
        return this.f4263a.reduce(d4, doubleBinaryOperator);
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ C0510m reduce(DoubleBinaryOperator doubleBinaryOperator) {
        return j$.util.D.j(this.f4263a.reduce(doubleBinaryOperator));
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ F sequential() {
        return w(this.f4263a.sequential());
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ InterfaceC0551h sequential() {
        return C0541f.w(this.f4263a.sequential());
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ F skip(long j4) {
        return w(this.f4263a.skip(j4));
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ F sorted() {
        return w(this.f4263a.sorted());
    }

    @Override // j$.util.stream.F, j$.util.stream.InterfaceC0551h
    public final /* synthetic */ j$.util.G spliterator() {
        return j$.util.E.a(this.f4263a.spliterator());
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ Spliterator spliterator() {
        return j$.util.Q.a(this.f4263a.spliterator());
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ double sum() {
        return this.f4263a.sum();
    }

    @Override // j$.util.stream.F
    public final C0505h summaryStatistics() {
        this.f4263a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.DoubleSummaryStatistics");
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ double[] toArray() {
        return this.f4263a.toArray();
    }

    @Override // j$.util.stream.F
    public final /* synthetic */ boolean u() {
        return this.f4263a.noneMatch(null);
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ InterfaceC0551h unordered() {
        return C0541f.w(this.f4263a.unordered());
    }
}
