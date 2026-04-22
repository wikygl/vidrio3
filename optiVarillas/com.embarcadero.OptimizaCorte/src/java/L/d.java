package L;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class d<T> extends c<T> {

    /* renamed from: c  reason: collision with root package name */
    public final Object f1426c;

    public d(int i4) {
        super(i4);
        this.f1426c = new Object();
    }

    @Override // L.c
    public final T a() {
        T t3;
        synchronized (this.f1426c) {
            t3 = (T) super.a();
        }
        return t3;
    }

    @Override // L.c
    public final boolean b(T t3) {
        boolean b4;
        synchronized (this.f1426c) {
            b4 = super.b(t3);
        }
        return b4;
    }
}
