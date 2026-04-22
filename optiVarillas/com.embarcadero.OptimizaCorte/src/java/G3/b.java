package G3;

import C3.AbstractC0171v;
import C3.S;
import F3.w;
import e0.C0405a;
import java.util.concurrent.Executor;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class b extends S implements Executor {

    /* renamed from: l  reason: collision with root package name */
    public static final b f989l = new S();

    /* renamed from: m  reason: collision with root package name */
    public static final AbstractC0171v f990m;

    /* JADX WARN: Type inference failed for: r0v0, types: [G3.b, C3.S] */
    static {
        AbstractC0171v abstractC0171v = l.f1006l;
        int i4 = w.f946a;
        if (64 >= i4) {
            i4 = 64;
        }
        int g4 = A0.c.g("kotlinx.coroutines.io.parallelism", i4, 0, 0, 12);
        abstractC0171v.getClass();
        if (g4 >= 1) {
            if (g4 < k.f1002d) {
                if (g4 >= 1) {
                    abstractC0171v = new F3.j(abstractC0171v, g4);
                } else {
                    throw new IllegalArgumentException(C0405a.c("Expected positive parallelism level, but got ", g4).toString());
                }
            }
            f990m = abstractC0171v;
            return;
        }
        throw new IllegalArgumentException(C0405a.c("Expected positive parallelism level, but got ", g4).toString());
    }

    @Override // C3.AbstractC0171v
    public final void F(n3.f fVar, Runnable runnable) {
        f990m.F(fVar, runnable);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        throw new IllegalStateException("Cannot be invoked on Dispatchers.IO".toString());
    }

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        F(n3.g.f5388j, runnable);
    }

    @Override // C3.AbstractC0171v
    public final String toString() {
        return "Dispatchers.IO";
    }
}
