package j$.time.format;

import j$.time.A;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class m implements g {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f3943a;

    /* renamed from: b  reason: collision with root package name */
    private final Object f3944b;

    public /* synthetic */ m(Object obj, int i4) {
        this.f3943a = i4;
        this.f3944b = obj;
    }

    @Override // j$.time.format.g
    public final boolean j(q qVar, StringBuilder sb) {
        switch (this.f3943a) {
            case 0:
                sb.append((String) this.f3944b);
                return true;
            default:
                A a4 = (A) qVar.f((b) ((j$.time.temporal.t) this.f3944b));
                if (a4 == null) {
                    return false;
                }
                sb.append(a4.i());
                return true;
        }
    }

    public final String toString() {
        switch (this.f3943a) {
            case 0:
                String replace = ((String) this.f3944b).replace("'", "''");
                return "'" + replace + "'";
            default:
                return "ZoneRegionId()";
        }
    }
}
