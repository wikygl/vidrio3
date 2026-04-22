package j2;

import W1.C0324l;
import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class k extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ String f4807n;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ String f4808o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ Bundle f4809p;

    /* renamed from: q  reason: collision with root package name */
    public final /* synthetic */ G f4810q;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public k(G g4, String str, String str2, Bundle bundle) {
        super(g4, true);
        this.f4810q = g4;
        this.f4807n = str;
        this.f4808o = str2;
        this.f4809p = bundle;
    }

    @Override // j2.C
    public final void a() {
        InterfaceC0675e interfaceC0675e = this.f4810q.f4790h;
        C0324l.d(interfaceC0675e);
        interfaceC0675e.x1(this.f4807n, this.f4808o, this.f4809p);
    }
}
