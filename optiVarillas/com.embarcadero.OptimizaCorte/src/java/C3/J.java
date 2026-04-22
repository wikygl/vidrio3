package C3;

import C3.Y;
import java.util.concurrent.CancellationException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class J<T> extends G3.h {

    /* renamed from: l  reason: collision with root package name */
    public int f430l;

    public J(int i4) {
        this.f430l = i4;
    }

    public abstract n3.d<T> b();

    public Throwable c(Object obj) {
        C0162l c0162l;
        if (obj instanceof C0162l) {
            c0162l = (C0162l) obj;
        } else {
            c0162l = null;
        }
        if (c0162l == null) {
            return null;
        }
        return c0162l.f490a;
    }

    public final void e(Throwable th, Throwable th2) {
        if (th == null && th2 == null) {
            return;
        }
        if (th != null && th2 != null) {
            A3.d.a(th, th2);
        }
        if (th == null) {
            th = th2;
        }
        v3.h.b(th);
        C0173x.a(b().getContext(), new Error("Fatal exception in coroutines machinery for " + this + ". Please read KDoc to 'handleFatalException' method and report this incident to maintainers", th));
    }

    public abstract Object f();

    @Override // java.lang.Runnable
    public final void run() {
        o0 o0Var;
        Y y4;
        Object obj = l3.g.f5271a;
        G3.i iVar = this.f996k;
        try {
            n3.d<T> b4 = b();
            v3.h.c(b4, "null cannot be cast to non-null type kotlinx.coroutines.internal.DispatchedContinuation<T of kotlinx.coroutines.DispatchedTask>");
            F3.h hVar = (F3.h) b4;
            n3.d<T> dVar = hVar.f917n;
            Object obj2 = hVar.f919p;
            n3.f context = dVar.getContext();
            Object b5 = F3.x.b(context, obj2);
            if (b5 != F3.x.f947a) {
                o0Var = C0169t.a(dVar, context);
            } else {
                o0Var = null;
            }
            n3.f context2 = dVar.getContext();
            Object f = f();
            Throwable c4 = c(f);
            if (c4 == null && B2.a.g(this.f430l)) {
                y4 = (Y) context2.E(Y.b.f450j);
            } else {
                y4 = null;
            }
            if (y4 != null && !y4.a()) {
                CancellationException t3 = y4.t();
                a(f, t3);
                dVar.j(B2.a.a(t3));
            } else if (c4 != null) {
                dVar.j(B2.a.a(c4));
            } else {
                dVar.j(d(f));
            }
            if (o0Var == null) {
                F3.x.a(context, b5);
                try {
                    iVar.getClass();
                } catch (Throwable th) {
                    obj = B2.a.a(th);
                }
                e(null, l3.c.a(obj));
                return;
            }
            o0Var.T();
            throw null;
        } catch (Throwable th2) {
            try {
                iVar.getClass();
            } catch (Throwable th3) {
                obj = B2.a.a(th3);
            }
            e(th2, l3.c.a(obj));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> T d(Object obj) {
        return obj;
    }

    public void a(Object obj, CancellationException cancellationException) {
    }
}
