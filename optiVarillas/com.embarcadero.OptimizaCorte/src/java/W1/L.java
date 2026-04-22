package W1;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public abstract class L {

    /* renamed from: a  reason: collision with root package name */
    public Object f2648a;

    /* renamed from: b  reason: collision with root package name */
    public boolean f2649b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ AbstractC0314b f2650c;

    public L(AbstractC0314b abstractC0314b) {
        Boolean bool = Boolean.TRUE;
        this.f2650c = abstractC0314b;
        this.f2648a = bool;
        this.f2649b = false;
    }

    public abstract void a();

    public final void b() {
        synchronized (this) {
            this.f2648a = null;
        }
    }

    public final void c() {
        b();
        synchronized (this.f2650c.f2693l) {
            this.f2650c.f2693l.remove(this);
        }
    }
}
