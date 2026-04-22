package p2;

import Q0.v;
import java.util.concurrent.Executor;

/* renamed from: p2.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0764m implements n {

    /* renamed from: j  reason: collision with root package name */
    public final Executor f5568j;

    /* renamed from: k  reason: collision with root package name */
    public final Object f5569k = new Object();

    /* renamed from: l  reason: collision with root package name */
    public final InterfaceC0755d f5570l;

    public C0764m(Executor executor, InterfaceC0755d interfaceC0755d) {
        this.f5568j = executor;
        this.f5570l = interfaceC0755d;
    }

    @Override // p2.n
    public final void a(AbstractC0757f abstractC0757f) {
        if (!abstractC0757f.k() && !abstractC0757f.i()) {
            synchronized (this.f5569k) {
                try {
                    if (this.f5570l == null) {
                        return;
                    }
                    this.f5568j.execute(new v(this, abstractC0757f));
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }
}
