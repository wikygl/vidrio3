package j$.time;

import j$.time.chrono.AbstractC0483a;
import j$.time.chrono.AbstractC0491i;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class q implements j$.time.temporal.o, j$.time.temporal.p, Comparable, Serializable {
    private static final long serialVersionUID = -939150713474957432L;

    /* renamed from: a  reason: collision with root package name */
    private final int f3994a;

    /* renamed from: b  reason: collision with root package name */
    private final int f3995b;

    static {
        j$.time.format.o oVar = new j$.time.format.o();
        oVar.f("--");
        oVar.k(j$.time.temporal.a.MONTH_OF_YEAR, 2);
        oVar.e('-');
        oVar.k(j$.time.temporal.a.DAY_OF_MONTH, 2);
        oVar.v();
    }

    private q(int i4, int i5) {
        this.f3994a = i4;
        this.f3995b = i5;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static q D(DataInput dataInput) {
        byte readByte = dataInput.readByte();
        byte readByte2 = dataInput.readByte();
        o G4 = o.G(readByte);
        Objects.requireNonNull(G4, "month");
        j$.time.temporal.a.DAY_OF_MONTH.D(readByte2);
        if (readByte2 <= G4.F()) {
            return new q(G4.getValue(), readByte2);
        }
        String name = G4.name();
        throw new RuntimeException("Illegal value for DayOfMonth field, value " + ((int) readByte2) + " is not valid for month " + name);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 13, this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void E(DataOutput dataOutput) {
        dataOutput.writeByte(this.f3994a);
        dataOutput.writeByte(this.f3995b);
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        q qVar = (q) obj;
        int i4 = this.f3994a - qVar.f3994a;
        return i4 == 0 ? this.f3995b - qVar.f3995b : i4;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof q) {
            q qVar = (q) obj;
            return this.f3994a == qVar.f3994a && this.f3995b == qVar.f3995b;
        }
        return false;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? rVar == j$.time.temporal.a.MONTH_OF_YEAR || rVar == j$.time.temporal.a.DAY_OF_MONTH : rVar != null && rVar.m(this);
    }

    public final int hashCode() {
        return (this.f3994a << 6) + this.f3995b;
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        return m(rVar).a(r(rVar), rVar);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        if (rVar == j$.time.temporal.a.MONTH_OF_YEAR) {
            return rVar.j();
        }
        if (rVar == j$.time.temporal.a.DAY_OF_MONTH) {
            int i4 = this.f3994a;
            o G4 = o.G(i4);
            G4.getClass();
            int i5 = n.f3990a[G4.ordinal()];
            return j$.time.temporal.w.k(i5 != 1 ? (i5 == 2 || i5 == 3 || i5 == 4 || i5 == 5) ? 30 : 31 : 28, o.G(i4).F());
        }
        return j$.time.temporal.n.d(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        int i4;
        if (rVar instanceof j$.time.temporal.a) {
            int i5 = p.f3993a[((j$.time.temporal.a) rVar).ordinal()];
            if (i5 == 1) {
                i4 = this.f3995b;
            } else if (i5 != 2) {
                throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
            } else {
                i4 = this.f3994a;
            }
            return i4;
        }
        return rVar.l(this);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(10);
        sb.append("--");
        int i4 = this.f3994a;
        sb.append(i4 < 10 ? "0" : "");
        sb.append(i4);
        int i5 = this.f3995b;
        sb.append(i5 < 10 ? "-0" : "-");
        sb.append(i5);
        return sb.toString();
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        return tVar == j$.time.temporal.n.e() ? j$.time.chrono.u.f3906d : j$.time.temporal.n.c(this, tVar);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        if (((AbstractC0483a) AbstractC0491i.p(mVar)).equals(j$.time.chrono.u.f3906d)) {
            j$.time.temporal.m d4 = mVar.d(this.f3994a, j$.time.temporal.a.MONTH_OF_YEAR);
            j$.time.temporal.a aVar = j$.time.temporal.a.DAY_OF_MONTH;
            return d4.d(Math.min(d4.m(aVar).d(), this.f3995b), aVar);
        }
        throw new RuntimeException("Adjustment only supported on ISO date-time");
    }
}
