package j2;

import W1.C0324l;
import android.app.Activity;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class E extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ Activity f4780n;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ F f4781o;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public E(F f, Activity activity) {
        super(f.f4782j, true);
        this.f4781o = f;
        this.f4780n = activity;
    }

    @Override // j2.C
    public final void a() {
        InterfaceC0675e interfaceC0675e = this.f4781o.f4782j.f4790h;
        C0324l.d(interfaceC0675e);
        interfaceC0675e.c3(new c2.b(this.f4780n), this.f4774k);
    }
}
