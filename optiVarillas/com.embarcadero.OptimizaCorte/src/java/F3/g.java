package F3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class g extends RuntimeException {

    /* renamed from: j  reason: collision with root package name */
    public final transient n3.f f914j;

    public g(n3.f fVar) {
        this.f914j = fVar;
    }

    @Override // java.lang.Throwable
    public final Throwable fillInStackTrace() {
        setStackTrace(new StackTraceElement[0]);
        return this;
    }

    @Override // java.lang.Throwable
    public final String getLocalizedMessage() {
        return this.f914j.toString();
    }
}
