package C3;

import F3.m;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class e0 extends m.a {

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ d0 f472d;

    /* renamed from: e  reason: collision with root package name */
    public final /* synthetic */ Object f473e;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public e0(F3.m mVar, d0 d0Var, U u4) {
        super(mVar);
        this.f472d = d0Var;
        this.f473e = u4;
    }

    @Override // F3.b
    public final C1.A c(Object obj) {
        F3.m mVar = (F3.m) obj;
        if (this.f472d.G() == this.f473e) {
            return null;
        }
        return F3.l.f930a;
    }
}
