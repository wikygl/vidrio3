package j$.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class I extends AbstractC0483a implements Serializable {

    /* renamed from: d  reason: collision with root package name */
    public static final I f3867d = new I();
    private static final long serialVersionUID = 2775954514031616474L;

    static {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        HashMap hashMap3 = new HashMap();
        hashMap.put("en", new String[]{"BB", "BE"});
        hashMap.put("th", new String[]{"BB", "BE"});
        hashMap2.put("en", new String[]{"B.B.", "B.E."});
        hashMap2.put("th", new String[]{"พ.ศ.", "ปีก่อนคริสต์กาลที่"});
        hashMap3.put("en", new String[]{"Before Buddhist", "Budhhist Era"});
        hashMap3.put("th", new String[]{"พุทธศักราช", "ปีก่อนคริสต์กาลที่"});
    }

    private I() {
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    @Override // j$.time.chrono.n
    public final o B(int i4) {
        if (i4 != 0) {
            if (i4 == 1) {
                return L.BE;
            }
            throw new RuntimeException("Invalid era: " + i4);
        }
        return L.BEFORE_BE;
    }

    @Override // j$.time.chrono.n
    public final String i() {
        return "ThaiBuddhist";
    }

    @Override // j$.time.chrono.n
    public final InterfaceC0484b k(j$.time.temporal.o oVar) {
        return oVar instanceof K ? (K) oVar : new K(j$.time.i.F(oVar));
    }

    public final j$.time.temporal.w m(j$.time.temporal.a aVar) {
        int i4 = H.f3866a[aVar.ordinal()];
        if (i4 == 1) {
            j$.time.temporal.w j4 = j$.time.temporal.a.PROLEPTIC_MONTH.j();
            return j$.time.temporal.w.j(j4.e() + 6516, j4.d() + 6516);
        } else if (i4 == 2) {
            j$.time.temporal.w j5 = j$.time.temporal.a.YEAR.j();
            return j$.time.temporal.w.k((-(j5.e() + 543)) + 1, j5.d() + 543);
        } else if (i4 != 3) {
            return aVar.j();
        } else {
            j$.time.temporal.w j6 = j$.time.temporal.a.YEAR.j();
            return j$.time.temporal.w.j(j6.e() + 543, j6.d() + 543);
        }
    }

    @Override // j$.time.chrono.n
    public final String q() {
        return "buddhist";
    }

    Object writeReplace() {
        return new G((byte) 1, this);
    }

    @Override // j$.time.chrono.n
    public final InterfaceC0493k y(j$.time.g gVar, j$.time.A a4) {
        return m.G(this, gVar, a4);
    }
}
