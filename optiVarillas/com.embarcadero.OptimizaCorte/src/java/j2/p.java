package j2;

import W1.C0324l;
import android.app.Activity;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class p extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ int f4827n = 1;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ Object f4828o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ Object f4829p;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public p(F f, Activity activity) {
        super(f.f4782j, true);
        this.f4829p = f;
        this.f4828o = activity;
    }

    @Override // j2.C
    public final void a() {
        switch (this.f4827n) {
            case 0:
                InterfaceC0675e interfaceC0675e = ((G) this.f4829p).f4790h;
                C0324l.d(interfaceC0675e);
                interfaceC0675e.H0((String) this.f4828o, this.f4774k);
                return;
            default:
                InterfaceC0675e interfaceC0675e2 = ((F) this.f4829p).f4782j.f4790h;
                C0324l.d(interfaceC0675e2);
                interfaceC0675e2.Q3(new c2.b((Activity) this.f4828o), this.f4774k);
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public p(G g4, String str) {
        super(g4, true);
        this.f4829p = g4;
        this.f4828o = str;
    }
}
