package M0;

import androidx.work.impl.WorkDatabase;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class l implements Runnable {

    /* renamed from: m  reason: collision with root package name */
    public static final String f1683m = C0.i.e("StopWorkRunnable");

    /* renamed from: j  reason: collision with root package name */
    public final D0.k f1684j;

    /* renamed from: k  reason: collision with root package name */
    public final String f1685k;

    /* renamed from: l  reason: collision with root package name */
    public final boolean f1686l;

    public l(D0.k kVar, String str, boolean z4) {
        this.f1684j = kVar;
        this.f1685k = str;
        this.f1686l = z4;
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean containsKey;
        boolean k4;
        D0.k kVar = this.f1684j;
        WorkDatabase workDatabase = kVar.f587c;
        D0.c cVar = kVar.f;
        L0.q n4 = workDatabase.n();
        workDatabase.c();
        try {
            String str = this.f1685k;
            synchronized (cVar.f566t) {
                containsKey = cVar.f561o.containsKey(str);
            }
            if (this.f1686l) {
                k4 = this.f1684j.f.j(this.f1685k);
            } else {
                if (!containsKey) {
                    L0.r rVar = (L0.r) n4;
                    if (rVar.f(this.f1685k) == C0.o.f338k) {
                        rVar.p(C0.o.f337j, this.f1685k);
                    }
                }
                k4 = this.f1684j.f.k(this.f1685k);
            }
            C0.i c4 = C0.i.c();
            String str2 = f1683m;
            String str3 = this.f1685k;
            c4.a(str2, "StopWorkRunnable for " + str3 + "; Processor.stopWork = " + k4, new Throwable[0]);
            workDatabase.h();
            workDatabase.f();
        } catch (Throwable th) {
            workDatabase.f();
            throw th;
        }
    }
}
