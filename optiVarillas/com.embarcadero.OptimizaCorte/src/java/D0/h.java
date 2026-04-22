package D0;

import androidx.work.impl.WorkDatabase;
import m0.AbstractC0731g;
import r0.C0779a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class h extends AbstractC0731g.b {
    @Override // m0.AbstractC0731g.b
    public final void a(C0779a c0779a) {
        c0779a.a();
        try {
            int i4 = WorkDatabase.k;
            c0779a.d("DELETE FROM workspec WHERE state IN (2, 3, 5) AND (period_start_time + minimum_retention_duration) < " + (System.currentTimeMillis() - WorkDatabase.j) + " AND (SELECT COUNT(*)=0 FROM dependency WHERE     prerequisite_id=id AND     work_spec_id NOT IN         (SELECT id FROM workspec WHERE state IN (2, 3, 5)))");
            c0779a.i();
        } finally {
            c0779a.b();
        }
    }
}
