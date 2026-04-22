package W1;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import h2.HandlerC0442e;
import java.util.HashMap;
import java.util.concurrent.Executor;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class Y extends AbstractC0319g {

    /* renamed from: d  reason: collision with root package name */
    public final HashMap f2673d = new HashMap();

    /* renamed from: e  reason: collision with root package name */
    public final Context f2674e;
    public volatile HandlerC0442e f;

    /* renamed from: g  reason: collision with root package name */
    public final Z1.a f2675g;

    /* renamed from: h  reason: collision with root package name */
    public final long f2676h;

    /* renamed from: i  reason: collision with root package name */
    public final long f2677i;

    /* renamed from: j  reason: collision with root package name */
    public volatile Executor f2678j;

    /* JADX WARN: Type inference failed for: r2v2, types: [h2.e, android.os.Handler] */
    public Y(Context context, Looper looper) {
        X x4 = new X(this);
        this.f2674e = context.getApplicationContext();
        ?? handler = new Handler(looper, x4);
        Looper.getMainLooper();
        this.f = handler;
        this.f2675g = Z1.a.a();
        this.f2676h = 5000L;
        this.f2677i = 300000L;
        this.f2678j = null;
    }

    @Override // W1.AbstractC0319g
    public final boolean d(V v4, N n4, String str, Executor executor) {
        boolean z4;
        synchronized (this.f2673d) {
            try {
                W w4 = (W) this.f2673d.get(v4);
                if (executor == null) {
                    executor = this.f2678j;
                }
                if (w4 == null) {
                    w4 = new W(this, v4);
                    w4.f2666a.put(n4, n4);
                    w4.a(str, executor);
                    this.f2673d.put(v4, w4);
                } else {
                    this.f.removeMessages(0, v4);
                    if (!w4.f2666a.containsKey(n4)) {
                        w4.f2666a.put(n4, n4);
                        int i4 = w4.f2667b;
                        if (i4 != 1) {
                            if (i4 == 2) {
                                w4.a(str, executor);
                            }
                        } else {
                            n4.onServiceConnected(w4.f, w4.f2669d);
                        }
                    } else {
                        throw new IllegalStateException("Trying to bind a GmsServiceConnection that was already connected before.  config=".concat(v4.toString()));
                    }
                }
                z4 = w4.f2668c;
            } catch (Throwable th) {
                throw th;
            }
        }
        return z4;
    }
}
