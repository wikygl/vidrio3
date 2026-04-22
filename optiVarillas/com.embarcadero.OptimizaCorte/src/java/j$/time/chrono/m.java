package j$.time.chrono;

import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class m implements InterfaceC0493k, Serializable {
    private static final long serialVersionUID = -5261813987200935591L;

    /* renamed from: a  reason: collision with root package name */
    private final transient C0489g f3885a;

    /* renamed from: b  reason: collision with root package name */
    private final transient j$.time.B f3886b;

    /* renamed from: c  reason: collision with root package name */
    private final transient j$.time.A f3887c;

    private m(j$.time.A a4, j$.time.B b4, C0489g c0489g) {
        this.f3885a = (C0489g) Objects.requireNonNull(c0489g, "dateTime");
        this.f3886b = (j$.time.B) Objects.requireNonNull(b4, "offset");
        this.f3887c = (j$.time.A) Objects.requireNonNull(a4, "zone");
    }

    static m D(n nVar, j$.time.temporal.m mVar) {
        m mVar2 = (m) mVar;
        AbstractC0483a abstractC0483a = (AbstractC0483a) nVar;
        if (abstractC0483a.equals(mVar2.a())) {
            return mVar2;
        }
        String i4 = abstractC0483a.i();
        String i5 = mVar2.a().i();
        throw new ClassCastException("Chronology mismatch, required: " + i4 + ", actual: " + i5);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0053, code lost:
        if (r2.contains(r7) != false) goto L9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static j$.time.chrono.InterfaceC0493k F(j$.time.A r6, j$.time.B r7, j$.time.chrono.C0489g r8) {
        /*
            java.lang.String r0 = "localDateTime"
            j$.util.Objects.requireNonNull(r8, r0)
            java.lang.String r0 = "zone"
            j$.util.Objects.requireNonNull(r6, r0)
            boolean r0 = r6 instanceof j$.time.B
            if (r0 == 0) goto L17
            j$.time.chrono.m r7 = new j$.time.chrono.m
            r0 = r6
            j$.time.B r0 = (j$.time.B) r0
            r7.<init>(r6, r0, r8)
            return r7
        L17:
            j$.time.zone.f r0 = r6.D()
            j$.time.k r1 = j$.time.k.E(r8)
            java.util.List r2 = r0.g(r1)
            int r3 = r2.size()
            r4 = 1
            r5 = 0
            if (r3 != r4) goto L32
        L2b:
            java.lang.Object r7 = r2.get(r5)
            j$.time.B r7 = (j$.time.B) r7
            goto L55
        L32:
            int r3 = r2.size()
            if (r3 != 0) goto L4d
            j$.time.zone.b r7 = r0.f(r1)
            j$.time.Duration r0 = r7.m()
            long r0 = r0.m()
            j$.time.chrono.g r8 = r8.H(r0)
            j$.time.B r7 = r7.r()
            goto L55
        L4d:
            if (r7 == 0) goto L2b
            boolean r0 = r2.contains(r7)
            if (r0 == 0) goto L2b
        L55:
            java.lang.String r0 = "offset"
            j$.util.Objects.requireNonNull(r7, r0)
            j$.time.chrono.m r0 = new j$.time.chrono.m
            r0.<init>(r6, r7, r8)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.chrono.m.F(j$.time.A, j$.time.B, j$.time.chrono.g):j$.time.chrono.k");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static m G(n nVar, j$.time.g gVar, j$.time.A a4) {
        j$.time.B d4 = a4.D().d(gVar);
        Objects.requireNonNull(d4, "offset");
        return new m(a4, d4, (C0489g) nVar.n(j$.time.k.M(gVar.E(), gVar.F(), d4)));
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new G((byte) 3, this);
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final /* synthetic */ long C() {
        return AbstractC0491i.o(this);
    }

    @Override // j$.time.temporal.m
    /* renamed from: E */
    public final InterfaceC0493k z(long j4, j$.time.temporal.u uVar) {
        return D(a(), j$.time.temporal.n.b(this, j4, uVar));
    }

    @Override // j$.time.temporal.m
    /* renamed from: H */
    public final InterfaceC0493k e(long j4, j$.time.temporal.u uVar) {
        if (uVar instanceof j$.time.temporal.b) {
            return D(a(), this.f3885a.e(j4, uVar).v(this));
        }
        return D(a(), uVar.j(this, j4));
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final n a() {
        return c().a();
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final j$.time.m b() {
        return ((C0489g) x()).b();
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final InterfaceC0484b c() {
        return ((C0489g) x()).c();
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m d(long j4, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            j$.time.temporal.a aVar = (j$.time.temporal.a) rVar;
            int i4 = AbstractC0494l.f3884a[aVar.ordinal()];
            if (i4 != 1) {
                j$.time.A a4 = this.f3887c;
                C0489g c0489g = this.f3885a;
                if (i4 != 2) {
                    return F(a4, this.f3886b, c0489g.d(j4, rVar));
                }
                j$.time.B M3 = j$.time.B.M(aVar.z(j4));
                c0489g.getClass();
                return G(a(), j$.time.g.H(AbstractC0491i.n(c0489g, M3), c0489g.b().I()), a4);
            }
            return e(j4 - AbstractC0491i.o(this), j$.time.temporal.b.SECONDS);
        }
        return D(a(), rVar.r(this, j4));
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof InterfaceC0493k) && AbstractC0491i.d(this, (InterfaceC0493k) obj) == 0;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        return (rVar instanceof j$.time.temporal.a) || (rVar != null && rVar.m(this));
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final j$.time.B g() {
        return this.f3886b;
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final InterfaceC0493k h(j$.time.A a4) {
        return F(a4, this.f3886b, this.f3885a);
    }

    public final int hashCode() {
        return (this.f3885a.hashCode() ^ this.f3886b.hashCode()) ^ Integer.rotateLeft(this.f3887c.hashCode(), 3);
    }

    @Override // j$.time.temporal.o
    public final /* synthetic */ int j(j$.time.temporal.r rVar) {
        return AbstractC0491i.e(this, rVar);
    }

    @Override // j$.time.temporal.m
    public final j$.time.temporal.m l(j$.time.i iVar) {
        return D(a(), iVar.v(this));
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? (rVar == j$.time.temporal.a.INSTANT_SECONDS || rVar == j$.time.temporal.a.OFFSET_SECONDS) ? ((j$.time.temporal.a) rVar).j() : ((C0489g) x()).m(rVar) : rVar.u(this);
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final j$.time.A p() {
        return this.f3887c;
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = AbstractC0492j.f3883a[((j$.time.temporal.a) rVar).ordinal()];
            return i4 != 1 ? i4 != 2 ? ((C0489g) x()).r(rVar) : g().J() : C();
        }
        return rVar.l(this);
    }

    public final String toString() {
        String c0489g = this.f3885a.toString();
        j$.time.B b4 = this.f3886b;
        String str = c0489g + b4.toString();
        j$.time.A a4 = this.f3887c;
        if (b4 != a4) {
            return str + "[" + a4.toString() + "]";
        }
        return str;
    }

    @Override // j$.time.temporal.o
    public final /* synthetic */ Object u(j$.time.temporal.t tVar) {
        return AbstractC0491i.l(this, tVar);
    }

    @Override // java.lang.Comparable
    /* renamed from: v */
    public final /* synthetic */ int compareTo(InterfaceC0493k interfaceC0493k) {
        return AbstractC0491i.d(this, interfaceC0493k);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void writeExternal(ObjectOutput objectOutput) {
        objectOutput.writeObject(this.f3885a);
        objectOutput.writeObject(this.f3886b);
        objectOutput.writeObject(this.f3887c);
    }

    @Override // j$.time.chrono.InterfaceC0493k
    public final InterfaceC0487e x() {
        return this.f3885a;
    }
}
