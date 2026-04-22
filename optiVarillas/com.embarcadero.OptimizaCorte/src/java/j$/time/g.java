package j$.time;

import j$.time.chrono.AbstractC0491i;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class g implements j$.time.temporal.m, j$.time.temporal.p, Comparable, Serializable {

    /* renamed from: c  reason: collision with root package name */
    public static final g f3966c = new g(0, 0);
    private static final long serialVersionUID = -665713676816604388L;

    /* renamed from: a  reason: collision with root package name */
    private final long f3967a;

    /* renamed from: b  reason: collision with root package name */
    private final int f3968b;

    static {
        H(-31557014167219200L, 0L);
        H(31556889864403199L, 999999999L);
    }

    private g(long j4, int i4) {
        this.f3967a = j4;
        this.f3968b = i4;
    }

    private static g D(long j4, int i4) {
        if ((i4 | j4) == 0) {
            return f3966c;
        }
        if (j4 < -31557014167219200L || j4 > 31556889864403199L) {
            throw new RuntimeException("Instant exceeds minimum or maximum instant");
        }
        return new g(j4, i4);
    }

    public static g G() {
        C0480a.f3852b.getClass();
        long currentTimeMillis = System.currentTimeMillis();
        long j4 = 1000;
        return D(j$.com.android.tools.r8.a.n(currentTimeMillis, j4), ((int) j$.com.android.tools.r8.a.m(currentTimeMillis, j4)) * 1000000);
    }

    public static g H(long j4, long j5) {
        return D(j$.com.android.tools.r8.a.i(j4, j$.com.android.tools.r8.a.n(j5, 1000000000L)), (int) j$.com.android.tools.r8.a.m(j5, 1000000000L));
    }

    private g I(long j4, long j5) {
        if ((j4 | j5) == 0) {
            return this;
        }
        return H(j$.com.android.tools.r8.a.i(j$.com.android.tools.r8.a.i(this.f3967a, j4), j5 / 1000000000), this.f3968b + (j5 % 1000000000));
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 2, this);
    }

    public final long E() {
        return this.f3967a;
    }

    public final int F() {
        return this.f3968b;
    }

    @Override // j$.time.temporal.m
    /* renamed from: J */
    public final g e(long j4, j$.time.temporal.u uVar) {
        if (uVar instanceof j$.time.temporal.b) {
            switch (f.f3918b[((j$.time.temporal.b) uVar).ordinal()]) {
                case 1:
                    return I(0L, j4);
                case 2:
                    return I(j4 / 1000000, (j4 % 1000000) * 1000);
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    return I(j4 / 1000, (j4 % 1000) * 1000000);
                case 4:
                    return I(j4, 0L);
                case 5:
                    return I(j$.com.android.tools.r8.a.o(j4, 60), 0L);
                case 6:
                    return I(j$.com.android.tools.r8.a.o(j4, 3600), 0L);
                case 7:
                    return I(j$.com.android.tools.r8.a.o(j4, 43200), 0L);
                case 8:
                    return I(j$.com.android.tools.r8.a.o(j4, 86400), 0L);
                default:
                    throw new RuntimeException("Unsupported unit: " + uVar);
            }
        }
        return (g) uVar.j(this, j4);
    }

    public final long K() {
        long o4;
        int i4;
        int i5 = this.f3968b;
        long j4 = this.f3967a;
        if (j4 >= 0 || i5 <= 0) {
            o4 = j$.com.android.tools.r8.a.o(j4, 1000);
            i4 = i5 / 1000000;
        } else {
            o4 = j$.com.android.tools.r8.a.o(j4 + 1, 1000);
            i4 = (i5 / 1000000) - 1000;
        }
        return j$.com.android.tools.r8.a.i(o4, i4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void L(DataOutput dataOutput) {
        dataOutput.writeLong(this.f3967a);
        dataOutput.writeInt(this.f3968b);
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        g gVar = (g) obj;
        int compare = Long.compare(this.f3967a, gVar.f3967a);
        return compare != 0 ? compare : this.f3968b - gVar.f3968b;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x003f, code lost:
        if (r7 != r2) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0049, code lost:
        if (r7 != r2) goto L22;
     */
    @Override // j$.time.temporal.m
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final j$.time.temporal.m d(long r6, j$.time.temporal.r r8) {
        /*
            r5 = this;
            boolean r0 = r8 instanceof j$.time.temporal.a
            if (r0 == 0) goto L53
            r0 = r8
            j$.time.temporal.a r0 = (j$.time.temporal.a) r0
            r0.D(r6)
            int[] r1 = j$.time.f.f3917a
            int r0 = r0.ordinal()
            r0 = r1[r0]
            r1 = 1
            int r2 = r5.f3968b
            long r3 = r5.f3967a
            if (r0 == r1) goto L4c
            r1 = 2
            if (r0 == r1) goto L46
            r1 = 3
            if (r0 == r1) goto L39
            r1 = 4
            if (r0 != r1) goto L2d
            int r8 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r8 == 0) goto L2b
            j$.time.g r6 = D(r6, r2)
            goto L59
        L2b:
            r6 = r5
            goto L59
        L2d:
            j$.time.temporal.v r6 = new j$.time.temporal.v
            java.lang.String r7 = "Unsupported field: "
            java.lang.String r7 = j$.time.AbstractC0495d.a(r7, r8)
            r6.<init>(r7)
            throw r6
        L39:
            int r7 = (int) r6
            r6 = 1000000(0xf4240, float:1.401298E-39)
            int r7 = r7 * r6
            if (r7 == r2) goto L2b
        L41:
            j$.time.g r6 = D(r3, r7)
            goto L59
        L46:
            int r7 = (int) r6
            int r7 = r7 * 1000
            if (r7 == r2) goto L2b
            goto L41
        L4c:
            long r0 = (long) r2
            int r8 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r8 == 0) goto L2b
            int r7 = (int) r6
            goto L41
        L53:
            j$.time.temporal.m r6 = r8.r(r5, r6)
            j$.time.g r6 = (j$.time.g) r6
        L59:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.g.d(long, j$.time.temporal.r):j$.time.temporal.m");
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof g) {
            g gVar = (g) obj;
            return this.f3967a == gVar.f3967a && this.f3968b == gVar.f3968b;
        }
        return false;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? rVar == j$.time.temporal.a.INSTANT_SECONDS || rVar == j$.time.temporal.a.NANO_OF_SECOND || rVar == j$.time.temporal.a.MICRO_OF_SECOND || rVar == j$.time.temporal.a.MILLI_OF_SECOND : rVar != null && rVar.m(this);
    }

    public final int hashCode() {
        long j4 = this.f3967a;
        return (this.f3968b * 51) + ((int) (j4 ^ (j4 >>> 32)));
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = f.f3917a[((j$.time.temporal.a) rVar).ordinal()];
            int i5 = this.f3968b;
            if (i4 != 1) {
                if (i4 != 2) {
                    if (i4 != 3) {
                        if (i4 == 4) {
                            j$.time.temporal.a.INSTANT_SECONDS.z(this.f3967a);
                        }
                        throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
                    }
                    return i5 / 1000000;
                }
                return i5 / 1000;
            }
            return i5;
        }
        return j$.time.temporal.n.d(this, rVar).a(rVar.l(this), rVar);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m l(i iVar) {
        return (g) AbstractC0491i.a(iVar, this);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        return j$.time.temporal.n.d(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        int i4;
        if (rVar instanceof j$.time.temporal.a) {
            int i5 = f.f3917a[((j$.time.temporal.a) rVar).ordinal()];
            int i6 = this.f3968b;
            if (i5 != 1) {
                if (i5 == 2) {
                    i4 = i6 / 1000;
                } else if (i5 != 3) {
                    if (i5 == 4) {
                        return this.f3967a;
                    }
                    throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
                } else {
                    i4 = i6 / 1000000;
                }
                return i4;
            }
            return i6;
        }
        return rVar.l(this);
    }

    public final String toString() {
        return j$.time.format.a.f.a(this);
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        if (tVar == j$.time.temporal.n.i()) {
            return j$.time.temporal.b.NANOS;
        }
        if (tVar == j$.time.temporal.n.e() || tVar == j$.time.temporal.n.k() || tVar == j$.time.temporal.n.j() || tVar == j$.time.temporal.n.h() || tVar == j$.time.temporal.n.f() || tVar == j$.time.temporal.n.g()) {
            return null;
        }
        return tVar.a(this);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        return mVar.d(this.f3967a, j$.time.temporal.a.INSTANT_SECONDS).d(this.f3968b, j$.time.temporal.a.NANO_OF_SECOND);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return j4 == Long.MIN_VALUE ? e(Long.MAX_VALUE, uVar).e(1L, uVar) : e(-j4, uVar);
    }
}
