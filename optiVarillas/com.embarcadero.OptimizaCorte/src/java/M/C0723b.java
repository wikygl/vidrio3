package m;

import G3.g;
import android.os.Looper;

/* renamed from: m.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0723b extends g {

    /* renamed from: l  reason: collision with root package name */
    public static volatile C0723b f5272l;

    /* renamed from: m  reason: collision with root package name */
    public static final ExecutorC0722a f5273m = new Object();

    /* renamed from: k  reason: collision with root package name */
    public final C0724c f5274k = new C0724c();

    public static C0723b F() {
        if (f5272l != null) {
            return f5272l;
        }
        synchronized (C0723b.class) {
            try {
                if (f5272l == null) {
                    f5272l = new C0723b();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return f5272l;
    }

    public final void G(Runnable runnable) {
        C0724c c0724c = this.f5274k;
        if (c0724c.f5277m == null) {
            synchronized (c0724c.f5275k) {
                try {
                    if (c0724c.f5277m == null) {
                        c0724c.f5277m = C0724c.F(Looper.getMainLooper());
                    }
                } finally {
                }
            }
        }
        c0724c.f5277m.post(runnable);
    }
}
