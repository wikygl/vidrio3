package A1;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class v1 extends AbstractC0143z {

    /* renamed from: j  reason: collision with root package name */
    public final G3.g f178j;

    /* renamed from: k  reason: collision with root package name */
    public final Object f179k;

    public v1(G3.g gVar, Object obj) {
        super("com.google.android.gms.ads.internal.client.IAdLoadCallback");
        this.f178j = gVar;
        this.f179k = obj;
    }

    @Override // A1.A
    public final void a4(N0 n02) {
        G3.g gVar = this.f178j;
        if (gVar != null) {
            gVar.v(n02.i());
        }
    }

    @Override // A1.A
    public final void r() {
        Object obj;
        G3.g gVar = this.f178j;
        if (gVar != null && (obj = this.f179k) != null) {
            gVar.x(obj);
        }
    }
}
