package D0;

import C.a;
import L0.p;
import S0.C0284u0;
import a3.InterfaceFutureC0346a;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import androidx.work.impl.WorkDatabase;
import androidx.work.impl.foreground.SystemForegroundService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class c implements D0.a, K0.a {

    /* renamed from: u  reason: collision with root package name */
    public static final String f555u = C0.i.e("Processor");

    /* renamed from: k  reason: collision with root package name */
    public final Context f557k;

    /* renamed from: l  reason: collision with root package name */
    public final androidx.work.a f558l;

    /* renamed from: m  reason: collision with root package name */
    public final O0.a f559m;

    /* renamed from: n  reason: collision with root package name */
    public final WorkDatabase f560n;

    /* renamed from: q  reason: collision with root package name */
    public final List<d> f563q;

    /* renamed from: p  reason: collision with root package name */
    public final HashMap f562p = new HashMap();

    /* renamed from: o  reason: collision with root package name */
    public final HashMap f561o = new HashMap();

    /* renamed from: r  reason: collision with root package name */
    public final HashSet f564r = new HashSet();

    /* renamed from: s  reason: collision with root package name */
    public final ArrayList f565s = new ArrayList();

    /* renamed from: j  reason: collision with root package name */
    public PowerManager.WakeLock f556j = null;

    /* renamed from: t  reason: collision with root package name */
    public final Object f566t = new Object();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public D0.a f567j;

        /* renamed from: k  reason: collision with root package name */
        public String f568k;

        /* renamed from: l  reason: collision with root package name */
        public InterfaceFutureC0346a<Boolean> f569l;

        @Override // java.lang.Runnable
        public final void run() {
            boolean z4;
            try {
                z4 = this.f569l.get().booleanValue();
            } catch (InterruptedException | ExecutionException unused) {
                z4 = true;
            }
            this.f567j.a(this.f568k, z4);
        }
    }

    public c(Context context, androidx.work.a aVar, O0.b bVar, WorkDatabase workDatabase, List list) {
        this.f557k = context;
        this.f558l = aVar;
        this.f559m = bVar;
        this.f560n = workDatabase;
        this.f563q = list;
    }

    public static boolean c(String str, n nVar) {
        boolean z4;
        if (nVar != null) {
            nVar.f602B = true;
            nVar.i();
            InterfaceFutureC0346a<ListenableWorker.a> interfaceFutureC0346a = nVar.f601A;
            if (interfaceFutureC0346a != null) {
                z4 = interfaceFutureC0346a.isDone();
                nVar.f601A.cancel(true);
            } else {
                z4 = false;
            }
            ListenableWorker listenableWorker = nVar.f608o;
            if (listenableWorker != null && !z4) {
                listenableWorker.stop();
            } else {
                p pVar = nVar.f607n;
                C0.i.c().a(n.f600C, "WorkSpec " + pVar + " is already done. Not interrupting.", new Throwable[0]);
            }
            C0.i.c().a(f555u, C0284u0.c("WorkerWrapper interrupted for ", str), new Throwable[0]);
            return true;
        }
        C0.i.c().a(f555u, C0284u0.c("WorkerWrapper could not be found for ", str), new Throwable[0]);
        return false;
    }

    @Override // D0.a
    public final void a(String str, boolean z4) {
        synchronized (this.f566t) {
            try {
                this.f562p.remove(str);
                C0.i c4 = C0.i.c();
                String str2 = f555u;
                String simpleName = c.class.getSimpleName();
                c4.a(str2, simpleName + " " + str + " executed; reschedule = " + z4, new Throwable[0]);
                Iterator it = this.f565s.iterator();
                while (it.hasNext()) {
                    ((D0.a) it.next()).a(str, z4);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void b(D0.a aVar) {
        synchronized (this.f566t) {
            this.f565s.add(aVar);
        }
    }

    public final boolean d(String str) {
        boolean contains;
        synchronized (this.f566t) {
            contains = this.f564r.contains(str);
        }
        return contains;
    }

    public final boolean e(String str) {
        boolean z4;
        synchronized (this.f566t) {
            try {
                if (!this.f562p.containsKey(str) && !this.f561o.containsKey(str)) {
                    z4 = false;
                }
                z4 = true;
            } finally {
            }
        }
        return z4;
    }

    public final void f(D0.a aVar) {
        synchronized (this.f566t) {
            this.f565s.remove(aVar);
        }
    }

    public final void g(String str, C0.f fVar) {
        synchronized (this.f566t) {
            try {
                C0.i c4 = C0.i.c();
                String str2 = f555u;
                c4.d(str2, "Moving WorkSpec (" + str + ") to the foreground", new Throwable[0]);
                n nVar = (n) this.f562p.remove(str);
                if (nVar != null) {
                    if (this.f556j == null) {
                        PowerManager.WakeLock a4 = M0.m.a(this.f557k, "ProcessorForegroundLck");
                        this.f556j = a4;
                        a4.acquire();
                    }
                    this.f561o.put(str, nVar);
                    Intent e4 = androidx.work.impl.foreground.a.e(this.f557k, str, fVar);
                    Context context = this.f557k;
                    if (Build.VERSION.SDK_INT >= 26) {
                        a.c.b(context, e4);
                    } else {
                        context.startService(e4);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.lang.Object, D0.c$a, java.lang.Runnable] */
    /* JADX WARN: Type inference failed for: r6v1, types: [D0.n, java.lang.Object, java.lang.Runnable] */
    /* JADX WARN: Type inference failed for: r8v1, types: [N0.a, N0.c<java.lang.Boolean>] */
    public final boolean h(String str, WorkerParameters.a aVar) {
        synchronized (this.f566t) {
            try {
                if (e(str)) {
                    C0.i c4 = C0.i.c();
                    String str2 = f555u;
                    c4.a(str2, "Work " + str + " is already enqueued for processing", new Throwable[0]);
                    return false;
                }
                Context context = this.f557k;
                androidx.work.a aVar2 = this.f558l;
                O0.a aVar3 = this.f559m;
                WorkDatabase workDatabase = this.f560n;
                WorkerParameters.a aVar4 = new WorkerParameters.a();
                Context applicationContext = context.getApplicationContext();
                List<d> list = this.f563q;
                if (aVar == null) {
                    aVar = aVar4;
                }
                ?? obj = new Object();
                obj.f610q = new ListenableWorker.a.a();
                obj.f619z = new N0.a();
                obj.f601A = null;
                obj.f603j = applicationContext;
                obj.f609p = aVar3;
                obj.f612s = this;
                obj.f604k = str;
                obj.f605l = list;
                obj.f606m = aVar;
                obj.f608o = null;
                obj.f611r = aVar2;
                obj.f613t = workDatabase;
                obj.f614u = workDatabase.n();
                obj.f615v = workDatabase.i();
                obj.f616w = workDatabase.o();
                N0.c<Boolean> cVar = obj.f619z;
                ?? obj2 = new Object();
                obj2.f567j = this;
                obj2.f568k = str;
                obj2.f569l = cVar;
                cVar.a(obj2, ((O0.b) this.f559m).f1796c);
                this.f562p.put(str, obj);
                ((O0.b) this.f559m).f1794a.execute(obj);
                C0.i.c().a(f555u, X1.b.e(c.class.getSimpleName(), ": processing ", str), new Throwable[0]);
                return true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void i() {
        synchronized (this.f566t) {
            try {
                if (!(!this.f561o.isEmpty())) {
                    Context context = this.f557k;
                    String str = androidx.work.impl.foreground.a.s;
                    Intent intent = new Intent(context, SystemForegroundService.class);
                    intent.setAction("ACTION_STOP_FOREGROUND");
                    this.f557k.startService(intent);
                    PowerManager.WakeLock wakeLock = this.f556j;
                    if (wakeLock != null) {
                        wakeLock.release();
                        this.f556j = null;
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final boolean j(String str) {
        boolean c4;
        synchronized (this.f566t) {
            C0.i c5 = C0.i.c();
            String str2 = f555u;
            c5.a(str2, "Processor stopping foreground work " + str, new Throwable[0]);
            c4 = c(str, (n) this.f561o.remove(str));
        }
        return c4;
    }

    public final boolean k(String str) {
        boolean c4;
        synchronized (this.f566t) {
            C0.i c5 = C0.i.c();
            String str2 = f555u;
            c5.a(str2, "Processor stopping background work " + str, new Throwable[0]);
            c4 = c(str, (n) this.f562p.remove(str));
        }
        return c4;
    }
}
