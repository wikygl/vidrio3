package j2;

import W1.C0324l;
import android.app.Activity;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class u extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ int f4839n = 1;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ Object f4840o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ Object f4841p;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public u(F f, Activity activity) {
        super(f.f4782j, true);
        this.f4841p = f;
        this.f4840o = activity;
    }

    @Override // j2.C
    public final void a() {
        switch (this.f4839n) {
            case 0:
                InterfaceC0675e interfaceC0675e = ((G) this.f4841p).f4790h;
                C0324l.d(interfaceC0675e);
                interfaceC0675e.a3((BinderC0672b) this.f4840o);
                return;
            default:
                InterfaceC0675e interfaceC0675e2 = ((F) this.f4841p).f4782j.f4790h;
                C0324l.d(interfaceC0675e2);
                interfaceC0675e2.c1(new c2.b((Activity) this.f4840o), this.f4774k);
                return;
        }
    }

    @Override // j2.C
    public void b() {
        switch (this.f4839n) {
            case 0:
                ((BinderC0672b) this.f4840o).Z(null);
                return;
            default:
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public u(G g4, BinderC0672b binderC0672b) {
        super(g4, true);
        this.f4841p = g4;
        this.f4840o = binderC0672b;
    }
}
