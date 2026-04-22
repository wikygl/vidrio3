package i2;

import j$.util.Objects;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class T extends P {

    /* renamed from: n  reason: collision with root package name */
    public static final T f3693n = new T(0, new Object[0]);

    /* renamed from: l  reason: collision with root package name */
    public final transient Object[] f3694l;

    /* renamed from: m  reason: collision with root package name */
    public final transient int f3695m;

    public T(int i4, Object[] objArr) {
        this.f3694l = objArr;
        this.f3695m = i4;
    }

    @Override // java.util.List
    public final Object get(int i4) {
        J.a(i4, this.f3695m);
        Object obj = this.f3694l[i4];
        Objects.requireNonNull(obj);
        return obj;
    }

    @Override // i2.P, i2.M
    public final void j(Object[] objArr) {
        System.arraycopy(this.f3694l, 0, objArr, 0, this.f3695m);
    }

    @Override // i2.M
    public final int k() {
        return this.f3695m;
    }

    @Override // i2.M
    public final int l() {
        return 0;
    }

    @Override // i2.M
    public final Object[] m() {
        return this.f3694l;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.f3695m;
    }
}
