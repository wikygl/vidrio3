package M0;

import android.content.Context;
import android.util.Pair;
import androidx.work.impl.WorkDatabase;
import com.google.android.gms.internal.ads.MY;
import com.google.android.gms.internal.ads.Vw;
import com.google.android.gms.internal.ads.Xh;
import com.google.android.gms.internal.ads.n10;
import com.google.android.gms.internal.ads.s10;
import com.google.android.gms.internal.ads.v10;
import com.google.android.gms.internal.ads.vj;
import java.util.UUID;
import m0.AbstractC0731g;
import t1.C0802d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class q implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1709j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f1710k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f1711l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ Object f1712m;

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ Object f1713n;

    public /* synthetic */ q(Object obj, Object obj2, Object obj3, Object obj4, int i4) {
        this.f1709j = i4;
        this.f1710k = obj;
        this.f1711l = obj2;
        this.f1712m = obj3;
        this.f1713n = obj4;
    }

    @Override // java.lang.Runnable
    public final void run() {
        L0.p i4;
        Object obj = this.f1713n;
        Object obj2 = this.f1712m;
        Object obj3 = this.f1711l;
        Object obj4 = this.f1710k;
        switch (this.f1709j) {
            case 0:
                N0.c cVar = (N0.c) obj2;
                UUID uuid = (UUID) obj4;
                String uuid2 = uuid.toString();
                C0.i c4 = C0.i.c();
                String str = r.f1714c;
                StringBuilder sb = new StringBuilder("Updating progress for ");
                sb.append(uuid);
                sb.append(" (");
                androidx.work.b bVar = (androidx.work.b) obj3;
                sb.append(bVar);
                sb.append(")");
                c4.a(str, sb.toString(), new Throwable[0]);
                r rVar = (r) obj;
                WorkDatabase workDatabase = rVar.f1715a;
                WorkDatabase workDatabase2 = rVar.f1715a;
                workDatabase.c();
                try {
                    i4 = ((L0.r) workDatabase2.n()).i(uuid2);
                } finally {
                    try {
                        return;
                    } finally {
                    }
                }
                if (i4 != null) {
                    if (i4.f1451b == C0.o.f338k) {
                        L0.m mVar = new L0.m(uuid2, bVar);
                        L0.o oVar = (L0.o) workDatabase2.m();
                        AbstractC0731g abstractC0731g = oVar.f1446a;
                        abstractC0731g.b();
                        abstractC0731g.c();
                        oVar.f1447b.e(mVar);
                        abstractC0731g.h();
                        abstractC0731g.f();
                    } else {
                        C0.i c5 = C0.i.c();
                        c5.f(str, "Ignoring setProgressAsync(...). WorkSpec (" + uuid2 + ") is not in a RUNNING state.", new Throwable[0]);
                    }
                    cVar.j(null);
                    workDatabase2.h();
                    return;
                }
                throw new IllegalStateException("Calls to setProgressAsync() must complete before a ListenableWorker signals completion of work by returning an instance of Result.");
            case 1:
                Context context = (Context) obj4;
                try {
                    new vj(context, (String) obj3).d(((C0802d) obj2).f5787a, (Vw) obj);
                    return;
                } catch (IllegalStateException e4) {
                    Xh.b(context).a("RewardedInterstitialAd.load", e4);
                    return;
                }
            default:
                Pair pair = (Pair) obj3;
                ((MY) obj4).k.h.o(((Integer) pair.first).intValue(), (v10) pair.second, (n10) obj2, (s10) obj);
                return;
        }
    }

    public q(r rVar, UUID uuid, androidx.work.b bVar, N0.c cVar) {
        this.f1709j = 0;
        this.f1713n = rVar;
        this.f1710k = uuid;
        this.f1711l = bVar;
        this.f1712m = cVar;
    }
}
