package W1;

import android.content.Context;
import android.content.ServiceConnection;
import android.os.HandlerThread;
import java.util.concurrent.Executor;

/* renamed from: W1.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public abstract class AbstractC0319g {

    /* renamed from: a  reason: collision with root package name */
    public static final Object f2741a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static Y f2742b;

    /* renamed from: c  reason: collision with root package name */
    public static HandlerThread f2743c;

    public static Y a(Context context) {
        synchronized (f2741a) {
            try {
                if (f2742b == null) {
                    f2742b = new Y(context.getApplicationContext(), context.getMainLooper());
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return f2742b;
    }

    public static HandlerThread b() {
        synchronized (f2741a) {
            try {
                HandlerThread handlerThread = f2743c;
                if (handlerThread != null) {
                    return handlerThread;
                }
                HandlerThread handlerThread2 = new HandlerThread("GoogleApiHandler", 9);
                f2743c = handlerThread2;
                handlerThread2.start();
                return f2743c;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void c(String str, ServiceConnection serviceConnection, boolean z4) {
        V v4 = new V(str, z4);
        Y y4 = (Y) this;
        C0324l.e(serviceConnection, "ServiceConnection must not be null");
        synchronized (y4.f2673d) {
            try {
                W w4 = (W) y4.f2673d.get(v4);
                if (w4 != null) {
                    if (w4.f2666a.containsKey(serviceConnection)) {
                        w4.f2666a.remove(serviceConnection);
                        if (w4.f2666a.isEmpty()) {
                            y4.f.sendMessageDelayed(y4.f.obtainMessage(0, v4), y4.f2676h);
                        }
                    } else {
                        throw new IllegalStateException("Trying to unbind a GmsServiceConnection  that was not bound before.  config=".concat(v4.toString()));
                    }
                } else {
                    throw new IllegalStateException("Nonexistent connection status for service config: ".concat(v4.toString()));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public abstract boolean d(V v4, N n4, String str, Executor executor);
}
