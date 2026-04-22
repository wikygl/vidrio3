package r;

import java.util.Map;

/* renamed from: r.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0772a extends i<Object, Object> {

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ C0773b f5635d;

    public C0772a(C0773b c0773b) {
        this.f5635d = c0773b;
    }

    @Override // r.i
    public final void a() {
        this.f5635d.clear();
    }

    @Override // r.i
    public final Object b(int i4, int i5) {
        return this.f5635d.f5684k[(i4 << 1) + i5];
    }

    @Override // r.i
    public final Map<Object, Object> c() {
        return this.f5635d;
    }

    @Override // r.i
    public final int d() {
        return this.f5635d.f5685l;
    }

    @Override // r.i
    public final int e(Object obj) {
        return this.f5635d.e(obj);
    }

    @Override // r.i
    public final int f(Object obj) {
        return this.f5635d.g(obj);
    }

    @Override // r.i
    public final void g(Object obj, Object obj2) {
        this.f5635d.put(obj, obj2);
    }

    @Override // r.i
    public final void h(int i4) {
        this.f5635d.i(i4);
    }

    @Override // r.i
    public final Object i(int i4, Object obj) {
        int i5 = (i4 << 1) + 1;
        Object[] objArr = this.f5635d.f5684k;
        Object obj2 = objArr[i5];
        objArr[i5] = obj;
        return obj2;
    }
}
