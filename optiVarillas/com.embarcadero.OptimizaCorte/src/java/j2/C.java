package j2;

import android.os.SystemClock;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class C implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final long f4773j;

    /* renamed from: k  reason: collision with root package name */
    public final long f4774k;

    /* renamed from: l  reason: collision with root package name */
    public final boolean f4775l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ G f4776m;

    public C(G g4, boolean z4) {
        this.f4776m = g4;
        g4.f4785b.getClass();
        this.f4773j = System.currentTimeMillis();
        g4.f4785b.getClass();
        this.f4774k = SystemClock.elapsedRealtime();
        this.f4775l = z4;
    }

    public abstract void a();

    @Override // java.lang.Runnable
    public final void run() {
        G g4 = this.f4776m;
        if (g4.f) {
            b();
            return;
        }
        try {
            a();
        } catch (Exception e4) {
            g4.a(e4, false, this.f4775l);
            b();
        }
    }

    public void b() {
    }
}
