package p2;

import K1.C0210d;
import java.util.concurrent.Executor;

/* renamed from: p2.j  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0761j<TResult, TContinuationResult> implements InterfaceC0755d, n {

    /* renamed from: j  reason: collision with root package name */
    public final Executor f5559j;

    /* renamed from: k  reason: collision with root package name */
    public final InterfaceC0752a f5560k;

    /* renamed from: l  reason: collision with root package name */
    public final q f5561l;

    public C0761j(p pVar, C0210d c0210d, q qVar) {
        this.f5559j = pVar;
        this.f5560k = c0210d;
        this.f5561l = qVar;
    }

    @Override // p2.n
    public final void a(AbstractC0757f abstractC0757f) {
        this.f5559j.execute(new B.h(this, abstractC0757f));
    }

    @Override // p2.InterfaceC0755d
    public final void b(Exception exc) {
        this.f5561l.l(exc);
    }
}
