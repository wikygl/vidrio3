package i2;

import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class U extends Q {

    /* renamed from: q  reason: collision with root package name */
    public static final Object[] f3696q;

    /* renamed from: r  reason: collision with root package name */
    public static final U f3697r;

    /* renamed from: l  reason: collision with root package name */
    public final transient Object[] f3698l;

    /* renamed from: m  reason: collision with root package name */
    public final transient int f3699m;

    /* renamed from: n  reason: collision with root package name */
    public final transient Object[] f3700n;

    /* renamed from: o  reason: collision with root package name */
    public final transient int f3701o;

    /* renamed from: p  reason: collision with root package name */
    public final transient int f3702p;

    static {
        Object[] objArr = new Object[0];
        f3696q = objArr;
        f3697r = new U(0, 0, 0, objArr, objArr);
    }

    public U(int i4, int i5, int i6, Object[] objArr, Object[] objArr2) {
        this.f3698l = objArr;
        this.f3699m = i4;
        this.f3700n = objArr2;
        this.f3701o = i5;
        this.f3702p = i6;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final boolean contains(Object obj) {
        if (obj != null) {
            Object[] objArr = this.f3700n;
            if (objArr.length != 0) {
                int rotateLeft = (int) (Integer.rotateLeft((int) (obj.hashCode() * (-862048943)), 15) * 461845907);
                while (true) {
                    int i4 = this.f3701o & rotateLeft;
                    Object obj2 = objArr[i4];
                    if (obj2 == null) {
                        return false;
                    }
                    if (obj2.equals(obj)) {
                        return true;
                    }
                    rotateLeft = i4 + 1;
                }
            }
        }
        return false;
    }

    @Override // i2.Q, java.util.Collection, java.util.Set
    public final int hashCode() {
        return this.f3699m;
    }

    @Override // i2.Q, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public final Iterator iterator() {
        P p4 = this.f3690k;
        if (p4 == null) {
            p4 = q();
            this.f3690k = p4;
        }
        return p4.listIterator(0);
    }

    @Override // i2.M
    public final void j(Object[] objArr) {
        System.arraycopy(this.f3698l, 0, objArr, 0, this.f3702p);
    }

    @Override // i2.M
    public final int k() {
        return this.f3702p;
    }

    @Override // i2.M
    public final int l() {
        return 0;
    }

    @Override // i2.M
    public final Object[] m() {
        return this.f3698l;
    }

    @Override // i2.Q
    public final W n() {
        P p4 = this.f3690k;
        if (p4 == null) {
            p4 = q();
            this.f3690k = p4;
        }
        return p4.listIterator(0);
    }

    public final T q() {
        N n4 = P.f3689k;
        int i4 = this.f3702p;
        if (i4 == 0) {
            return T.f3693n;
        }
        return new T(i4, this.f3698l);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final int size() {
        return this.f3702p;
    }
}
