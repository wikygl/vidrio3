package D1;

import android.os.SystemClock;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class P {

    /* renamed from: a  reason: collision with root package name */
    public long f645a;

    /* renamed from: b  reason: collision with root package name */
    public long f646b = Long.MIN_VALUE;

    /* renamed from: c  reason: collision with root package name */
    public final Object f647c = new Object();

    public P(long j4) {
        this.f645a = j4;
    }

    public final void a(long j4) {
        synchronized (this.f647c) {
            this.f645a = j4;
        }
    }

    public final boolean b() {
        synchronized (this.f647c) {
            try {
                z1.p.f6575A.f6584j.getClass();
                long elapsedRealtime = SystemClock.elapsedRealtime();
                if (this.f646b + this.f645a > elapsedRealtime) {
                    return false;
                }
                this.f646b = elapsedRealtime;
                return true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
