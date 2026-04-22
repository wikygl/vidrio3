package D3;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;
import l3.c;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class e {
    private static volatile Choreographer choreographer;

    static {
        Object a4;
        try {
            a4 = new c(a(Looper.getMainLooper()));
        } catch (Throwable th) {
            a4 = B2.a.a(th);
        }
        if (a4 instanceof c.a) {
            a4 = null;
        }
        d dVar = (d) a4;
    }

    public static final Handler a(Looper looper) {
        if (Build.VERSION.SDK_INT >= 28) {
            Object invoke = Handler.class.getDeclaredMethod("createAsync", Looper.class).invoke(null, looper);
            h.c(invoke, "null cannot be cast to non-null type android.os.Handler");
            return (Handler) invoke;
        }
        try {
            return (Handler) Handler.class.getDeclaredConstructor(Looper.class, Handler.Callback.class, Boolean.TYPE).newInstance(looper, null, Boolean.TRUE);
        } catch (NoSuchMethodException unused) {
            return new Handler(looper);
        }
    }
}
