package j$.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C extends AbstractC0483a implements Serializable {

    /* renamed from: d  reason: collision with root package name */
    public static final C f3860d = new C();
    private static final long serialVersionUID = 1039765215346859963L;

    private C() {
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    @Override // j$.time.chrono.n
    public final o B(int i4) {
        if (i4 != 0) {
            if (i4 == 1) {
                return F.ROC;
            }
            throw new RuntimeException("Invalid era: " + i4);
        }
        return F.BEFORE_ROC;
    }

    @Override // j$.time.chrono.n
    public final String i() {
        return "Minguo";
    }

    @Override // j$.time.chrono.n
    public final InterfaceC0484b k(j$.time.temporal.o oVar) {
        return oVar instanceof E ? (E) oVar : new E(j$.time.i.F(oVar));
    }

    public final j$.time.temporal.w m(j$.time.temporal.a aVar) {
        int i4 = B.f3859a[aVar.ordinal()];
        if (i4 == 1) {
            j$.time.temporal.w j4 = j$.time.temporal.a.PROLEPTIC_MONTH.j();
            return j$.time.temporal.w.j(j4.e() - 22932, j4.d() - 22932);
        } else if (i4 == 2) {
            j$.time.temporal.w j5 = j$.time.temporal.a.YEAR.j();
            return j$.time.temporal.w.k(j5.d() - 1911, (-j5.e()) + 1912);
        } else if (i4 != 3) {
            return aVar.j();
        } else {
            j$.time.temporal.w j6 = j$.time.temporal.a.YEAR.j();
            return j$.time.temporal.w.j(j6.e() - 1911, j6.d() - 1911);
        }
    }

    @Override // j$.time.chrono.n
    public final String q() {
        return "roc";
    }

    Object writeReplace() {
        return new G((byte) 1, this);
    }

    @Override // j$.time.chrono.n
    public final InterfaceC0493k y(j$.time.g gVar, j$.time.A a4) {
        return m.G(this, gVar, a4);
    }
}
