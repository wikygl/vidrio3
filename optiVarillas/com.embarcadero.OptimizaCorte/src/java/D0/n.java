package D0;

import C0.o;
import L0.o;
import L0.p;
import L0.q;
import L0.r;
import L0.t;
import S0.C0284u0;
import a3.InterfaceFutureC0346a;
import android.content.Context;
import android.database.Cursor;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import androidx.work.impl.WorkDatabase;
import androidx.work.impl.background.systemalarm.RescheduleReceiver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import m0.AbstractC0731g;
import m0.C0733i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class n implements Runnable {

    /* renamed from: C  reason: collision with root package name */
    public static final String f600C = C0.i.e("WorkerWrapper");

    /* renamed from: A  reason: collision with root package name */
    public InterfaceFutureC0346a<ListenableWorker.a> f601A;

    /* renamed from: B  reason: collision with root package name */
    public volatile boolean f602B;

    /* renamed from: j  reason: collision with root package name */
    public Context f603j;

    /* renamed from: k  reason: collision with root package name */
    public String f604k;

    /* renamed from: l  reason: collision with root package name */
    public List<d> f605l;

    /* renamed from: m  reason: collision with root package name */
    public WorkerParameters.a f606m;

    /* renamed from: n  reason: collision with root package name */
    public p f607n;

    /* renamed from: o  reason: collision with root package name */
    public ListenableWorker f608o;

    /* renamed from: p  reason: collision with root package name */
    public O0.a f609p;

    /* renamed from: q  reason: collision with root package name */
    public ListenableWorker.a f610q;

    /* renamed from: r  reason: collision with root package name */
    public androidx.work.a f611r;

    /* renamed from: s  reason: collision with root package name */
    public K0.a f612s;

    /* renamed from: t  reason: collision with root package name */
    public WorkDatabase f613t;

    /* renamed from: u  reason: collision with root package name */
    public q f614u;

    /* renamed from: v  reason: collision with root package name */
    public L0.b f615v;

    /* renamed from: w  reason: collision with root package name */
    public t f616w;

    /* renamed from: x  reason: collision with root package name */
    public ArrayList f617x;

    /* renamed from: y  reason: collision with root package name */
    public String f618y;

    /* renamed from: z  reason: collision with root package name */
    public N0.c<Boolean> f619z;

    public final void a(ListenableWorker.a aVar) {
        boolean z4 = aVar instanceof ListenableWorker.a.c;
        String str = f600C;
        if (z4) {
            C0.i.c().d(str, C0284u0.c("Worker result SUCCESS for ", this.f618y), new Throwable[0]);
            if (this.f607n.c()) {
                e();
                return;
            }
            L0.b bVar = this.f615v;
            String str2 = this.f604k;
            q qVar = this.f614u;
            WorkDatabase workDatabase = this.f613t;
            workDatabase.c();
            try {
                ((r) qVar).p(o.f339l, str2);
                ((r) qVar).n(str2, this.f610q.a);
                long currentTimeMillis = System.currentTimeMillis();
                Iterator it = ((L0.c) bVar).a(str2).iterator();
                while (it.hasNext()) {
                    String str3 = (String) it.next();
                    if (((r) qVar).f(str3) == o.f341n && ((L0.c) bVar).b(str3)) {
                        C0.i c4 = C0.i.c();
                        c4.d(str, "Setting status to enqueued for " + str3, new Throwable[0]);
                        ((r) qVar).p(o.f337j, str3);
                        ((r) qVar).o(str3, currentTimeMillis);
                    }
                }
                workDatabase.h();
                workDatabase.f();
                f(false);
            } catch (Throwable th) {
                workDatabase.f();
                f(false);
                throw th;
            }
        } else if (aVar instanceof ListenableWorker.a.b) {
            C0.i.c().d(str, C0284u0.c("Worker result RETRY for ", this.f618y), new Throwable[0]);
            d();
        } else {
            C0.i.c().d(str, C0284u0.c("Worker result FAILURE for ", this.f618y), new Throwable[0]);
            if (this.f607n.c()) {
                e();
            } else {
                h();
            }
        }
    }

    public final void b(String str) {
        LinkedList linkedList = new LinkedList();
        linkedList.add(str);
        while (!linkedList.isEmpty()) {
            String str2 = (String) linkedList.remove();
            r rVar = (r) this.f614u;
            if (rVar.f(str2) != o.f342o) {
                rVar.p(o.f340m, str2);
            }
            linkedList.addAll(((L0.c) this.f615v).a(str2));
        }
    }

    public final void c() {
        boolean i4 = i();
        String str = this.f604k;
        WorkDatabase workDatabase = this.f613t;
        if (!i4) {
            workDatabase.c();
            try {
                o f = ((r) this.f614u).f(str);
                L0.o oVar = (L0.o) workDatabase.m();
                AbstractC0731g abstractC0731g = oVar.f1446a;
                abstractC0731g.b();
                o.b bVar = oVar.f1448c;
                r0.e a4 = bVar.a();
                if (str == null) {
                    a4.f(1);
                } else {
                    a4.g(str, 1);
                }
                abstractC0731g.c();
                a4.i();
                abstractC0731g.h();
                abstractC0731g.f();
                bVar.c(a4);
                if (f == null) {
                    f(false);
                } else if (f == C0.o.f338k) {
                    a(this.f610q);
                } else if (!f.a()) {
                    d();
                }
                workDatabase.h();
                workDatabase.f();
            } catch (Throwable th) {
                workDatabase.f();
                throw th;
            }
        }
        List<d> list = this.f605l;
        if (list != null) {
            for (d dVar : list) {
                dVar.b(str);
            }
            e.a(this.f611r, workDatabase, list);
        }
    }

    public final void d() {
        String str = this.f604k;
        q qVar = this.f614u;
        WorkDatabase workDatabase = this.f613t;
        workDatabase.c();
        try {
            ((r) qVar).p(C0.o.f337j, str);
            ((r) qVar).o(str, System.currentTimeMillis());
            ((r) qVar).l(str, -1L);
            workDatabase.h();
        } finally {
            workDatabase.f();
            f(true);
        }
    }

    public final void e() {
        String str = this.f604k;
        q qVar = this.f614u;
        WorkDatabase workDatabase = this.f613t;
        workDatabase.c();
        try {
            ((r) qVar).o(str, System.currentTimeMillis());
            ((r) qVar).p(C0.o.f337j, str);
            ((r) qVar).m(str);
            ((r) qVar).l(str, -1L);
            workDatabase.h();
        } finally {
            workDatabase.f();
            f(false);
        }
    }

    public final void f(boolean z4) {
        boolean z5;
        ListenableWorker listenableWorker;
        this.f613t.c();
        try {
            r rVar = (r) this.f613t.n();
            rVar.getClass();
            C0733i b4 = C0733i.b("SELECT COUNT(*) > 0 FROM workspec WHERE state NOT IN (2, 3, 5) LIMIT 1", 0);
            AbstractC0731g abstractC0731g = rVar.f1469a;
            abstractC0731g.b();
            Cursor g4 = abstractC0731g.g(b4);
            if (g4.moveToFirst() && g4.getInt(0) != 0) {
                z5 = true;
            } else {
                z5 = false;
            }
            g4.close();
            b4.k();
            if (!z5) {
                M0.g.a(this.f603j, RescheduleReceiver.class, false);
            }
            if (z4) {
                ((r) this.f614u).p(C0.o.f337j, this.f604k);
                ((r) this.f614u).l(this.f604k, -1L);
            }
            if (this.f607n != null && (listenableWorker = this.f608o) != null && listenableWorker.isRunInForeground()) {
                K0.a aVar = this.f612s;
                String str = this.f604k;
                c cVar = (c) aVar;
                synchronized (cVar.f566t) {
                    cVar.f561o.remove(str);
                    cVar.i();
                }
            }
            this.f613t.h();
            this.f613t.f();
            this.f619z.j(Boolean.valueOf(z4));
        } catch (Throwable th) {
            this.f613t.f();
            throw th;
        }
    }

    public final void g() {
        String str = this.f604k;
        C0.o f = ((r) this.f614u).f(str);
        C0.o oVar = C0.o.f338k;
        String str2 = f600C;
        if (f == oVar) {
            C0.i.c().a(str2, C.b.b("Status for ", str, " is RUNNING;not doing any work and rescheduling for later execution"), new Throwable[0]);
            f(true);
            return;
        }
        C0.i c4 = C0.i.c();
        c4.a(str2, "Status for " + str + " is " + f + "; not doing any work", new Throwable[0]);
        f(false);
    }

    public final void h() {
        String str = this.f604k;
        WorkDatabase workDatabase = this.f613t;
        workDatabase.c();
        try {
            b(str);
            ((r) this.f614u).n(str, this.f610q.a);
            workDatabase.h();
        } finally {
            workDatabase.f();
            f(false);
        }
    }

    public final boolean i() {
        if (!this.f602B) {
            return false;
        }
        C0.i.c().a(f600C, C0284u0.c("Work interrupted for ", this.f618y), new Throwable[0]);
        C0.o f = ((r) this.f614u).f(this.f604k);
        if (f == null) {
            f(false);
        } else {
            f(!f.a());
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x00bd, code lost:
        if (r6.f1459k > 0) goto L94;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:40:0x010b  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0111  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01ed  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x01fd  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0216  */
    /* JADX WARN: Type inference failed for: r0v36, types: [N0.a, N0.c, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r8v8, types: [androidx.work.WorkerParameters, java.lang.Object] */
    @Override // java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void run() {
        /*
            Method dump skipped, instructions count: 707
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: D0.n.run():void");
    }
}
