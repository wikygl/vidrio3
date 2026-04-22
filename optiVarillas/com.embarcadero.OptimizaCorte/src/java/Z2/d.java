package Z2;

import j$.util.Objects;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class d<E> extends c<E> {

    /* renamed from: n  reason: collision with root package name */
    public static final d f2851n = new d(0, new Object[0]);

    /* renamed from: l  reason: collision with root package name */
    public final transient Object[] f2852l;

    /* renamed from: m  reason: collision with root package name */
    public final transient int f2853m;

    public d(int i4, Object[] objArr) {
        this.f2852l = objArr;
        this.f2853m = i4;
    }

    @Override // java.util.List
    public final E get(int i4) {
        A0.c.c(i4, this.f2853m);
        E e4 = (E) this.f2852l[i4];
        Objects.requireNonNull(e4);
        return e4;
    }

    @Override // Z2.c, Z2.b
    public final int j(Object[] objArr) {
        Object[] objArr2 = this.f2852l;
        int i4 = this.f2853m;
        System.arraycopy(objArr2, 0, objArr, 0, i4);
        return i4;
    }

    @Override // Z2.b
    public final Object[] k() {
        return this.f2852l;
    }

    @Override // Z2.b
    public final int l() {
        return this.f2853m;
    }

    @Override // Z2.b
    public final int m() {
        return 0;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.f2853m;
    }
}
