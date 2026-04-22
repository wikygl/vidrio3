package j$.util.stream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class J3 implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Runnable f4312a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Runnable f4313b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public J3(Runnable runnable, Runnable runnable2) {
        this.f4312a = runnable;
        this.f4313b = runnable2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Runnable runnable = this.f4313b;
        try {
            this.f4312a.run();
            runnable.run();
        } catch (Throwable th) {
            try {
                runnable.run();
            } catch (Throwable th2) {
                try {
                    th.addSuppressed(th2);
                } catch (Throwable unused) {
                }
            }
            throw th;
        }
    }
}
