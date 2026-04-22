package j$.time.chrono;

import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class A implements o, Serializable {

    /* renamed from: d  reason: collision with root package name */
    public static final A f3854d;

    /* renamed from: e  reason: collision with root package name */
    private static final A[] f3855e;
    private static final long serialVersionUID = 1466499369062886794L;

    /* renamed from: a  reason: collision with root package name */
    private final transient int f3856a;

    /* renamed from: b  reason: collision with root package name */
    private final transient j$.time.i f3857b;

    /* renamed from: c  reason: collision with root package name */
    private final transient String f3858c;

    static {
        A a4 = new A(-1, j$.time.i.O(1868, 1, 1), "Meiji");
        f3854d = a4;
        A a5 = new A(0, j$.time.i.O(1912, 7, 30), "Taisho");
        A a6 = new A(1, j$.time.i.O(1926, 12, 25), "Showa");
        A a7 = new A(2, j$.time.i.O(1989, 1, 8), "Heisei");
        A a8 = new A(3, j$.time.i.O(2019, 5, 1), "Reiwa");
        f3855e = r8;
        A[] aArr = {a4, a5, a6, a7, a8};
    }

    private A(int i4, j$.time.i iVar, String str) {
        this.f3856a = i4;
        this.f3857b = iVar;
        this.f3858c = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long B() {
        A[] aArr;
        long f = j$.time.temporal.a.DAY_OF_YEAR.j().f();
        for (A a4 : f3855e) {
            f = Math.min(f, ((a4.f3857b.M() ? 366 : 365) - a4.f3857b.I()) + 1);
            if (a4.q() != null) {
                f = Math.min(f, a4.q().f3857b.I() - 1);
            }
        }
        return f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long D() {
        int K3 = 1000000000 - k().f3857b.K();
        A[] aArr = f3855e;
        int K4 = aArr[0].f3857b.K();
        for (int i4 = 1; i4 < aArr.length; i4++) {
            A a4 = aArr[i4];
            K3 = Math.min(K3, (a4.f3857b.K() - K4) + 1);
            K4 = a4.f3857b.K();
        }
        return K3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static A i(j$.time.i iVar) {
        if (iVar.L(z.f3911d)) {
            throw new RuntimeException("JapaneseDate before Meiji 6 are not supported");
        }
        A[] aArr = f3855e;
        for (int length = aArr.length - 1; length >= 0; length--) {
            A a4 = aArr[length];
            if (iVar.compareTo(a4.f3857b) >= 0) {
                return a4;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static A k() {
        A[] aArr = f3855e;
        return aArr[aArr.length - 1];
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new G((byte) 5, this);
    }

    public static A y(int i4) {
        int i5 = i4 + 1;
        if (i5 >= 0) {
            A[] aArr = f3855e;
            if (i5 < aArr.length) {
                return aArr[i5];
            }
        }
        throw new RuntimeException("Invalid era: " + i4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void E(DataOutput dataOutput) {
        dataOutput.writeByte(this.f3856a);
    }

    @Override // j$.time.temporal.o
    public final /* synthetic */ boolean f(j$.time.temporal.r rVar) {
        return AbstractC0491i.i(this, rVar);
    }

    @Override // j$.time.chrono.o
    public final int getValue() {
        return this.f3856a;
    }

    @Override // j$.time.temporal.o
    public final /* synthetic */ int j(j$.time.temporal.r rVar) {
        return AbstractC0491i.f(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        j$.time.temporal.a aVar = j$.time.temporal.a.ERA;
        return rVar == aVar ? x.f3909d.m(aVar) : j$.time.temporal.n.d(this, rVar);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final j$.time.i n() {
        return this.f3857b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final A q() {
        if (this == k()) {
            return null;
        }
        return y(this.f3856a + 1);
    }

    @Override // j$.time.temporal.o
    public final /* synthetic */ long r(j$.time.temporal.r rVar) {
        return AbstractC0491i.g(this, rVar);
    }

    public final String toString() {
        return this.f3858c;
    }

    @Override // j$.time.temporal.o
    public final /* synthetic */ Object u(j$.time.temporal.t tVar) {
        return AbstractC0491i.m(this, tVar);
    }

    @Override // j$.time.temporal.p
    public final j$.time.temporal.m v(j$.time.temporal.m mVar) {
        return mVar.d(getValue(), j$.time.temporal.a.ERA);
    }
}
