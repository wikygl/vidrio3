package j$.util.stream;

import j$.util.C0505h;
import j$.util.C0510m;
import j$.util.InterfaceC0515s;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public interface F extends InterfaceC0551h {
    F a();

    C0510m average();

    F b();

    Stream boxed();

    F c(C0516a c0516a);

    Object collect(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BiConsumer biConsumer);

    long count();

    F distinct();

    boolean f();

    C0510m findAny();

    C0510m findFirst();

    void forEach(DoubleConsumer doubleConsumer);

    void forEachOrdered(DoubleConsumer doubleConsumer);

    InterfaceC0587o0 g();

    @Override // 
    InterfaceC0515s iterator();

    boolean l();

    F limit(long j4);

    Stream mapToObj(DoubleFunction doubleFunction);

    C0510m max();

    C0510m min();

    @Override // 
    F parallel();

    F peek(DoubleConsumer doubleConsumer);

    IntStream q();

    double reduce(double d4, DoubleBinaryOperator doubleBinaryOperator);

    C0510m reduce(DoubleBinaryOperator doubleBinaryOperator);

    @Override // 
    F sequential();

    F skip(long j4);

    F sorted();

    @Override // j$.util.stream.InterfaceC0551h
    j$.util.G spliterator();

    double sum();

    C0505h summaryStatistics();

    double[] toArray();

    boolean u();
}
