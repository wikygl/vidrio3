package L0;

import android.database.Cursor;
import m0.AbstractC0726b;
import m0.AbstractC0731g;
import m0.AbstractC0735k;
import m0.C0733i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class i implements h {

    /* renamed from: a  reason: collision with root package name */
    public final AbstractC0731g f1437a;

    /* renamed from: b  reason: collision with root package name */
    public final a f1438b;

    /* renamed from: c  reason: collision with root package name */
    public final b f1439c;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends AbstractC0726b<g> {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "INSERT OR REPLACE INTO `SystemIdInfo` (`work_spec_id`,`system_id`) VALUES (?,?)";
        }

        @Override // m0.AbstractC0726b
        public final void d(r0.e eVar, g gVar) {
            g gVar2 = gVar;
            String str = gVar2.f1435a;
            if (str == null) {
                eVar.f(1);
            } else {
                eVar.g(str, 1);
            }
            eVar.d(2, gVar2.f1436b);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class b extends AbstractC0735k {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "DELETE FROM SystemIdInfo where work_spec_id=?";
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [m0.k, L0.i$a] */
    /* JADX WARN: Type inference failed for: r0v1, types: [m0.k, L0.i$b] */
    public i(AbstractC0731g abstractC0731g) {
        this.f1437a = abstractC0731g;
        this.f1438b = new AbstractC0735k(abstractC0731g);
        this.f1439c = new AbstractC0735k(abstractC0731g);
    }

    public final g a(String str) {
        g gVar;
        C0733i b4 = C0733i.b("SELECT `SystemIdInfo`.`work_spec_id` AS `work_spec_id`, `SystemIdInfo`.`system_id` AS `system_id` FROM SystemIdInfo WHERE work_spec_id=?", 1);
        if (str == null) {
            b4.g(1);
        } else {
            b4.i(str, 1);
        }
        AbstractC0731g abstractC0731g = this.f1437a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            int e4 = B2.a.e(g4, "work_spec_id");
            int e5 = B2.a.e(g4, "system_id");
            if (g4.moveToFirst()) {
                gVar = new g(g4.getString(e4), g4.getInt(e5));
            } else {
                gVar = null;
            }
            return gVar;
        } finally {
            g4.close();
            b4.k();
        }
    }

    public final void b(g gVar) {
        AbstractC0731g abstractC0731g = this.f1437a;
        abstractC0731g.b();
        abstractC0731g.c();
        try {
            this.f1438b.e(gVar);
            abstractC0731g.h();
        } finally {
            abstractC0731g.f();
        }
    }

    public final void c(String str) {
        AbstractC0731g abstractC0731g = this.f1437a;
        abstractC0731g.b();
        b bVar = this.f1439c;
        r0.e a4 = bVar.a();
        if (str == null) {
            a4.f(1);
        } else {
            a4.g(str, 1);
        }
        abstractC0731g.c();
        try {
            a4.i();
            abstractC0731g.h();
        } finally {
            abstractC0731g.f();
            bVar.c(a4);
        }
    }
}
