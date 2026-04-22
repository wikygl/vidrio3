package p2;

import A1.RunnableC0098e1;
import java.util.concurrent.Executor;

/* renamed from: p2.k  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0762k implements n {

    /* renamed from: j  reason: collision with root package name */
    public final Executor f5562j;

    /* renamed from: k  reason: collision with root package name */
    public final Object f5563k = new Object();

    /* renamed from: l  reason: collision with root package name */
    public final C0761j f5564l;

    public C0762k(o oVar, C0761j c0761j) {
        this.f5562j = oVar;
        this.f5564l = c0761j;
    }

    @Override // p2.n
    public final void a(AbstractC0757f abstractC0757f) {
        if (abstractC0757f.i()) {
            synchronized (this.f5563k) {
                try {
                    if (this.f5564l == null) {
                        return;
                    }
                    this.f5562j.execute(new RunnableC0098e1(14, this));
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }
}
