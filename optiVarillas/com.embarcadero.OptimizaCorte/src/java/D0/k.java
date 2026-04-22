package D0;

import C0.i;
import C0.p;
import L0.r;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import androidx.work.WorkerParameters;
import androidx.work.a;
import androidx.work.impl.WorkDatabase;
import androidx.work.impl.a;
import androidx.work.impl.background.systemalarm.SystemAlarmService;
import androidx.work.impl.background.systemjob.SystemJobService;
import androidx.work.impl.utils.ForceStopRunnable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import m.C0723b;
import m.ExecutorC0722a;
import m0.AbstractC0731g;
import m0.C0725a;
import m0.C0734j;
import q0.InterfaceC0767b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class k extends p {

    /* renamed from: j  reason: collision with root package name */
    public static k f582j;

    /* renamed from: k  reason: collision with root package name */
    public static k f583k;

    /* renamed from: l  reason: collision with root package name */
    public static final Object f584l;

    /* renamed from: a  reason: collision with root package name */
    public final Context f585a;

    /* renamed from: b  reason: collision with root package name */
    public final androidx.work.a f586b;

    /* renamed from: c  reason: collision with root package name */
    public final WorkDatabase f587c;

    /* renamed from: d  reason: collision with root package name */
    public final O0.a f588d;

    /* renamed from: e  reason: collision with root package name */
    public final List<d> f589e;
    public final c f;

    /* renamed from: g  reason: collision with root package name */
    public final M0.h f590g;

    /* renamed from: h  reason: collision with root package name */
    public boolean f591h;

    /* renamed from: i  reason: collision with root package name */
    public BroadcastReceiver.PendingResult f592i;

    static {
        C0.i.e("WorkManagerImpl");
        f582j = null;
        f583k = null;
        f584l = new Object();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v24, types: [java.lang.Object, q0.b$c] */
    /* JADX WARN: Type inference failed for: r6v11 */
    /* JADX WARN: Type inference failed for: r6v3 */
    /* JADX WARN: Type inference failed for: r6v4, types: [boolean] */
    /* JADX WARN: Type inference failed for: r6v7 */
    /* JADX WARN: Type inference failed for: r6v8 */
    public k(Context context, androidx.work.a aVar, O0.b bVar) {
        AbstractC0731g.a aVar2;
        Executor executor;
        AbstractC0731g.c cVar;
        String str;
        boolean z4;
        ?? r6;
        int i4;
        d dVar;
        boolean isDeviceProtectedStorage;
        boolean z5 = context.getResources().getBoolean(2130968582);
        Context applicationContext = context.getApplicationContext();
        M0.j jVar = bVar.f1794a;
        int i5 = WorkDatabase.k;
        if (z5) {
            aVar2 = new AbstractC0731g.a(applicationContext, null);
            aVar2.f5317h = true;
        } else {
            String str2 = i.f580a;
            aVar2 = new AbstractC0731g.a(applicationContext, "androidx.work.workdb");
            aVar2.f5316g = new g(applicationContext);
        }
        aVar2.f5315e = jVar;
        AbstractC0731g.b bVar2 = new AbstractC0731g.b();
        if (aVar2.f5314d == null) {
            aVar2.f5314d = new ArrayList<>();
        }
        aVar2.f5314d.add(bVar2);
        aVar2.a(androidx.work.impl.a.a);
        aVar2.a(new a.h(applicationContext, 2, 3));
        aVar2.a(androidx.work.impl.a.b);
        aVar2.a(androidx.work.impl.a.c);
        aVar2.a(new a.h(applicationContext, 5, 6));
        aVar2.a(androidx.work.impl.a.d);
        aVar2.a(androidx.work.impl.a.e);
        aVar2.a(androidx.work.impl.a.f);
        aVar2.a(new a.i(applicationContext));
        aVar2.a(new a.h(applicationContext, 10, 11));
        aVar2.a(androidx.work.impl.a.g);
        aVar2.f5318i = false;
        aVar2.f5319j = true;
        Context context2 = aVar2.f5313c;
        if (context2 != null) {
            Class cls = aVar2.f5311a;
            if (cls != null) {
                Executor executor2 = aVar2.f5315e;
                if (executor2 == null && aVar2.f == null) {
                    ExecutorC0722a executorC0722a = C0723b.f5273m;
                    aVar2.f = executorC0722a;
                    aVar2.f5315e = executorC0722a;
                } else if (executor2 != null && aVar2.f == null) {
                    aVar2.f = executor2;
                } else if (executor2 == null && (executor = aVar2.f) != null) {
                    aVar2.f5315e = executor;
                }
                if (aVar2.f5316g == null) {
                    aVar2.f5316g = new Object();
                }
                InterfaceC0767b.c cVar2 = aVar2.f5316g;
                ArrayList<AbstractC0731g.b> arrayList = aVar2.f5314d;
                boolean z6 = aVar2.f5317h;
                ActivityManager activityManager = (ActivityManager) context2.getSystemService("activity");
                AbstractC0731g.c cVar3 = AbstractC0731g.c.f5323k;
                if (activityManager != null && !activityManager.isLowRamDevice()) {
                    cVar = cVar3;
                } else {
                    cVar = AbstractC0731g.c.f5322j;
                }
                Executor executor3 = aVar2.f5315e;
                AbstractC0731g.c cVar4 = cVar;
                C0725a c0725a = new C0725a(context2, aVar2.f5312b, cVar2, aVar2.f5320k, arrayList, z6, cVar4, executor3, aVar2.f, aVar2.f5318i, aVar2.f5319j);
                String name = cls.getPackage().getName();
                String canonicalName = cls.getCanonicalName();
                canonicalName = name.isEmpty() ? canonicalName : canonicalName.substring(name.length() + 1);
                String str3 = canonicalName.replace('.', '_') + "_Impl";
                try {
                    if (name.isEmpty()) {
                        str = str3;
                    } else {
                        str = name + "." + str3;
                    }
                    WorkDatabase workDatabase = (AbstractC0731g) Class.forName(str).newInstance();
                    InterfaceC0767b e4 = workDatabase.e(c0725a);
                    workDatabase.f5305c = e4;
                    if (e4 instanceof C0734j) {
                        ((C0734j) e4).getClass();
                    }
                    if (cVar4 == cVar3) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    e4.setWriteAheadLoggingEnabled(z4);
                    workDatabase.f5308g = arrayList;
                    workDatabase.f5304b = executor3;
                    new ArrayDeque();
                    workDatabase.f5307e = z6;
                    workDatabase.f = z4;
                    WorkDatabase workDatabase2 = workDatabase;
                    Context applicationContext2 = context.getApplicationContext();
                    i.a aVar3 = new i.a(aVar.f);
                    synchronized (C0.i.class) {
                        C0.i.f322a = aVar3;
                    }
                    int i6 = Build.VERSION.SDK_INT;
                    String str4 = e.f570a;
                    if (i6 >= 23) {
                        dVar = new G0.g(applicationContext2, this);
                        M0.g.a(applicationContext2, SystemJobService.class, true);
                        C0.i.c().a(str4, "Created SystemJobScheduler and enabled SystemJobService", new Throwable[0]);
                        r6 = 1;
                        i4 = 0;
                    } else {
                        try {
                            dVar = (d) Class.forName("androidx.work.impl.background.gcm.GcmScheduler").getConstructor(Context.class).newInstance(applicationContext2);
                            C0.i.c().a(str4, "Created androidx.work.impl.background.gcm.GcmScheduler", new Throwable[0]);
                            r6 = 1;
                            i4 = 0;
                        } catch (Throwable th) {
                            r6 = 1;
                            i4 = 0;
                            C0.i.c().a(str4, "Unable to create GCM Scheduler", th);
                            dVar = null;
                        }
                        if (dVar == null) {
                            dVar = new F0.b(applicationContext2);
                            M0.g.a(applicationContext2, SystemAlarmService.class, r6);
                            C0.i.c().a(str4, "Created SystemAlarmScheduler", new Throwable[i4]);
                        }
                    }
                    E0.c cVar5 = new E0.c(applicationContext2, aVar, bVar, this);
                    d[] dVarArr = new d[2];
                    dVarArr[i4] = dVar;
                    dVarArr[r6] = cVar5;
                    List<d> asList = Arrays.asList(dVarArr);
                    c cVar6 = new c(context, aVar, bVar, workDatabase2, asList);
                    Context applicationContext3 = context.getApplicationContext();
                    this.f585a = applicationContext3;
                    this.f586b = aVar;
                    this.f588d = bVar;
                    this.f587c = workDatabase2;
                    this.f589e = asList;
                    this.f = cVar6;
                    this.f590g = new M0.h(workDatabase2);
                    this.f591h = false;
                    if (Build.VERSION.SDK_INT >= 24) {
                        isDeviceProtectedStorage = applicationContext3.isDeviceProtectedStorage();
                        if (isDeviceProtectedStorage) {
                            throw new IllegalStateException("Cannot initialize WorkManager in direct boot mode");
                        }
                    }
                    ((O0.b) this.f588d).a(new ForceStopRunnable(applicationContext3, this));
                    return;
                } catch (ClassNotFoundException unused) {
                    throw new RuntimeException("cannot find implementation for " + cls.getCanonicalName() + ". " + str3 + " does not exist");
                } catch (IllegalAccessException unused2) {
                    throw new RuntimeException("Cannot access the constructor" + cls.getCanonicalName());
                } catch (InstantiationException unused3) {
                    throw new RuntimeException("Failed to create an instance of " + cls.getCanonicalName());
                }
            }
            throw new IllegalArgumentException("Must provide an abstract class that extends RoomDatabase");
        }
        throw new IllegalArgumentException("Cannot provide null context for the database.");
    }

    @Deprecated
    public static k b() {
        synchronized (f584l) {
            try {
                k kVar = f582j;
                if (kVar != null) {
                    return kVar;
                }
                return f583k;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static k c(Context context) {
        k b4;
        synchronized (f584l) {
            try {
                b4 = b();
                if (b4 == null) {
                    a.b applicationContext = context.getApplicationContext();
                    if (applicationContext instanceof a.b) {
                        d(applicationContext, applicationContext.a());
                        b4 = c(applicationContext);
                    } else {
                        throw new IllegalStateException("WorkManager is not initialized properly.  You have explicitly disabled WorkManagerInitializer in your manifest, have not manually called WorkManager#initialize at this point, and your Application does not implement Configuration.Provider.");
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return b4;
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0018, code lost:
        r4 = r4.getApplicationContext();
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x001e, code lost:
        if (D0.k.f583k != null) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0020, code lost:
        D0.k.f583k = new D0.k(r4, r5, new O0.b(r5.b));
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x002e, code lost:
        D0.k.f582j = D0.k.f583k;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void d(android.content.Context r4, androidx.work.a r5) {
        /*
            java.lang.Object r0 = D0.k.f584l
            monitor-enter(r0)
            D0.k r1 = D0.k.f582j     // Catch: java.lang.Throwable -> L14
            if (r1 == 0) goto L16
            D0.k r2 = D0.k.f583k     // Catch: java.lang.Throwable -> L14
            if (r2 != 0) goto Lc
            goto L16
        Lc:
            java.lang.IllegalStateException r4 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L14
            java.lang.String r5 = "WorkManager is already initialized.  Did you try to initialize it manually without disabling WorkManagerInitializer? See WorkManager#initialize(Context, Configuration) or the class level Javadoc for more information."
            r4.<init>(r5)     // Catch: java.lang.Throwable -> L14
            throw r4     // Catch: java.lang.Throwable -> L14
        L14:
            r4 = move-exception
            goto L34
        L16:
            if (r1 != 0) goto L32
            android.content.Context r4 = r4.getApplicationContext()     // Catch: java.lang.Throwable -> L14
            D0.k r1 = D0.k.f583k     // Catch: java.lang.Throwable -> L14
            if (r1 != 0) goto L2e
            D0.k r1 = new D0.k     // Catch: java.lang.Throwable -> L14
            O0.b r2 = new O0.b     // Catch: java.lang.Throwable -> L14
            java.util.concurrent.ExecutorService r3 = r5.b     // Catch: java.lang.Throwable -> L14
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L14
            r1.<init>(r4, r5, r2)     // Catch: java.lang.Throwable -> L14
            D0.k.f583k = r1     // Catch: java.lang.Throwable -> L14
        L2e:
            D0.k r4 = D0.k.f583k     // Catch: java.lang.Throwable -> L14
            D0.k.f582j = r4     // Catch: java.lang.Throwable -> L14
        L32:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L14
            return
        L34:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L14
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: D0.k.d(android.content.Context, androidx.work.a):void");
    }

    public final void e() {
        synchronized (f584l) {
            try {
                this.f591h = true;
                BroadcastReceiver.PendingResult pendingResult = this.f592i;
                if (pendingResult != null) {
                    pendingResult.finish();
                    this.f592i = null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void f() {
        ArrayList d4;
        WorkDatabase workDatabase = this.f587c;
        if (Build.VERSION.SDK_INT >= 23) {
            Context context = this.f585a;
            String str = G0.g.f955n;
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
            if (jobScheduler != null && (d4 = G0.g.d(context, jobScheduler)) != null && !d4.isEmpty()) {
                Iterator it = d4.iterator();
                while (it.hasNext()) {
                    G0.g.a(jobScheduler, ((JobInfo) it.next()).getId());
                }
            }
        }
        r rVar = (r) workDatabase.n();
        AbstractC0731g abstractC0731g = rVar.f1469a;
        abstractC0731g.b();
        r.h hVar = rVar.f1476i;
        r0.e a4 = hVar.a();
        abstractC0731g.c();
        try {
            a4.f5706k.executeUpdateDelete();
            abstractC0731g.h();
            abstractC0731g.f();
            hVar.c(a4);
            e.a(this.f586b, workDatabase, this.f589e);
        } catch (Throwable th) {
            abstractC0731g.f();
            hVar.c(a4);
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0, types: [M0.k, java.lang.Object, java.lang.Runnable] */
    public final void g(String str, WorkerParameters.a aVar) {
        O0.a aVar2 = this.f588d;
        ?? obj = new Object();
        obj.f1680j = this;
        obj.f1681k = str;
        obj.f1682l = aVar;
        ((O0.b) aVar2).a(obj);
    }

    public final void h(String str) {
        ((O0.b) this.f588d).a(new M0.l(this, str, false));
    }
}
