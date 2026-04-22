package G0;

import C0.i;
import C0.o;
import D0.k;
import L0.p;
import L0.r;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import androidx.work.impl.WorkDatabase;
import androidx.work.impl.background.systemjob.SystemJobService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class g implements D0.d {

    /* renamed from: n  reason: collision with root package name */
    public static final String f955n = i.e("SystemJobScheduler");

    /* renamed from: j  reason: collision with root package name */
    public final Context f956j;

    /* renamed from: k  reason: collision with root package name */
    public final JobScheduler f957k;

    /* renamed from: l  reason: collision with root package name */
    public final k f958l;

    /* renamed from: m  reason: collision with root package name */
    public final f f959m;

    public g(Context context, k kVar) {
        f fVar = new f(context);
        this.f956j = context;
        this.f958l = kVar;
        this.f957k = (JobScheduler) context.getSystemService("jobscheduler");
        this.f959m = fVar;
    }

    public static void a(JobScheduler jobScheduler, int i4) {
        try {
            jobScheduler.cancel(i4);
        } catch (Throwable th) {
            i.c().b(f955n, String.format(Locale.getDefault(), "Exception while trying to cancel job (%d)", Integer.valueOf(i4)), th);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0039 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0012 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.ArrayList c(android.content.Context r5, android.app.job.JobScheduler r6, java.lang.String r7) {
        /*
            java.util.ArrayList r5 = d(r5, r6)
            r6 = 0
            if (r5 != 0) goto L8
            return r6
        L8:
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = 2
            r0.<init>(r1)
            java.util.Iterator r5 = r5.iterator()
        L12:
            boolean r1 = r5.hasNext()
            if (r1 == 0) goto L45
            java.lang.Object r1 = r5.next()
            android.app.job.JobInfo r1 = (android.app.job.JobInfo) r1
            java.lang.String r2 = "EXTRA_WORK_SPEC_ID"
            android.os.PersistableBundle r3 = r1.getExtras()
            if (r3 == 0) goto L32
            boolean r4 = r3.containsKey(r2)     // Catch: java.lang.NullPointerException -> L31
            if (r4 == 0) goto L32
            java.lang.String r2 = r3.getString(r2)     // Catch: java.lang.NullPointerException -> L31
            goto L33
        L31:
        L32:
            r2 = r6
        L33:
            boolean r2 = r7.equals(r2)
            if (r2 == 0) goto L12
            int r1 = r1.getId()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r0.add(r1)
            goto L12
        L45:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: G0.g.c(android.content.Context, android.app.job.JobScheduler, java.lang.String):java.util.ArrayList");
    }

    public static ArrayList d(Context context, JobScheduler jobScheduler) {
        List<JobInfo> list;
        try {
            list = jobScheduler.getAllPendingJobs();
        } catch (Throwable th) {
            i.c().b(f955n, "getAllPendingJobs() is not reliable on this device.", th);
            list = null;
        }
        if (list == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(list.size());
        ComponentName componentName = new ComponentName(context, SystemJobService.class);
        for (JobInfo jobInfo : list) {
            if (componentName.equals(jobInfo.getService())) {
                arrayList.add(jobInfo);
            }
        }
        return arrayList;
    }

    @Override // D0.d
    public final void b(String str) {
        Context context = this.f956j;
        JobScheduler jobScheduler = this.f957k;
        ArrayList c4 = c(context, jobScheduler, str);
        if (c4 != null && !c4.isEmpty()) {
            Iterator it = c4.iterator();
            while (it.hasNext()) {
                a(jobScheduler, ((Integer) it.next()).intValue());
            }
            ((L0.i) this.f958l.f587c.k()).c(str);
        }
    }

    @Override // D0.d
    public final boolean e() {
        return true;
    }

    @Override // D0.d
    public final void f(p... pVarArr) {
        int b4;
        ArrayList c4;
        int b5;
        k kVar = this.f958l;
        WorkDatabase workDatabase = kVar.f587c;
        M0.f fVar = new M0.f(workDatabase);
        for (p pVar : pVarArr) {
            workDatabase.c();
            try {
                p i4 = ((r) workDatabase.n()).i(pVar.f1450a);
                String str = f955n;
                if (i4 == null) {
                    i.c().f(str, "Skipping scheduling " + pVar.f1450a + " because it's no longer in the DB", new Throwable[0]);
                    workDatabase.h();
                } else if (i4.f1451b != o.f337j) {
                    i.c().f(str, "Skipping scheduling " + pVar.f1450a + " because it is no longer enqueued", new Throwable[0]);
                    workDatabase.h();
                } else {
                    L0.g a4 = ((L0.i) workDatabase.k()).a(pVar.f1450a);
                    if (a4 != null) {
                        b4 = a4.f1436b;
                    } else {
                        kVar.f586b.getClass();
                        b4 = fVar.b(kVar.f586b.g);
                    }
                    if (a4 == null) {
                        ((L0.i) kVar.f587c.k()).b(new L0.g(pVar.f1450a, b4));
                    }
                    g(pVar, b4);
                    if (Build.VERSION.SDK_INT == 23 && (c4 = c(this.f956j, this.f957k, pVar.f1450a)) != null) {
                        int indexOf = c4.indexOf(Integer.valueOf(b4));
                        if (indexOf >= 0) {
                            c4.remove(indexOf);
                        }
                        if (!c4.isEmpty()) {
                            b5 = ((Integer) c4.get(0)).intValue();
                        } else {
                            kVar.f586b.getClass();
                            b5 = fVar.b(kVar.f586b.g);
                        }
                        g(pVar, b5);
                    }
                    workDatabase.h();
                }
            } finally {
                workDatabase.f();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0070, code lost:
        if (r11 < 26) goto L82;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void g(L0.p r19, int r20) {
        /*
            Method dump skipped, instructions count: 553
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: G0.g.g(L0.p, int):void");
    }
}
