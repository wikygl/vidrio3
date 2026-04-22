package l;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class U {

    /* renamed from: a  reason: collision with root package name */
    public int f5066a;

    /* renamed from: b  reason: collision with root package name */
    public int f5067b;

    /* renamed from: c  reason: collision with root package name */
    public int f5068c;

    /* renamed from: d  reason: collision with root package name */
    public int f5069d;

    /* renamed from: e  reason: collision with root package name */
    public int f5070e;
    public int f;

    /* renamed from: g  reason: collision with root package name */
    public boolean f5071g;

    /* renamed from: h  reason: collision with root package name */
    public boolean f5072h;

    public final void a(int i4, int i5) {
        this.f5068c = i4;
        this.f5069d = i5;
        this.f5072h = true;
        if (this.f5071g) {
            if (i5 != Integer.MIN_VALUE) {
                this.f5066a = i5;
            }
            if (i4 != Integer.MIN_VALUE) {
                this.f5067b = i4;
                return;
            }
            return;
        }
        if (i4 != Integer.MIN_VALUE) {
            this.f5066a = i4;
        }
        if (i5 != Integer.MIN_VALUE) {
            this.f5067b = i5;
        }
    }
}
