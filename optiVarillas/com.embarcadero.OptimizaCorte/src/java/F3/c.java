package F3;

import F3.c;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public abstract class c<N extends c<N>> {
    private volatile Object _next;
    private volatile Object _prev;

    static {
        AtomicReferenceFieldUpdater.newUpdater(c.class, Object.class, "_next");
        AtomicReferenceFieldUpdater.newUpdater(c.class, Object.class, "_prev");
    }
}
