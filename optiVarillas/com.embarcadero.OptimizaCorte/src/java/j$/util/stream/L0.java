package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
interface L0 {
    L0 b(int i4);

    long count();

    void forEach(Consumer consumer);

    L0 h(long j4, long j5, IntFunction intFunction);

    void i(Object[] objArr, int i4);

    Object[] o(IntFunction intFunction);

    int q();

    Spliterator spliterator();
}
