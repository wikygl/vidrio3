package W1;

import android.content.ComponentName;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class X implements Handler.Callback {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ Y f2672j;

    public /* synthetic */ X(Y y4) {
        this.f2672j = y4;
    }

    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message message) {
        int i4 = message.what;
        if (i4 != 0) {
            if (i4 != 1) {
                return false;
            }
            synchronized (this.f2672j.f2673d) {
                try {
                    V v4 = (V) message.obj;
                    W w4 = (W) this.f2672j.f2673d.get(v4);
                    if (w4 != null && w4.f2667b == 3) {
                        Log.e("GmsClientSupervisor", "Timeout waiting for ServiceConnection callback ".concat(String.valueOf(v4)), new Exception());
                        ComponentName componentName = w4.f;
                        if (componentName == null) {
                            v4.getClass();
                            componentName = null;
                        }
                        if (componentName == null) {
                            String str = v4.f2664b;
                            C0324l.d(str);
                            componentName = new ComponentName(str, "unknown");
                        }
                        w4.onServiceDisconnected(componentName);
                    }
                } finally {
                }
            }
            return true;
        }
        synchronized (this.f2672j.f2673d) {
            try {
                V v5 = (V) message.obj;
                W w5 = (W) this.f2672j.f2673d.get(v5);
                if (w5 != null && w5.f2666a.isEmpty()) {
                    if (w5.f2668c) {
                        w5.f2671g.f.removeMessages(1, w5.f2670e);
                        Y y4 = w5.f2671g;
                        y4.f2675g.b(y4.f2674e, w5);
                        w5.f2668c = false;
                        w5.f2667b = 2;
                    }
                    this.f2672j.f2673d.remove(v5);
                }
            } finally {
            }
        }
        return true;
    }
}
