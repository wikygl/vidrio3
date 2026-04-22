package L0;

import m0.AbstractC0726b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class e extends AbstractC0726b<d> {
    @Override // m0.AbstractC0735k
    public final String b() {
        return "INSERT OR REPLACE INTO `Preference` (`key`,`long_value`) VALUES (?,?)";
    }

    @Override // m0.AbstractC0726b
    public final void d(r0.e eVar, d dVar) {
        d dVar2 = dVar;
        String str = dVar2.f1431a;
        if (str == null) {
            eVar.f(1);
        } else {
            eVar.g(str, 1);
        }
        Long l2 = dVar2.f1432b;
        if (l2 == null) {
            eVar.f(2);
        } else {
            eVar.d(2, l2.longValue());
        }
    }
}
