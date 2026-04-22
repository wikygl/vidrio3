package j$.time.temporal;

import j$.time.A;
import j$.time.B;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class s implements t {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4027a;

    public /* synthetic */ s(int i4) {
        this.f4027a = i4;
    }

    @Override // j$.time.temporal.t
    public final Object a(o oVar) {
        switch (this.f4027a) {
            case 0:
                return (A) oVar.u(n.f4019a);
            case 1:
                return (j$.time.chrono.n) oVar.u(n.f4020b);
            case 2:
                return (u) oVar.u(n.f4021c);
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                a aVar = a.OFFSET_SECONDS;
                if (oVar.f(aVar)) {
                    return B.M(oVar.j(aVar));
                }
                return null;
            case 4:
                A a4 = (A) oVar.u(n.f4019a);
                return a4 != null ? a4 : (A) oVar.u(n.f4022d);
            case 5:
                a aVar2 = a.EPOCH_DAY;
                if (oVar.f(aVar2)) {
                    return j$.time.i.Q(oVar.r(aVar2));
                }
                return null;
            default:
                a aVar3 = a.NANO_OF_DAY;
                if (oVar.f(aVar3)) {
                    return j$.time.m.L(oVar.r(aVar3));
                }
                return null;
        }
    }

    public final String toString() {
        switch (this.f4027a) {
            case 0:
                return "ZoneId";
            case 1:
                return "Chronology";
            case 2:
                return "Precision";
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return "ZoneOffset";
            case 4:
                return "Zone";
            case 5:
                return "LocalDate";
            default:
                return "LocalTime";
        }
    }
}
