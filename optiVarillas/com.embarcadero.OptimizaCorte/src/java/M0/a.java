package M0;

import androidx.work.impl.WorkDatabase;
import java.util.UUID;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class a extends d {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ D0.k f1659k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ UUID f1660l;

    public a(D0.k kVar, UUID uuid) {
        this.f1659k = kVar;
        this.f1660l = uuid;
    }

    @Override // M0.d
    public final void b() {
        D0.k kVar = this.f1659k;
        WorkDatabase workDatabase = kVar.f587c;
        workDatabase.c();
        try {
            d.a(kVar, this.f1660l.toString());
            workDatabase.h();
            workDatabase.f();
            D0.e.a(kVar.f586b, kVar.f587c, kVar.f589e);
        } catch (Throwable th) {
            workDatabase.f();
            throw th;
        }
    }
}
