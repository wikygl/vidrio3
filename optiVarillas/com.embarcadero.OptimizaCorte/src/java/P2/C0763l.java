package p2;

import A1.Q0;
import java.util.concurrent.Executor;

/* renamed from: p2.l  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0763l implements n {

    /* renamed from: j  reason: collision with root package name */
    public final Executor f5565j;

    /* renamed from: k  reason: collision with root package name */
    public final Object f5566k = new Object();

    /* renamed from: l  reason: collision with root package name */
    public final InterfaceC0754c f5567l;

    public C0763l(Executor executor, InterfaceC0754c interfaceC0754c) {
        this.f5565j = executor;
        this.f5567l = interfaceC0754c;
    }

    @Override // p2.n
    public final void a(AbstractC0757f abstractC0757f) {
        synchronized (this.f5566k) {
            try {
                if (this.f5567l == null) {
                    return;
                }
                this.f5565j.execute(new Q0(this, abstractC0757f, 2, false));
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
