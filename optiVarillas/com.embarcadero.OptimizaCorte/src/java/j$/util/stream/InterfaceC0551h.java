package j$.util.stream;

import j$.util.Spliterator;
import java.util.Iterator;

/* renamed from: j$.util.stream.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public interface InterfaceC0551h extends AutoCloseable {
    boolean isParallel();

    Iterator iterator();

    InterfaceC0551h onClose(Runnable runnable);

    InterfaceC0551h parallel();

    InterfaceC0551h sequential();

    Spliterator spliterator();

    InterfaceC0551h unordered();
}
