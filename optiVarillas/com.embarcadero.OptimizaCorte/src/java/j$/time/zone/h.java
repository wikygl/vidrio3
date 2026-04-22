package j$.time.zone;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class h implements PrivilegedAction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f4073a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public h(ArrayList arrayList) {
        this.f4073a = arrayList;
    }

    @Override // java.security.PrivilegedAction
    public final Object run() {
        String property = System.getProperty("java.time.zone.DefaultZoneRulesProvider");
        if (property == null) {
            j.d(new i());
            return null;
        }
        try {
            j jVar = (j) j.class.cast(Class.forName(property, true, j.class.getClassLoader()).newInstance());
            j.d(jVar);
            this.f4073a.add(jVar);
            return null;
        } catch (Exception e4) {
            throw new Error(e4);
        }
    }
}
