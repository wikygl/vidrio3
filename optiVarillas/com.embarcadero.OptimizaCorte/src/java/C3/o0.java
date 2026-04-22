package C3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class o0<T> extends F3.u<T> {
    private volatile boolean threadLocalIsSet;

    public final void T() {
        if (this.threadLocalIsSet) {
            throw null;
        }
        throw null;
    }

    public final void U() {
        this.threadLocalIsSet = true;
        throw null;
    }

    @Override // F3.u, C3.d0
    public final void m(Object obj) {
        if (!this.threadLocalIsSet) {
            if (obj instanceof C0162l) {
                B2.a.a(((C0162l) obj).f490a);
            }
            throw null;
        }
        throw null;
    }
}
