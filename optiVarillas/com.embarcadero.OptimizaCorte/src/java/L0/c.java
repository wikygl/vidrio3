package L0;

import android.database.Cursor;
import java.util.ArrayList;
import m0.AbstractC0726b;
import m0.AbstractC0731g;
import m0.AbstractC0735k;
import m0.C0733i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class c implements b {

    /* renamed from: a  reason: collision with root package name */
    public final AbstractC0731g f1429a;

    /* renamed from: b  reason: collision with root package name */
    public final a f1430b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends AbstractC0726b<L0.a> {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "INSERT OR IGNORE INTO `Dependency` (`work_spec_id`,`prerequisite_id`) VALUES (?,?)";
        }

        @Override // m0.AbstractC0726b
        public final void d(r0.e eVar, L0.a aVar) {
            L0.a aVar2 = aVar;
            String str = aVar2.f1427a;
            if (str == null) {
                eVar.f(1);
            } else {
                eVar.g(str, 1);
            }
            String str2 = aVar2.f1428b;
            if (str2 == null) {
                eVar.f(2);
            } else {
                eVar.g(str2, 2);
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [m0.k, L0.c$a] */
    public c(AbstractC0731g abstractC0731g) {
        this.f1429a = abstractC0731g;
        this.f1430b = new AbstractC0735k(abstractC0731g);
    }

    public final ArrayList a(String str) {
        C0733i b4 = C0733i.b("SELECT work_spec_id FROM dependency WHERE prerequisite_id=?", 1);
        if (str == null) {
            b4.g(1);
        } else {
            b4.i(str, 1);
        }
        AbstractC0731g abstractC0731g = this.f1429a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            ArrayList arrayList = new ArrayList(g4.getCount());
            while (g4.moveToNext()) {
                arrayList.add(g4.getString(0));
            }
            return arrayList;
        } finally {
            g4.close();
            b4.k();
        }
    }

    public final boolean b(String str) {
        boolean z4 = true;
        C0733i b4 = C0733i.b("SELECT COUNT(*)=0 FROM dependency WHERE work_spec_id=? AND prerequisite_id IN (SELECT id FROM workspec WHERE state!=2)", 1);
        if (str == null) {
            b4.g(1);
        } else {
            b4.i(str, 1);
        }
        AbstractC0731g abstractC0731g = this.f1429a;
        abstractC0731g.b();
        Cursor g4 = abstractC0731g.g(b4);
        try {
            boolean z5 = false;
            if (g4.moveToFirst()) {
                if (g4.getInt(0) == 0) {
                    z4 = false;
                }
                z5 = z4;
            }
            return z5;
        } finally {
            g4.close();
            b4.k();
        }
    }
}
