package j$.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class x extends AbstractC0483a implements Serializable {

    /* renamed from: d  reason: collision with root package name */
    public static final x f3909d = new x();
    private static final long serialVersionUID = 459996390165777884L;

    private x() {
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    @Override // j$.time.chrono.n
    public final o B(int i4) {
        return A.y(i4);
    }

    @Override // j$.time.chrono.n
    public final String i() {
        return "Japanese";
    }

    @Override // j$.time.chrono.n
    public final InterfaceC0484b k(j$.time.temporal.o oVar) {
        return oVar instanceof z ? (z) oVar : new z(j$.time.i.F(oVar));
    }

    public final j$.time.temporal.w m(j$.time.temporal.a aVar) {
        long K3;
        long j4;
        switch (w.f3908a[aVar.ordinal()]) {
            case 1:
            case 2:
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
            case 4:
                throw new RuntimeException("Unsupported field: " + aVar);
            case 5:
                return j$.time.temporal.w.k(A.D(), 999999999 - A.k().n().K());
            case 6:
                return j$.time.temporal.w.k(A.B(), j$.time.temporal.a.DAY_OF_YEAR.j().d());
            case 7:
                K3 = z.f3911d.K();
                j4 = 999999999;
                break;
            case 8:
                K3 = A.f3854d.getValue();
                j4 = A.k().getValue();
                break;
            default:
                return aVar.j();
        }
        return j$.time.temporal.w.j(K3, j4);
    }

    @Override // j$.time.chrono.n
    public final String q() {
        return "japanese";
    }

    Object writeReplace() {
        return new G((byte) 1, this);
    }

    @Override // j$.time.chrono.n
    public final InterfaceC0493k y(j$.time.g gVar, j$.time.A a4) {
        return m.G(this, gVar, a4);
    }
}
