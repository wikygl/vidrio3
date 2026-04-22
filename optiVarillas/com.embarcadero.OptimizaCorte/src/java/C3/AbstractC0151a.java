package C3;

import C3.Y;

/* renamed from: C3.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class AbstractC0151a<T> extends d0 implements n3.d<T>, InterfaceC0174y {

    /* renamed from: l  reason: collision with root package name */
    public final n3.f f452l;

    public AbstractC0151a(n3.f fVar, boolean z4) {
        super(z4);
        I((Y) fVar.E(Y.b.f450j));
        this.f452l = fVar.k(this);
    }

    @Override // C3.d0
    public final void H(C0165o c0165o) {
        C0173x.a(this.f452l, c0165o);
    }

    @Override // C3.d0
    public final void O(Object obj) {
        if (obj instanceof C0162l) {
            C0162l c0162l = (C0162l) obj;
            Throwable th = c0162l.f490a;
            c0162l.getClass();
            C0162l.f489b.get(c0162l);
        }
    }

    @Override // n3.d
    public final n3.f getContext() {
        return this.f452l;
    }

    @Override // n3.d
    public final void j(Object obj) {
        Object S3;
        C0162l c0162l;
        Throwable a4 = l3.c.a(obj);
        if (a4 != null) {
            obj = new C0162l(a4, false);
        }
        do {
            S3 = S(G(), obj);
            if (S3 == f0.f475a) {
                String str = "Job " + this + " is already complete or completing, but is being completed with " + obj;
                Throwable th = null;
                if (obj instanceof C0162l) {
                    c0162l = (C0162l) obj;
                } else {
                    c0162l = null;
                }
                if (c0162l != null) {
                    th = c0162l.f490a;
                }
                throw new IllegalStateException(str, th);
            }
        } while (S3 == f0.f477c);
        if (S3 == f0.f476b) {
            return;
        }
        m(S3);
    }

    @Override // C3.d0
    public final String x() {
        return getClass().getSimpleName().concat(" was cancelled");
    }
}
