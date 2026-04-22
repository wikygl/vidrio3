package r;

import java.util.Map;

/* renamed from: r.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0774c extends i<Object, Object> {

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ C0775d f5637d;

    public C0774c(C0775d c0775d) {
        this.f5637d = c0775d;
    }

    @Override // r.i
    public final void a() {
        this.f5637d.clear();
    }

    @Override // r.i
    public final Object b(int i4, int i5) {
        return this.f5637d.f5645k[i4];
    }

    @Override // r.i
    public final Map<Object, Object> c() {
        throw new UnsupportedOperationException("not a map");
    }

    @Override // r.i
    public final int d() {
        return this.f5637d.f5646l;
    }

    @Override // r.i
    public final int e(Object obj) {
        return this.f5637d.indexOf(obj);
    }

    @Override // r.i
    public final int f(Object obj) {
        return this.f5637d.indexOf(obj);
    }

    @Override // r.i
    public final void g(Object obj, Object obj2) {
        this.f5637d.add(obj);
    }

    @Override // r.i
    public final void h(int i4) {
        this.f5637d.n(i4);
    }

    @Override // r.i
    public final Object i(int i4, Object obj) {
        throw new UnsupportedOperationException("not a map");
    }
}
