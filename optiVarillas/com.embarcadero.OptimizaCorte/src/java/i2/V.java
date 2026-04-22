package i2;

import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class V extends Q {

    /* renamed from: l  reason: collision with root package name */
    public final transient Object f3703l;

    public V(Object obj) {
        this.f3703l = obj;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final boolean contains(Object obj) {
        return this.f3703l.equals(obj);
    }

    @Override // i2.Q, java.util.Collection, java.util.Set
    public final int hashCode() {
        return this.f3703l.hashCode();
    }

    @Override // i2.Q, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public final /* synthetic */ Iterator iterator() {
        return new S(this.f3703l);
    }

    @Override // i2.M
    public final void j(Object[] objArr) {
        objArr[0] = this.f3703l;
    }

    @Override // i2.Q
    public final W n() {
        return new S(this.f3703l);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public final int size() {
        return 1;
    }

    @Override // java.util.AbstractCollection
    public final String toString() {
        return C.b.b("[", this.f3703l.toString(), "]");
    }
}
