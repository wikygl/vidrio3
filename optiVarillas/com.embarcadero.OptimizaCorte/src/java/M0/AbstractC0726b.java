package m0;

/* renamed from: m0.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class AbstractC0726b<T> extends AbstractC0735k {
    public abstract void d(r0.e eVar, T t3);

    public final void e(T t3) {
        r0.e a4 = a();
        try {
            d(a4, t3);
            a4.f5706k.executeInsert();
        } finally {
            c(a4);
        }
    }
}
