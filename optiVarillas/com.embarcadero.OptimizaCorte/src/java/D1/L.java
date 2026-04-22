package D1;

import W1.C0324l;
import android.os.HandlerThread;
import android.os.Looper;
import com.google.android.gms.internal.ads.WJ;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class L {

    /* renamed from: a  reason: collision with root package name */
    public HandlerThread f638a = null;

    /* renamed from: b  reason: collision with root package name */
    public WJ f639b = null;

    /* renamed from: c  reason: collision with root package name */
    public int f640c = 0;

    /* renamed from: d  reason: collision with root package name */
    public final Object f641d = new Object();

    public final Looper a() {
        Looper looper;
        synchronized (this.f641d) {
            try {
                if (this.f640c == 0) {
                    if (this.f638a == null) {
                        C0183d0.k("Starting the looper thread.");
                        HandlerThread handlerThread = new HandlerThread("LooperProvider");
                        this.f638a = handlerThread;
                        handlerThread.start();
                        this.f639b = new WJ(this.f638a.getLooper());
                        C0183d0.k("Looper thread started.");
                    } else {
                        C0183d0.k("Resuming the looper thread");
                        this.f641d.notifyAll();
                    }
                } else {
                    C0324l.e(this.f638a, "Invalid state: handlerThread should already been initialized.");
                }
                this.f640c++;
                looper = this.f638a.getLooper();
            } catch (Throwable th) {
                throw th;
            }
        }
        return looper;
    }
}
