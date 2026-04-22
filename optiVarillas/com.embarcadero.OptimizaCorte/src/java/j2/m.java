package j2;

import W1.C0324l;
import android.app.Activity;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class m extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ Activity f4815n;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ String f4816o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ String f4817p;

    /* renamed from: q  reason: collision with root package name */
    public final /* synthetic */ G f4818q;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public m(G g4, Activity activity, String str, String str2) {
        super(g4, true);
        this.f4818q = g4;
        this.f4815n = activity;
        this.f4816o = str;
        this.f4817p = str2;
    }

    @Override // j2.C
    public final void a() {
        InterfaceC0675e interfaceC0675e = this.f4818q.f4790h;
        C0324l.d(interfaceC0675e);
        interfaceC0675e.j2(new c2.b(this.f4815n), this.f4816o, this.f4817p, this.f4773j);
    }
}
