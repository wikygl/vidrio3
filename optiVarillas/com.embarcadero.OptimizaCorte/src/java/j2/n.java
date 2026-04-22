package j2;

import W1.C0324l;
import android.app.Activity;
import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class n extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ int f4819n = 1;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ Object f4820o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ Object f4821p;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public n(F f, Activity activity) {
        super(f.f4782j, true);
        this.f4821p = f;
        this.f4820o = activity;
    }

    @Override // j2.C
    public final void a() {
        switch (this.f4819n) {
            case 0:
                InterfaceC0675e interfaceC0675e = ((G) this.f4821p).f4790h;
                C0324l.d(interfaceC0675e);
                interfaceC0675e.T2((Bundle) this.f4820o, this.f4773j);
                return;
            default:
                InterfaceC0675e interfaceC0675e2 = ((F) this.f4821p).f4782j.f4790h;
                C0324l.d(interfaceC0675e2);
                interfaceC0675e2.w4(new c2.b((Activity) this.f4820o), this.f4774k);
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public n(G g4, Bundle bundle) {
        super(g4, true);
        this.f4821p = g4;
        this.f4820o = bundle;
    }
}
