package s;

import s.C0791d;

/* renamed from: s.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0789b<T> {

    /* renamed from: a  reason: collision with root package name */
    public Object f5749a;

    /* renamed from: b  reason: collision with root package name */
    public C0791d<T> f5750b;

    /* renamed from: c  reason: collision with root package name */
    public C0792e<Void> f5751c = new AbstractC0788a();

    /* renamed from: d  reason: collision with root package name */
    public boolean f5752d;

    public final void finalize() {
        C0792e<Void> c0792e;
        C0791d<T> c0791d = this.f5750b;
        if (c0791d != null) {
            C0791d.a aVar = c0791d.f5754k;
            if (!aVar.isDone()) {
                aVar.i(new Throwable("The completer object was garbage collected - this future would otherwise never complete. The tag was: " + this.f5749a));
            }
        }
        if (!this.f5752d && (c0792e = this.f5751c) != null) {
            c0792e.j(null);
        }
    }
}
