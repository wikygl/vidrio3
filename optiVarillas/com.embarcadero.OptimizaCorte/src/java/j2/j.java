package j2;

import W1.C0324l;
import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class j extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ int f4804n;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ G f4805o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ Object f4806p;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ j(G g4, Object obj, int i4) {
        super(g4, true);
        this.f4804n = i4;
        this.f4805o = g4;
        this.f4806p = obj;
    }

    @Override // j2.C
    public final void a() {
        switch (this.f4804n) {
            case 0:
                InterfaceC0675e interfaceC0675e = this.f4805o.f4790h;
                C0324l.d(interfaceC0675e);
                interfaceC0675e.A0((Bundle) this.f4806p, this.f4773j);
                return;
            default:
                InterfaceC0675e interfaceC0675e2 = this.f4805o.f4790h;
                C0324l.d(interfaceC0675e2);
                interfaceC0675e2.r3((BinderC0672b) this.f4806p);
                return;
        }
    }

    @Override // j2.C
    public void b() {
        switch (this.f4804n) {
            case 1:
                ((BinderC0672b) this.f4806p).Z(null);
                return;
            default:
                return;
        }
    }
}
