package M0;

import C0.l;
import androidx.work.b;
import androidx.work.impl.WorkDatabase;
import androidx.work.impl.background.systemalarm.RescheduleReceiver;
import androidx.work.impl.workers.ConstraintTrackingWorker;
import java.util.HashSet;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class e implements Runnable {

    /* renamed from: l  reason: collision with root package name */
    public static final String f1667l = C0.i.e("EnqueueRunnable");

    /* renamed from: j  reason: collision with root package name */
    public final D0.f f1668j;

    /* renamed from: k  reason: collision with root package name */
    public final D0.b f1669k = new D0.b();

    public e(D0.f fVar) {
        this.f1668j = fVar;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:115:0x0221  */
    /* JADX WARN: Removed duplicated region for block: B:149:0x0292  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x02b0  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x02fe  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x032e  */
    /* JADX WARN: Removed duplicated region for block: B:216:0x0356 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:224:0x01ad A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0194  */
    /* JADX WARN: Type inference failed for: r5v9, types: [java.util.List] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean a(D0.f r25) {
        /*
            Method dump skipped, instructions count: 879
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: M0.e.a(D0.f):boolean");
    }

    public static void b(L0.p pVar) {
        C0.c cVar = pVar.f1458j;
        String str = pVar.f1452c;
        if (!str.equals(ConstraintTrackingWorker.class.getName())) {
            if (cVar.f307d || cVar.f308e) {
                b.a aVar = new b.a();
                aVar.a(pVar.f1454e.a);
                aVar.a.put("androidx.work.impl.workers.ConstraintTrackingWorker.ARGUMENT_CLASS_NAME", str);
                pVar.f1452c = ConstraintTrackingWorker.class.getName();
                androidx.work.b bVar = new androidx.work.b(aVar.a);
                androidx.work.b.c(bVar);
                pVar.f1454e = bVar;
            }
        }
    }

    @Override // java.lang.Runnable
    public final void run() {
        D0.b bVar = this.f1669k;
        D0.f fVar = this.f1668j;
        try {
            fVar.getClass();
            D0.k kVar = fVar.f572a;
            if (!D0.f.b(fVar, new HashSet())) {
                WorkDatabase workDatabase = kVar.f587c;
                workDatabase.c();
                boolean a4 = a(fVar);
                workDatabase.h();
                workDatabase.f();
                if (a4) {
                    g.a(kVar.f585a, RescheduleReceiver.class, true);
                    D0.e.a(kVar.f586b, kVar.f587c, kVar.f589e);
                }
                bVar.a(C0.l.f331a);
                return;
            }
            throw new IllegalStateException("WorkContinuation has cycles (" + fVar + ")");
        } catch (Throwable th) {
            bVar.a(new l.a.C0003a(th));
        }
    }
}
