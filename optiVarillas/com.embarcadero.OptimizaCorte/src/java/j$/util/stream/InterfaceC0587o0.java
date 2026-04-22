package j$.util.stream;

import j$.util.C0508k;
import j$.util.C0510m;
import j$.util.C0512o;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

/* renamed from: j$.util.stream.o0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public interface InterfaceC0587o0 extends InterfaceC0551h {
    InterfaceC0587o0 a();

    F asDoubleStream();

    C0510m average();

    InterfaceC0587o0 b();

    Stream boxed();

    InterfaceC0587o0 c(C0516a c0516a);

    Object collect(Supplier supplier, ObjLongConsumer objLongConsumer, BiConsumer biConsumer);

    long count();

    InterfaceC0587o0 distinct();

    C0512o findAny();

    C0512o findFirst();

    void forEach(LongConsumer longConsumer);

    void forEachOrdered(LongConsumer longConsumer);

    F h();

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    j$.util.A iterator();

    boolean j();

    InterfaceC0587o0 limit(long j4);

    Stream mapToObj(LongFunction longFunction);

    C0512o max();

    C0512o min();

    boolean n();

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    InterfaceC0587o0 parallel();

    InterfaceC0587o0 peek(LongConsumer longConsumer);

    long reduce(long j4, LongBinaryOperator longBinaryOperator);

    C0512o reduce(LongBinaryOperator longBinaryOperator);

    boolean s();

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    InterfaceC0587o0 sequential();

    InterfaceC0587o0 skip(long j4);

    InterfaceC0587o0 sorted();

    @Override // j$.util.stream.InterfaceC0551h
    j$.util.M spliterator();

    long sum();

    C0508k summaryStatistics();

    IntStream t();

    long[] toArray();
}
