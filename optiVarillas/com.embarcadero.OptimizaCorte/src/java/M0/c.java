package M0;

import androidx.work.impl.WorkDatabase;
import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class c extends d {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ D0.k f1663k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ String f1664l = null;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ boolean f1665m = false;

    public c(D0.k kVar) {
        this.f1663k = kVar;
    }

    @Override // M0.d
    public final void b() {
        D0.k kVar = this.f1663k;
        WorkDatabase workDatabase = kVar.f587c;
        workDatabase.c();
        try {
            Iterator it = ((L0.r) workDatabase.n()).g(this.f1664l).iterator();
            while (it.hasNext()) {
                d.a(kVar, (String) it.next());
            }
            workDatabase.h();
            workDatabase.f();
            if (this.f1665m) {
                D0.e.a(kVar.f586b, kVar.f587c, kVar.f589e);
            }
        } catch (Throwable th) {
            workDatabase.f();
            throw th;
        }
    }
}
