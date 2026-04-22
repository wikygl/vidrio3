package F3;

import C3.j0;
import F3.v;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class v<S extends v<S>> extends c<S> implements j0 {
    private volatile int cleanedAndPointers;

    static {
        AtomicIntegerFieldUpdater.newUpdater(v.class, "cleanedAndPointers");
    }

    public abstract void a();
}
