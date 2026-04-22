package E0;

import B0.v;
import C0.i;
import C0.o;
import D0.d;
import D0.k;
import L0.p;
import S0.C0284u0;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class c implements d, H0.c, D0.a {

    /* renamed from: r  reason: collision with root package name */
    public static final String f835r = i.e("GreedyScheduler");

    /* renamed from: j  reason: collision with root package name */
    public final Context f836j;

    /* renamed from: k  reason: collision with root package name */
    public final k f837k;

    /* renamed from: l  reason: collision with root package name */
    public final H0.d f838l;

    /* renamed from: n  reason: collision with root package name */
    public final b f840n;

    /* renamed from: o  reason: collision with root package name */
    public boolean f841o;

    /* renamed from: q  reason: collision with root package name */
    public Boolean f843q;

    /* renamed from: m  reason: collision with root package name */
    public final HashSet f839m = new HashSet();

    /* renamed from: p  reason: collision with root package name */
    public final Object f842p = new Object();

    public c(Context context, androidx.work.a aVar, O0.b bVar, k kVar) {
        this.f836j = context;
        this.f837k = kVar;
        this.f838l = new H0.d(context, bVar, this);
        this.f840n = new b(this, aVar.e);
    }

    @Override // D0.a
    public final void a(String str, boolean z4) {
        synchronized (this.f842p) {
            try {
                Iterator it = this.f839m.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    p pVar = (p) it.next();
                    if (pVar.f1450a.equals(str)) {
                        i c4 = i.c();
                        String str2 = f835r;
                        c4.a(str2, "Stopping tracking for " + str, new Throwable[0]);
                        this.f839m.remove(pVar);
                        this.f838l.c(this.f839m);
                        break;
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D0.d
    public final void b(String str) {
        Runnable runnable;
        Boolean bool = this.f843q;
        k kVar = this.f837k;
        if (bool == null) {
            this.f843q = Boolean.valueOf(M0.i.a(this.f836j, kVar.f586b));
        }
        boolean booleanValue = this.f843q.booleanValue();
        String str2 = f835r;
        if (!booleanValue) {
            i.c().d(str2, "Ignoring schedule request in non-main process", new Throwable[0]);
            return;
        }
        if (!this.f841o) {
            kVar.f.b(this);
            this.f841o = true;
        }
        i.c().a(str2, C0284u0.c("Cancelling work ID ", str), new Throwable[0]);
        b bVar = this.f840n;
        if (bVar != null && (runnable = (Runnable) bVar.f834c.remove(str)) != null) {
            ((Handler) bVar.f833b.f293j).removeCallbacks(runnable);
        }
        kVar.h(str);
    }

    @Override // H0.c
    public final void c(List<String> list) {
        for (String str : list) {
            i.c().a(f835r, C0284u0.c("Constraints not met: Cancelling work ID ", str), new Throwable[0]);
            this.f837k.h(str);
        }
    }

    @Override // H0.c
    public final void d(List<String> list) {
        Iterator it = ((ArrayList) list).iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            i.c().a(f835r, C0284u0.c("Constraints met: Scheduling work ID ", str), new Throwable[0]);
            this.f837k.g(str, null);
        }
    }

    @Override // D0.d
    public final boolean e() {
        return false;
    }

    @Override // D0.d
    public final void f(p... pVarArr) {
        if (this.f843q == null) {
            this.f843q = Boolean.valueOf(M0.i.a(this.f836j, this.f837k.f586b));
        }
        if (!this.f843q.booleanValue()) {
            i.c().d(f835r, "Ignoring schedule request in a secondary process", new Throwable[0]);
            return;
        }
        if (!this.f841o) {
            this.f837k.f.b(this);
            this.f841o = true;
        }
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        for (p pVar : pVarArr) {
            long a4 = pVar.a();
            long currentTimeMillis = System.currentTimeMillis();
            if (pVar.f1451b == o.f337j) {
                if (currentTimeMillis < a4) {
                    b bVar = this.f840n;
                    if (bVar != null) {
                        HashMap hashMap = bVar.f834c;
                        Runnable runnable = (Runnable) hashMap.remove(pVar.f1450a);
                        v vVar = bVar.f833b;
                        if (runnable != null) {
                            ((Handler) vVar.f293j).removeCallbacks(runnable);
                        }
                        a aVar = new a(bVar, 0, pVar);
                        hashMap.put(pVar.f1450a, aVar);
                        ((Handler) vVar.f293j).postDelayed(aVar, pVar.a() - System.currentTimeMillis());
                    }
                } else if (pVar.b()) {
                    int i4 = Build.VERSION.SDK_INT;
                    if (i4 >= 23 && pVar.f1458j.f306c) {
                        i.c().a(f835r, "Ignoring WorkSpec " + pVar + ", Requires device idle.", new Throwable[0]);
                    } else if (i4 >= 24 && pVar.f1458j.f310h.f311a.size() > 0) {
                        i.c().a(f835r, "Ignoring WorkSpec " + pVar + ", Requires ContentUri triggers.", new Throwable[0]);
                    } else {
                        hashSet.add(pVar);
                        hashSet2.add(pVar.f1450a);
                    }
                } else {
                    i.c().a(f835r, C0284u0.c("Starting work for ", pVar.f1450a), new Throwable[0]);
                    this.f837k.g(pVar.f1450a, null);
                }
            }
        }
        synchronized (this.f842p) {
            try {
                if (!hashSet.isEmpty()) {
                    i.c().a(f835r, "Starting tracking for [" + TextUtils.join(",", hashSet2) + "]", new Throwable[0]);
                    this.f839m.addAll(hashSet);
                    this.f838l.c(this.f839m);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
