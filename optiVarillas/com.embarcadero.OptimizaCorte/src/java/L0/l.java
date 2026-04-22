package L0;

import m0.AbstractC0726b;
import m0.AbstractC0731g;
import m0.AbstractC0735k;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class l implements k {

    /* renamed from: a  reason: collision with root package name */
    public final AbstractC0731g f1442a;

    /* renamed from: b  reason: collision with root package name */
    public final a f1443b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends AbstractC0726b<j> {
        @Override // m0.AbstractC0735k
        public final String b() {
            return "INSERT OR IGNORE INTO `WorkName` (`name`,`work_spec_id`) VALUES (?,?)";
        }

        @Override // m0.AbstractC0726b
        public final void d(r0.e eVar, j jVar) {
            j jVar2 = jVar;
            String str = jVar2.f1440a;
            if (str == null) {
                eVar.f(1);
            } else {
                eVar.g(str, 1);
            }
            String str2 = jVar2.f1441b;
            if (str2 == null) {
                eVar.f(2);
            } else {
                eVar.g(str2, 2);
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [m0.k, L0.l$a] */
    public l(AbstractC0731g abstractC0731g) {
        this.f1442a = abstractC0731g;
        this.f1443b = new AbstractC0735k(abstractC0731g);
    }
}
