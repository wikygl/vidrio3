package p2;

import K1.C0210d;
import W1.C0324l;
import com.google.android.gms.internal.ads.Q8;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class q<TResult> extends AbstractC0757f<TResult> {

    /* renamed from: a  reason: collision with root package name */
    public final Object f5572a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public final Q8 f5573b = new Q8(1);

    /* renamed from: c  reason: collision with root package name */
    public boolean f5574c;

    /* renamed from: d  reason: collision with root package name */
    public volatile boolean f5575d;

    /* renamed from: e  reason: collision with root package name */
    public Object f5576e;
    public Exception f;

    @Override // p2.AbstractC0757f
    public final void a(o oVar, C0761j c0761j) {
        this.f5573b.b(new C0762k(oVar, c0761j));
        p();
    }

    @Override // p2.AbstractC0757f
    public final void b(Executor executor, InterfaceC0754c interfaceC0754c) {
        this.f5573b.b(new C0763l(executor, interfaceC0754c));
        p();
    }

    @Override // p2.AbstractC0757f
    public final q c(Executor executor, InterfaceC0755d interfaceC0755d) {
        this.f5573b.b(new C0764m(executor, interfaceC0755d));
        p();
        return this;
    }

    @Override // p2.AbstractC0757f
    public final q d(o oVar, C0761j c0761j) {
        this.f5573b.b(new C0760i(oVar, c0761j));
        p();
        return this;
    }

    @Override // p2.AbstractC0757f
    public final <TContinuationResult> AbstractC0757f<TContinuationResult> e(Executor executor, InterfaceC0752a<TResult, TContinuationResult> interfaceC0752a) {
        q qVar = new q();
        this.f5573b.b(new C0760i(executor, interfaceC0752a, qVar));
        p();
        return qVar;
    }

    @Override // p2.AbstractC0757f
    public final AbstractC0757f f(C0210d c0210d) {
        p pVar = C0759h.f5553a;
        q qVar = new q();
        this.f5573b.b(new C0761j(pVar, c0210d, qVar));
        p();
        return qVar;
    }

    @Override // p2.AbstractC0757f
    public final Exception g() {
        Exception exc;
        synchronized (this.f5572a) {
            exc = this.f;
        }
        return exc;
    }

    @Override // p2.AbstractC0757f
    public final TResult h() {
        TResult tresult;
        synchronized (this.f5572a) {
            try {
                C0324l.f("Task is not yet complete", this.f5574c);
                if (!this.f5575d) {
                    Exception exc = this.f;
                    if (exc == null) {
                        tresult = (TResult) this.f5576e;
                    } else {
                        throw new RuntimeException(exc);
                    }
                } else {
                    throw new CancellationException("Task is already canceled.");
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return tresult;
    }

    @Override // p2.AbstractC0757f
    public final boolean i() {
        return this.f5575d;
    }

    @Override // p2.AbstractC0757f
    public final boolean j() {
        boolean z4;
        synchronized (this.f5572a) {
            z4 = this.f5574c;
        }
        return z4;
    }

    @Override // p2.AbstractC0757f
    public final boolean k() {
        boolean z4;
        synchronized (this.f5572a) {
            try {
                z4 = false;
                if (this.f5574c && !this.f5575d && this.f == null) {
                    z4 = true;
                }
            } finally {
            }
        }
        return z4;
    }

    public final void l(Exception exc) {
        C0324l.e(exc, "Exception must not be null");
        synchronized (this.f5572a) {
            o();
            this.f5574c = true;
            this.f = exc;
        }
        this.f5573b.d(this);
    }

    public final void m(Object obj) {
        synchronized (this.f5572a) {
            o();
            this.f5574c = true;
            this.f5576e = obj;
        }
        this.f5573b.d(this);
    }

    public final void n() {
        synchronized (this.f5572a) {
            try {
                if (this.f5574c) {
                    return;
                }
                this.f5574c = true;
                this.f5575d = true;
                this.f5573b.d(this);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void o() {
        String str;
        if (this.f5574c) {
            int i4 = C0753b.f5551j;
            if (j()) {
                Exception g4 = g();
                if (g4 == null) {
                    if (!k()) {
                        if (i()) {
                            str = "cancellation";
                        } else {
                            str = "unknown issue";
                        }
                    } else {
                        str = "result ".concat(String.valueOf(h()));
                    }
                } else {
                    str = "failure";
                }
                throw new IllegalStateException("Complete with: ".concat(str), g4);
            }
            throw new IllegalStateException("DuplicateTaskCompletionException can only be created from completed Task.");
        }
    }

    public final void p() {
        synchronized (this.f5572a) {
            try {
                if (!this.f5574c) {
                    return;
                }
                this.f5573b.d(this);
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
