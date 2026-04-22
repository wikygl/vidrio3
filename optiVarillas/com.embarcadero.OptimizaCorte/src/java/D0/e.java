package D0;

import L0.p;
import L0.q;
import L0.r;
import android.os.Build;
import androidx.work.impl.WorkDatabase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class e {

    /* renamed from: a  reason: collision with root package name */
    public static final String f570a = C0.i.e("Schedulers");

    public static void a(androidx.work.a aVar, WorkDatabase workDatabase, List<d> list) {
        if (list != null && list.size() != 0) {
            q n4 = workDatabase.n();
            workDatabase.c();
            try {
                int i4 = Build.VERSION.SDK_INT;
                int i5 = aVar.h;
                if (i4 == 23) {
                    i5 /= 2;
                }
                r rVar = (r) n4;
                ArrayList c4 = rVar.c(i5);
                ArrayList b4 = rVar.b();
                if (c4.size() > 0) {
                    long currentTimeMillis = System.currentTimeMillis();
                    Iterator it = c4.iterator();
                    while (it.hasNext()) {
                        rVar.l(((p) it.next()).f1450a, currentTimeMillis);
                    }
                }
                workDatabase.h();
                workDatabase.f();
                if (c4.size() > 0) {
                    p[] pVarArr = (p[]) c4.toArray(new p[c4.size()]);
                    for (d dVar : list) {
                        if (dVar.e()) {
                            dVar.f(pVarArr);
                        }
                    }
                }
                if (b4.size() > 0) {
                    p[] pVarArr2 = (p[]) b4.toArray(new p[b4.size()]);
                    for (d dVar2 : list) {
                        if (!dVar2.e()) {
                            dVar2.f(pVarArr2);
                        }
                    }
                }
            } catch (Throwable th) {
                workDatabase.f();
                throw th;
            }
        }
    }
}
