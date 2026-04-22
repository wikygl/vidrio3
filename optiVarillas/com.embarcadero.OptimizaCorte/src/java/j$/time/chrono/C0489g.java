package j$.time.chrono;

import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.time.chrono.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0489g implements InterfaceC0487e, j$.time.temporal.m, j$.time.temporal.p, Serializable {
    private static final long serialVersionUID = 4556003607393004514L;

    /* renamed from: a  reason: collision with root package name */
    private final transient InterfaceC0484b f3876a;

    /* renamed from: b  reason: collision with root package name */
    private final transient j$.time.m f3877b;

    private C0489g(InterfaceC0484b interfaceC0484b, j$.time.m mVar) {
        Objects.requireNonNull(interfaceC0484b, "date");
        Objects.requireNonNull(mVar, "time");
        this.f3876a = interfaceC0484b;
        this.f3877b = mVar;
    }

    static C0489g D(n nVar, j$.time.temporal.m mVar) {
        C0489g c0489g = (C0489g) mVar;
        AbstractC0483a abstractC0483a = (AbstractC0483a) nVar;
        if (abstractC0483a.equals(c0489g.f3876a.a())) {
            return c0489g;
        }
        String i4 = abstractC0483a.i();
        String i5 = c0489g.f3876a.a().i();
        throw new ClassCastException("Chronology mismatch, required: " + i4 + ", actual: " + i5);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static C0489g F(InterfaceC0484b interfaceC0484b, j$.time.m mVar) {
        return new C0489g(interfaceC0484b, mVar);
    }

    private C0489g I(InterfaceC0484b interfaceC0484b, long j4, long j5, long j6, long j7) {
        j$.time.m mVar = this.f3877b;
        if ((j4 | j5 | j6 | j7) == 0) {
            return K(interfaceC0484b, mVar);
        }
        long j8 = j5 / 1440;
        long j9 = j4 / 24;
        long j10 = (j5 % 1440) * 60000000000L;
        long j11 = ((j4 % 24) * 3600000000000L) + j10 + ((j6 % 86400) * 1000000000) + (j7 % 86400000000000L);
        long T3 = mVar.T();
        long j12 = j11 + T3;
        long n4 = j$.com.android.tools.r8.a.n(j12, 86400000000000L) + j9 + j8 + (j6 / 86400) + (j7 / 86400000000000L);
        long m4 = j$.com.android.tools.r8.a.m(j12, 86400000000000L);
        if (m4 != T3) {
            mVar = j$.time.m.L(m4);
        }
        return K(interfaceC0484b.e(n4, (j$.time.temporal.u) j$.time.temporal.b.DAYS), mVar);
    }

    private C0489g K(j$.time.temporal.m mVar, j$.time.m mVar2) {
        InterfaceC0484b interfaceC0484b = this.f3876a;
        return (interfaceC0484b == mVar && this.f3877b == mVar2) ? this : new C0489g(AbstractC0486d.D(interfaceC0484b.a(), mVar), mVar2);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new G((byte) 2, this);
    }

    @Override // j$.time.temporal.m
    /* renamed from: E */
    public final InterfaceC0487e z(long j4, j$.time.temporal.u uVar) {
        return D(a(), j$.time.temporal.n.b(this, j4, uVar));
    }

    @Override // j$.time.temporal.m
    /* renamed from: G */
    public final C0489g e(long j4, j$.time.temporal.u uVar) {
        boolean z4 = uVar instanceof j$.time.temporal.b;
        InterfaceC0484b interfaceC0484b = this.f3876a;
        if (z4) {
            int i4 = AbstractC0488f.f3875a[((j$.time.temporal.b) uVar).ordinal()];
            j$.time.m mVar = this.f3877b;
            switch (i4) {
                case 1:
                    return I(this.f3876a, 0L, 0L, 0L, j4);
                case 2:
                    C0489g K3 = K(interfaceC0484b.e(j4 / 86400000000L, (j$.time.temporal.u) j$.time.temporal.b.DAYS), mVar);
                    return K3.I(K3.f3876a, 0L, 0L, 0L, (j4 % 86400000000L) * 1000);
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    C0489g K4 = K(interfaceC0484b.e(j4 / 86400000, (j$.time.temporal.u) j$.time.temporal.b.DAYS), mVar);
                    return K4.I(K4.f3876a, 0L, 0L, 0L, (j4 % 86400000) * 1000000);
                case 4:
                    return H(j4);
                case 5:
                    return I(this.f3876a, 0L, j4, 0L, 0L);
                case 6:
                    return I(this.f3876a, j4, 0L, 0L, 0L);
                case 7:
                    C0489g K5 = K(interfaceC0484b.e(j4 / 256, (j$.time.temporal.u) j$.time.temporal.b.DAYS), mVar);
                    return K5.I(K5.f3876a, (j4 % 256) * 12, 0L, 0L, 0L);
                default:
                    return K(interfaceC0484b.e(j4, uVar), mVar);
            }
        }
        return D(interfaceC0484b.a(), uVar.j(this, j4));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final C0489g H(long j4) {
        return I(this.f3876a, 0L, 0L, j4, 0L);
    }

    @Override // j$.time.temporal.m
    /* renamed from: J */
    public final C0489g d(long j4, j$.time.temporal.r rVar) {
        boolean z4 = rVar instanceof j$.time.temporal.a;
        InterfaceC0484b interfaceC0484b = this.f3876a;
        if (z4) {
            boolean E4 = ((j$.time.temporal.a) rVar).E();
            j$.time.m mVar = this.f3877b;
            return E4 ? K(interfaceC0484b, mVar.d(j4, rVar)) : K(interfaceC0484b.d(j4, rVar), mVar);
        }
        return D(interfaceC0484b.a(), rVar.r(this, j4));
    }

    @Override // j$.time.chrono.InterfaceC0487e
    public final n a() {
        return this.f3876a.a();
    }

    @Override // j$.time.chrono.InterfaceC0487e
    public final j$.time.m b() {
        return this.f3877b;
    }

    @Override // j$.time.chrono.InterfaceC0487e
    public final InterfaceC0484b c() {
        return this.f3876a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof InterfaceC0487e) && AbstractC0491i.c(this, (InterfaceC0487e) obj) == 0;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        if (!(rVar instanceof j$.time.temporal.a)) {
            return rVar != null && rVar.m(this);
        }
        j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
        return aVar.v() || aVar.E();
    }

    public final int hashCode() {
        return this.f3876a.hashCode() ^ this.f3877b.hashCode();
    }

    @Override // j$.time.temporal.o
    public final int j(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? ((j$.time.temporal.a) rVar).E() ? this.f3877b.j(rVar) : this.f3876a.j(rVar) : m(rVar).a(r(rVar), rVar);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m l(j$.time.i iVar) {
        return K(iVar, this.f3877b);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            if (((j$.time.temporal.a) rVar).E()) {
                j$.time.m mVar = this.f3877b;
                mVar.getClass();
                return j$.time.temporal.n.d(mVar, rVar);
            }
            return this.f3876a.m(rVar);
        }
        return rVar.u(this);
    }

    @Override // j$.time.chrono.InterfaceC0487e
    public final InterfaceC0493k o(j$.time.B b4) {
        return m.F(b4, null, this);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? ((j$.time.temporal.a) rVar).E() ? this.f3877b.r(rVar) : this.f3876a.r(rVar) : rVar.l(this);
    }

    public final String toString() {
        String interfaceC0484b = this.f3876a.toString();
        String mVar = this.f3877b.toString();
        return interfaceC0484b + "T" + mVar;
    }

    @Override // j$.time.temporal.o
    public final /* synthetic */ Object u(j$.time.temporal.t tVar) {
        return AbstractC0491i.k(this, tVar);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        return mVar.d(c().s(), j$.time.temporal.a.EPOCH_DAY).d(b().T(), j$.time.temporal.a.NANO_OF_DAY);
    }

    @Override // java.lang.Comparable
    /* renamed from: w */
    public final /* synthetic */ int compareTo(InterfaceC0487e interfaceC0487e) {
        return AbstractC0491i.c(this, interfaceC0487e);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void writeExternal(ObjectOutput objectOutput) {
        objectOutput.writeObject(this.f3876a);
        objectOutput.writeObject(this.f3877b);
    }
}
