package p2;

import K1.C0210d;
import java.util.concurrent.Executor;

/* renamed from: p2.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0757f<TResult> {
    public void a(o oVar, C0761j c0761j) {
        throw new UnsupportedOperationException("addOnCanceledListener is not implemented");
    }

    public void b(Executor executor, InterfaceC0754c interfaceC0754c) {
        throw new UnsupportedOperationException("addOnCompleteListener is not implemented");
    }

    public abstract q c(Executor executor, InterfaceC0755d interfaceC0755d);

    public abstract q d(o oVar, C0761j c0761j);

    public <TContinuationResult> AbstractC0757f<TContinuationResult> e(Executor executor, InterfaceC0752a<TResult, TContinuationResult> interfaceC0752a) {
        throw new UnsupportedOperationException("continueWith is not implemented");
    }

    public AbstractC0757f f(C0210d c0210d) {
        throw new UnsupportedOperationException("continueWithTask is not implemented");
    }

    public abstract Exception g();

    public abstract TResult h();

    public abstract boolean i();

    public abstract boolean j();

    public abstract boolean k();
}
