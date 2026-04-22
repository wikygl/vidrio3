package j2;

import W1.C0324l;
import android.app.Activity;
import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class x extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ int f4850n = 1;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ BinderC0672b f4851o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ Object f4852p;

    /* renamed from: q  reason: collision with root package name */
    public final /* synthetic */ Object f4853q;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public x(F f, Activity activity, BinderC0672b binderC0672b) {
        super(f.f4782j, true);
        this.f4853q = f;
        this.f4852p = activity;
        this.f4851o = binderC0672b;
    }

    @Override // j2.C
    public final void a() {
        switch (this.f4850n) {
            case 0:
                InterfaceC0675e interfaceC0675e = ((G) this.f4853q).f4790h;
                C0324l.d(interfaceC0675e);
                interfaceC0675e.n3((Bundle) this.f4852p, this.f4851o, this.f4773j);
                return;
            default:
                InterfaceC0675e interfaceC0675e2 = ((F) this.f4853q).f4782j.f4790h;
                C0324l.d(interfaceC0675e2);
                interfaceC0675e2.G2(new c2.b((Activity) this.f4852p), this.f4851o, this.f4774k);
                return;
        }
    }

    @Override // j2.C
    public void b() {
        switch (this.f4850n) {
            case 0:
                this.f4851o.Z(null);
                return;
            default:
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public x(G g4, Bundle bundle, BinderC0672b binderC0672b) {
        super(g4, true);
        this.f4853q = g4;
        this.f4852p = bundle;
        this.f4851o = binderC0672b;
    }
}
