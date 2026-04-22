package L2;

import android.graphics.Typeface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class a extends G3.g {

    /* renamed from: k  reason: collision with root package name */
    public final Typeface f1488k;

    /* renamed from: l  reason: collision with root package name */
    public final InterfaceC0012a f1489l;

    /* renamed from: m  reason: collision with root package name */
    public boolean f1490m;

    /* renamed from: L2.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface InterfaceC0012a {
    }

    public a(H2.b bVar, Typeface typeface) {
        this.f1488k = typeface;
        this.f1489l = bVar;
    }

    @Override // G3.g
    public final void B(int i4) {
        if (!this.f1490m) {
            H2.c cVar = ((H2.b) this.f1489l).f1021a;
            if (cVar.j(this.f1488k)) {
                cVar.h(false);
            }
        }
    }

    @Override // G3.g
    public final void C(Typeface typeface, boolean z4) {
        if (!this.f1490m) {
            H2.c cVar = ((H2.b) this.f1489l).f1021a;
            if (cVar.j(typeface)) {
                cVar.h(false);
            }
        }
    }
}
