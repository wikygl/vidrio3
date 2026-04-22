package M0;

import androidx.work.impl.WorkDatabase;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class f {

    /* renamed from: a  reason: collision with root package name */
    public final WorkDatabase f1670a;

    public f(WorkDatabase workDatabase) {
        this.f1670a = workDatabase;
    }

    public final int a(String str) {
        int i4;
        WorkDatabase workDatabase = this.f1670a;
        workDatabase.c();
        try {
            Long a4 = workDatabase.j().a(str);
            int i5 = 0;
            if (a4 != null) {
                i4 = a4.intValue();
            } else {
                i4 = 0;
            }
            if (i4 != Integer.MAX_VALUE) {
                i5 = i4 + 1;
            }
            workDatabase.j().b(new L0.d(str, i5));
            workDatabase.h();
            workDatabase.f();
            return i4;
        } catch (Throwable th) {
            workDatabase.f();
            throw th;
        }
    }

    public final int b(int i4) {
        int a4;
        synchronized (f.class) {
            a4 = a("next_job_scheduler_id");
            if (a4 < 0 || a4 > i4) {
                this.f1670a.j().b(new L0.d("next_job_scheduler_id", 1));
                a4 = 0;
            }
        }
        return a4;
    }
}
