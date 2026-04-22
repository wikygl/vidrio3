package i2;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class O extends P {

    /* renamed from: l  reason: collision with root package name */
    public final transient int f3686l;

    /* renamed from: m  reason: collision with root package name */
    public final transient int f3687m;

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ P f3688n;

    public O(P p4, int i4, int i5) {
        this.f3688n = p4;
        this.f3686l = i4;
        this.f3687m = i5;
    }

    @Override // java.util.List
    public final Object get(int i4) {
        J.a(i4, this.f3687m);
        return this.f3688n.get(i4 + this.f3686l);
    }

    @Override // i2.M
    public final int k() {
        return this.f3688n.l() + this.f3686l + this.f3687m;
    }

    @Override // i2.M
    public final int l() {
        return this.f3688n.l() + this.f3686l;
    }

    @Override // i2.M
    public final Object[] m() {
        return this.f3688n.m();
    }

    @Override // i2.P, java.util.List
    /* renamed from: n */
    public final P subList(int i4, int i5) {
        J.b(i4, i5, this.f3687m);
        int i6 = this.f3686l;
        return this.f3688n.subList(i4 + i6, i5 + i6);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.f3687m;
    }
}
