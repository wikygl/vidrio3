package x3;

import j$.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class a extends w3.a {
    @Override // w3.a
    public final Random c() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        h.d(current, "current()");
        return current;
    }
}
