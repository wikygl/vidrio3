package j$.time.chrono;

import j$.time.AbstractC0495d;
import java.io.Serializable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.time.chrono.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public abstract class AbstractC0486d implements InterfaceC0484b, j$.time.temporal.m, j$.time.temporal.p, Serializable {
    private static final long serialVersionUID = 6282433883239719096L;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InterfaceC0484b D(n nVar, j$.time.temporal.m mVar) {
        InterfaceC0484b interfaceC0484b = (InterfaceC0484b) mVar;
        AbstractC0483a abstractC0483a = (AbstractC0483a) nVar;
        if (abstractC0483a.equals(interfaceC0484b.a())) {
            return interfaceC0484b;
        }
        String i4 = abstractC0483a.i();
        String i5 = interfaceC0484b.a().i();
        throw new ClassCastException("Chronology mismatch, expected: " + i4 + ", actual: " + i5);
    }

    @Override // java.lang.Comparable
    /* renamed from: A */
    public final /* synthetic */ int compareTo(InterfaceC0484b interfaceC0484b) {
        return AbstractC0491i.b(this, interfaceC0484b);
    }

    public o E() {
        return a().B(j$.time.temporal.n.a(this, j$.time.temporal.a.ERA));
    }

    @Override // j$.time.temporal.m
    /* renamed from: F */
    public InterfaceC0484b z(long j4, j$.time.temporal.u uVar) {
        return D(a(), j$.time.temporal.n.b(this, j4, uVar));
    }

    abstract InterfaceC0484b G(long j4);

    abstract InterfaceC0484b H(long j4);

    abstract InterfaceC0484b I(long j4);

    @Override // j$.time.temporal.m
    /* renamed from: J */
    public InterfaceC0484b l(j$.time.temporal.p pVar) {
        return D(a(), pVar.v(this));
    }

    @Override // j$.time.temporal.m
    public InterfaceC0484b d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
        return D(a(), rVar.r(this, j4));
    }

    @Override // j$.time.temporal.m
    public InterfaceC0484b e(long j4, j$.time.temporal.u uVar) {
        boolean z4 = uVar instanceof j$.time.temporal.b;
        if (!z4) {
            if (z4) {
                throw new RuntimeException("Unsupported unit: " + uVar);
            }
            return D(a(), uVar.j(this, j4));
        }
        switch (AbstractC0485c.f3874a[((j$.time.temporal.b) uVar).ordinal()]) {
            case 1:
                return G(j4);
            case 2:
                return G(j$.com.android.tools.r8.a.o(j4, 7));
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return H(j4);
            case 4:
                return I(j4);
            case 5:
                return I(j$.com.android.tools.r8.a.o(j4, 10));
            case 6:
                return I(j$.com.android.tools.r8.a.o(j4, 100));
            case 7:
                return I(j$.com.android.tools.r8.a.o(j4, 1000));
            case 8:
                j$.time.temporal.a aVar = j$.time.temporal.a.ERA;
                return d(j$.com.android.tools.r8.a.i(r(aVar), j4), (j$.time.temporal.r) aVar);
            default:
                throw new RuntimeException("Unsupported unit: " + uVar);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof InterfaceC0484b) && AbstractC0491i.b(this, (InterfaceC0484b) obj) == 0;
    }

    @Override // j$.time.chrono.InterfaceC0484b, j$.time.temporal.o
    public /* synthetic */ boolean f(j$.time.temporal.r rVar) {
        return AbstractC0491i.h(this, rVar);
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public int hashCode() {
        long s4 = s();
        return ((AbstractC0483a) a()).hashCode() ^ ((int) (s4 ^ (s4 >>> 32)));
    }

    @Override // j$.time.temporal.o
    public final /* synthetic */ int j(j$.time.temporal.r rVar) {
        return j$.time.temporal.n.a(this, rVar);
    }

    @Override // j$.time.temporal.o
    public /* synthetic */ j$.time.temporal.w m(j$.time.temporal.r rVar) {
        return j$.time.temporal.n.d(this, rVar);
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public long s() {
        return r(j$.time.temporal.a.EPOCH_DAY);
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public InterfaceC0487e t(j$.time.m mVar) {
        return C0489g.F(this, mVar);
    }

    @Override // j$.time.chrono.InterfaceC0484b
    public String toString() {
        long r4 = r(j$.time.temporal.a.YEAR_OF_ERA);
        long r5 = r(j$.time.temporal.a.MONTH_OF_YEAR);
        long r6 = r(j$.time.temporal.a.DAY_OF_MONTH);
        StringBuilder sb = new StringBuilder(30);
        sb.append(((AbstractC0483a) a()).i());
        sb.append(" ");
        sb.append(E());
        sb.append(" ");
        sb.append(r4);
        sb.append(r5 < 10 ? "-0" : "-");
        sb.append(r5);
        sb.append(r6 < 10 ? "-0" : "-");
        sb.append(r6);
        return sb.toString();
    }

    @Override // j$.time.temporal.o
    public final /* synthetic */ Object u(j$.time.temporal.t tVar) {
        return AbstractC0491i.j(this, tVar);
    }

    @Override // j$.time.temporal.p
    public final /* synthetic */ j$.time.temporal.m v(j$.time.temporal.m mVar) {
        return AbstractC0491i.a(this, mVar);
    }
}
