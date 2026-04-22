package i2;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class c0 {

    /* renamed from: a  reason: collision with root package name */
    public final C0463i f3713a;

    /* renamed from: b  reason: collision with root package name */
    public final f0 f3714b;

    /* renamed from: c  reason: collision with root package name */
    public final C0469o f3715c;

    /* renamed from: d  reason: collision with root package name */
    public final Object f3716d = new Object();

    /* renamed from: e  reason: collision with root package name */
    public boolean f3717e = false;

    public c0(C0463i c0463i, f0 f0Var, C0469o c0469o) {
        this.f3713a = c0463i;
        this.f3714b = f0Var;
        this.f3715c = c0469o;
    }

    public final int a() {
        boolean z4;
        synchronized (this.f3716d) {
            z4 = this.f3717e;
        }
        if (!z4) {
            return 0;
        }
        return this.f3713a.f3760b.getInt("consent_status", 0);
    }
}
