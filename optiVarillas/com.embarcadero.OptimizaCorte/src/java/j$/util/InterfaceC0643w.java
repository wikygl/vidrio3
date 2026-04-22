package j$.util;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.util.w  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public interface InterfaceC0643w extends B {
    @Override // java.util.Iterator, j$.util.InterfaceC0507j
    void forEachRemaining(Consumer consumer);

    void forEachRemaining(IntConsumer intConsumer);

    @Override // java.util.Iterator
    Integer next();

    int nextInt();
}
