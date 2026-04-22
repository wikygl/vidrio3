package V1;

import U1.a;
import W1.AbstractC0314b;
import W1.InterfaceC0320h;
import java.util.Set;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class x implements AbstractC0314b.c, F {

    /* renamed from: a  reason: collision with root package name */
    public final a.e f2621a;

    /* renamed from: b  reason: collision with root package name */
    public final C0295a f2622b;

    /* renamed from: c  reason: collision with root package name */
    public InterfaceC0320h f2623c = null;

    /* renamed from: d  reason: collision with root package name */
    public Set f2624d = null;

    /* renamed from: e  reason: collision with root package name */
    public boolean f2625e = false;
    public final /* synthetic */ C0298d f;

    public x(C0298d c0298d, a.e eVar, C0295a c0295a) {
        this.f = c0298d;
        this.f2621a = eVar;
        this.f2622b = c0295a;
    }

    @Override // W1.AbstractC0314b.c
    public final void a(T1.b bVar) {
        this.f.f2587v.post(new w(this, bVar));
    }

    public final void b(T1.b bVar) {
        u uVar = (u) this.f.f2584s.get(this.f2622b);
        if (uVar != null) {
            uVar.n(bVar);
        }
    }
}
