package L0;

import android.database.Cursor;
import java.util.ArrayList;
import m0.AbstractC0726b;
import m0.AbstractC0731g;
import m0.AbstractC0735k;
import m0.C0733i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class u implements t {

    /* renamed from: a  reason: collision with root package name */
    public final AbstractC0731g f1479a;

    /* renamed from: b  reason: collision with root package name */
    public final a f1480b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends AbstractC0726b<s> {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "INSERT OR IGNORE INTO `WorkTag` (`tag`,`work_spec_id`) VALUES (?,?)";
        }

        @Override // m0.AbstractC0726b
        public final void d(r0.e eVar, s sVar) {
            s sVar2 = sVar;
            String str = sVar2.f1477a;
            if (str == null) {
                eVar.f(1);
            } else {
                eVar.g(str, 1);
            }
            String str2 = sVar2.f1478b;
            if (str2 == null) {
                eVar.f(2);
            } else {
                eVar.g(str2, 2);
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [m0.k, L0.u$a] */
    public u(AbstractC0731g abstractC0731g) {
        this.f1479a = abstractC0731g;
        this.f1480b = new AbstractC0735k(abstractC0731g);
    }

    public final ArrayList a(String str) {
        C0733i b4 = C0733i.b("SELECT DISTINCT tag FROM worktag WHERE work_spec_id=?", 1);
        if (str == null) {
            b4.g(1);
        } else {
            b4.i(str, 1);
        }
        AbstractC0731g abstractC0731g = this.f1479a;
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
}
