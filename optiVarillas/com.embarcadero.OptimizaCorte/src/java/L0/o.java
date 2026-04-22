package L0;

import m0.AbstractC0726b;
import m0.AbstractC0731g;
import m0.AbstractC0735k;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class o implements n {

    /* renamed from: a  reason: collision with root package name */
    public final AbstractC0731g f1446a;

    /* renamed from: b  reason: collision with root package name */
    public final a f1447b;

    /* renamed from: c  reason: collision with root package name */
    public final b f1448c;

    /* renamed from: d  reason: collision with root package name */
    public final c f1449d;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends AbstractC0726b<m> {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "INSERT OR REPLACE INTO `WorkProgress` (`work_spec_id`,`progress`) VALUES (?,?)";
        }

        @Override // m0.AbstractC0726b
        public final void d(r0.e eVar, m mVar) {
            m mVar2 = mVar;
            String str = mVar2.f1444a;
            if (str == null) {
                eVar.f(1);
            } else {
                eVar.g(str, 1);
            }
            byte[] c4 = androidx.work.b.c(mVar2.f1445b);
            if (c4 == null) {
                eVar.f(2);
            } else {
                eVar.a(2, c4);
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class b extends AbstractC0735k {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "DELETE from WorkProgress where work_spec_id=?";
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class c extends AbstractC0735k {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "DELETE FROM WorkProgress";
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [m0.k, L0.o$a] */
    /* JADX WARN: Type inference failed for: r0v1, types: [m0.k, L0.o$b] */
    /* JADX WARN: Type inference failed for: r0v2, types: [m0.k, L0.o$c] */
    public o(AbstractC0731g abstractC0731g) {
        this.f1446a = abstractC0731g;
        this.f1447b = new AbstractC0735k(abstractC0731g);
        this.f1448c = new AbstractC0735k(abstractC0731g);
        this.f1449d = new AbstractC0735k(abstractC0731g);
    }
}
