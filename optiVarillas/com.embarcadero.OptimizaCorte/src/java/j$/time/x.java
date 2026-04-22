package j$.time;

import j$.time.chrono.AbstractC0483a;
import j$.time.chrono.AbstractC0491i;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class x implements j$.time.temporal.m, j$.time.temporal.p, Comparable, Serializable {

    /* renamed from: b  reason: collision with root package name */
    public static final /* synthetic */ int f4040b = 0;
    private static final long serialVersionUID = -23038383694477807L;

    /* renamed from: a  reason: collision with root package name */
    private final int f4041a;

    static {
        j$.time.format.o oVar = new j$.time.format.o();
        oVar.l(j$.time.temporal.a.YEAR, 4, 10, j$.time.format.v.EXCEEDS_PAD);
        oVar.v();
    }

    private x(int i4) {
        this.f4041a = i4;
    }

    public static x D(int i4) {
        j$.time.temporal.a.YEAR.D(i4);
        return new x(i4);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 11, this);
    }

    @Override // j$.time.temporal.m
    /* renamed from: E */
    public final x e(long j4, j$.time.temporal.u uVar) {
        if (uVar instanceof j$.time.temporal.b) {
            int i4 = w.f4039b[((j$.time.temporal.b) uVar).ordinal()];
            if (i4 != 1) {
                if (i4 != 2) {
                    if (i4 != 3) {
                        if (i4 != 4) {
                            if (i4 == 5) {
                                j$.time.temporal.a aVar = j$.time.temporal.a.ERA;
                                return d(j$.com.android.tools.r8.a.i(r(aVar), j4), aVar);
                            }
                            throw new RuntimeException("Unsupported unit: " + uVar);
                        }
                        return F(j$.com.android.tools.r8.a.o(j4, 1000));
                    }
                    return F(j$.com.android.tools.r8.a.o(j4, 100));
                }
                return F(j$.com.android.tools.r8.a.o(j4, 10));
            }
            return F(j4);
        }
        return (x) uVar.j(this, j4);
    }

    public final x F(long j4) {
        return j4 == 0 ? this : D(j$.time.temporal.a.YEAR.z(this.f4041a + j4));
    }

    @Override // j$.time.temporal.m
    /* renamed from: G */
    public final x d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
            aVar.D(j4);
            int i4 = w.f4038a[aVar.ordinal()];
            int i5 = this.f4041a;
            if (i4 == 1) {
                if (i5 < 1) {
                    j4 = 1 - j4;
                }
                return D((int) j4);
            } else if (i4 != 2) {
                if (i4 == 3) {
                    return r(j$.time.temporal.a.ERA) == j4 ? this : D(1 - i5);
                }
                throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
            } else {
                return D((int) j4);
            }
        }
        return (x) rVar.r(this, j4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void H(DataOutput dataOutput) {
        dataOutput.writeInt(this.f4041a);
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        return this.f4041a - ((x) obj).f4041a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof x) {
            return this.f4041a == ((x) obj).f4041a;
        }
        return false;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? rVar == j$.time.temporal.a.YEAR || rVar == j$.time.temporal.a.YEAR_OF_ERA || rVar == j$.time.temporal.a.ERA : rVar != null && rVar.m(this);
    }

    public final int hashCode() {
        return this.f4041a;
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        return m(rVar).a(r(rVar), rVar);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m l(i iVar) {
        return (x) AbstractC0491i.a(iVar, this);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        if (rVar == j$.time.temporal.a.YEAR_OF_ERA) {
            return j$.time.temporal.w.j(1L, this.f4041a <= 0 ? 1000000000L : 999999999L);
        }
        return j$.time.temporal.n.d(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = w.f4038a[((j$.time.temporal.a) rVar).ordinal()];
            int i5 = this.f4041a;
            if (i4 == 1) {
                if (i5 < 1) {
                    i5 = 1 - i5;
                }
                return i5;
            } else if (i4 != 2) {
                if (i4 == 3) {
                    return i5 < 1 ? 0 : 1;
                }
                throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
            } else {
                return i5;
            }
        }
        return rVar.l(this);
    }

    public final String toString() {
        return Integer.toString(this.f4041a);
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        return tVar == j$.time.temporal.n.e() ? j$.time.chrono.u.f3906d : tVar == j$.time.temporal.n.i() ? j$.time.temporal.b.YEARS : j$.time.temporal.n.c(this, tVar);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        if (((AbstractC0483a) AbstractC0491i.p(mVar)).equals(j$.time.chrono.u.f3906d)) {
            return mVar.d(this.f4041a, j$.time.temporal.a.YEAR);
        }
        throw new RuntimeException("Adjustment only supported on ISO date-time");
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m z(long j4, j$.time.temporal.u uVar) {
        return j4 == Long.MIN_VALUE ? e(Long.MAX_VALUE, uVar).e(1L, uVar) : e(-j4, uVar);
    }
}
