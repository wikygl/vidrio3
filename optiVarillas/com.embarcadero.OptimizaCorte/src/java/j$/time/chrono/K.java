package j$.time.chrono;

import j$.time.AbstractC0495d;
import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class K extends AbstractC0486d {
    private static final long serialVersionUID = -8722293800195731463L;

    /* renamed from: a  reason: collision with root package name */
    private final transient j$.time.i f3869a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public K(j$.time.i iVar) {
        Objects.requireNonNull(iVar, "isoDate");
        this.f3869a = iVar;
    }

    private int K() {
        return this.f3869a.K() + 543;
    }

    private K M(j$.time.i iVar) {
        return iVar.equals(this.f3869a) ? this : new K(iVar);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new G((byte) 8, this);
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final o E() {
        return K() >= 1 ? L.BE : L.BEFORE_BE;
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final InterfaceC0484b F(long j4, j$.time.temporal.u uVar) {
        return (K) super.z(j4, uVar);
    }

    @Override // j$.time.chrono.AbstractC0486d
    final InterfaceC0484b G(long j4) {
        return M(this.f3869a.S(j4));
    }

    @Override // j$.time.chrono.AbstractC0486d
    final InterfaceC0484b H(long j4) {
        return M(this.f3869a.T(j4));
    }

    @Override // j$.time.chrono.AbstractC0486d
    final InterfaceC0484b I(long j4) {
        return M(this.f3869a.U(j4));
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final InterfaceC0484b J(j$.time.temporal.p pVar) {
        return (K) super.l(pVar);
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0024, code lost:
        if (r2 != 7) goto L13;
     */
    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    /* renamed from: L */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final j$.time.chrono.K d(long r9, j$.time.temporal.r r11) {
        /*
            r8 = this;
            boolean r0 = r11 instanceof j$.time.temporal.a
            if (r0 == 0) goto L9a
            r0 = r11
            j$.time.temporal.a r0 = (j$.time.temporal.a) r0
            long r1 = r8.r(r0)
            int r3 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r3 != 0) goto L10
            return r8
        L10:
            int[] r1 = j$.time.chrono.J.f3868a
            int r2 = r0.ordinal()
            r2 = r1[r2]
            j$.time.i r3 = r8.f3869a
            r4 = 7
            r5 = 6
            r6 = 4
            if (r2 == r6) goto L4c
            r7 = 5
            if (r2 == r7) goto L27
            if (r2 == r5) goto L4c
            if (r2 == r4) goto L4c
            goto L62
        L27:
            j$.time.chrono.I r11 = j$.time.chrono.I.f3867d
            j$.time.temporal.w r11 = r11.m(r0)
            r11.b(r9, r0)
            int r11 = r8.K()
            long r0 = (long) r11
            r4 = 12
            long r0 = r0 * r4
            int r11 = r3.J()
            long r4 = (long) r11
            long r0 = r0 + r4
            r4 = 1
            long r0 = r0 - r4
            long r9 = r9 - r0
            j$.time.i r9 = r3.T(r9)
            j$.time.chrono.K r9 = r8.M(r9)
            return r9
        L4c:
            j$.time.chrono.I r2 = j$.time.chrono.I.f3867d
            j$.time.temporal.w r2 = r2.m(r0)
            int r2 = r2.a(r9, r0)
            int r0 = r0.ordinal()
            r0 = r1[r0]
            if (r0 == r6) goto L85
            if (r0 == r5) goto L7a
            if (r0 == r4) goto L6b
        L62:
            j$.time.i r9 = r3.d(r9, r11)
            j$.time.chrono.K r9 = r8.M(r9)
            return r9
        L6b:
            int r9 = r8.K()
            int r9 = (-542) - r9
            j$.time.i r9 = r3.Z(r9)
            j$.time.chrono.K r9 = r8.M(r9)
            return r9
        L7a:
            int r2 = r2 + (-543)
            j$.time.i r9 = r3.Z(r2)
            j$.time.chrono.K r9 = r8.M(r9)
            return r9
        L85:
            int r9 = r8.K()
            r10 = 1
            if (r9 < r10) goto L8d
            goto L8f
        L8d:
            int r2 = 1 - r2
        L8f:
            int r2 = r2 + (-543)
            j$.time.i r9 = r3.Z(r2)
            j$.time.chrono.K r9 = r8.M(r9)
            return r9
        L9a:
            j$.time.chrono.b r9 = super.d(r9, r11)
            j$.time.chrono.K r9 = (j$.time.chrono.K) r9
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.chrono.K.d(long, j$.time.temporal.r):j$.time.chrono.K");
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public final n a() {
        return I.f3867d;
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b, j$.time.temporal.m
    public final InterfaceC0484b e(long j4, j$.time.temporal.u uVar) {
        return (K) super.e(j4, uVar);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    public final j$.time.temporal.m e(long j4, j$.time.temporal.u uVar) {
        return (K) super.e(j4, uVar);
    }

    @Override // j$.time.chrono.AbstractC0486d
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof K) {
            return this.f3869a.equals(((K) obj).f3869a);
        }
        return false;
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b
    public final int hashCode() {
        I.f3867d.getClass();
        return this.f3869a.hashCode() ^ 146118545;
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    public final j$.time.temporal.m l(j$.time.i iVar) {
        return (K) super.l(iVar);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            if (AbstractC0491i.h(this, rVar)) {
                j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
                int i4 = J.f3868a[aVar.ordinal()];
                if (i4 == 1 || i4 == 2 || i4 == 3) {
                    return this.f3869a.m(rVar);
                }
                if (i4 != 4) {
                    return I.f3867d.m(aVar);
                }
                j$.time.temporal.w j4 = j$.time.temporal.a.YEAR.j();
                return j$.time.temporal.w.j(1L, K() <= 0 ? (-(j4.e() + 543)) + 1 : 543 + j4.d());
            }
            throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
        return rVar.u(this);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = J.f3868a[((j$.time.temporal.a) rVar).ordinal()];
            if (i4 == 4) {
                int K3 = K();
                if (K3 < 1) {
                    K3 = 1 - K3;
                }
                return K3;
            }
            j$.time.i iVar = this.f3869a;
            if (i4 != 5) {
                if (i4 != 6) {
                    if (i4 != 7) {
                        return iVar.r(rVar);
                    }
                    return K() < 1 ? 0 : 1;
                }
                return K();
            }
            return ((K() * 12) + iVar.J()) - 1;
        }
        return rVar.l(this);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b
    public final long s() {
        return this.f3869a.s();
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.chrono.InterfaceC0484b
    public final InterfaceC0487e t(j$.time.m mVar) {
        return C0489g.F(this, mVar);
    }

    @Override // j$.time.chrono.AbstractC0486d, j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return (K) super.z(j4, uVar);
    }
}
