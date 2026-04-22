package j$.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.util.s  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public interface InterfaceC0515s extends B {
    @Override // java.util.Iterator, j$.util.InterfaceC0507j
    void forEachRemaining(Consumer consumer);

    void forEachRemaining(DoubleConsumer doubleConsumer);

    @Override // java.util.Iterator
    Double next();

    double nextDouble();
}
