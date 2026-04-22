package w3;

import java.util.Random;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class a extends c {
    @Override // w3.c
    public final int a() {
        return c().nextInt();
    }

    @Override // w3.c
    public final int b() {
        return c().nextInt(2147418112);
    }

    public abstract Random c();
}
