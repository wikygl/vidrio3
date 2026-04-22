package A1;

import android.os.RemoteException;
import t1.AbstractC0800b;
import t1.C0807i;
import t1.C0813o;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class M0 extends AbstractC0800b {

    /* renamed from: j  reason: collision with root package name */
    public final Object f59j = new Object();

    /* renamed from: k  reason: collision with root package name */
    public AbstractC0800b f60k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ O0 f61l;

    public M0(O0 o02) {
        this.f61l = o02;
    }

    @Override // t1.AbstractC0800b
    public final void a() {
        synchronized (this.f59j) {
            try {
                AbstractC0800b abstractC0800b = this.f60k;
                if (abstractC0800b != null) {
                    abstractC0800b.a();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // t1.AbstractC0800b
    public final void b(C0807i c0807i) {
        O0 o02 = this.f61l;
        C0813o c0813o = o02.f69c;
        L l2 = o02.f74i;
        D0 d02 = null;
        if (l2 != null) {
            try {
                d02 = l2.m();
            } catch (RemoteException e4) {
                E1.m.i("#007 Could not call remote method.", e4);
            }
        }
        c0813o.b(d02);
        synchronized (this.f59j) {
            try {
                AbstractC0800b abstractC0800b = this.f60k;
                if (abstractC0800b != null) {
                    abstractC0800b.b(c0807i);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // t1.AbstractC0800b
    public final void c() {
        synchronized (this.f59j) {
            try {
                AbstractC0800b abstractC0800b = this.f60k;
                if (abstractC0800b != null) {
                    abstractC0800b.c();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // t1.AbstractC0800b
    public final void d() {
        O0 o02 = this.f61l;
        C0813o c0813o = o02.f69c;
        L l2 = o02.f74i;
        D0 d02 = null;
        if (l2 != null) {
            try {
                d02 = l2.m();
            } catch (RemoteException e4) {
                E1.m.i("#007 Could not call remote method.", e4);
            }
        }
        c0813o.b(d02);
        synchronized (this.f59j) {
            try {
                AbstractC0800b abstractC0800b = this.f60k;
                if (abstractC0800b != null) {
                    abstractC0800b.d();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // t1.AbstractC0800b
    public final void e() {
        synchronized (this.f59j) {
            try {
                AbstractC0800b abstractC0800b = this.f60k;
                if (abstractC0800b != null) {
                    abstractC0800b.e();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // t1.AbstractC0800b
    public final void m() {
        synchronized (this.f59j) {
            try {
                AbstractC0800b abstractC0800b = this.f60k;
                if (abstractC0800b != null) {
                    abstractC0800b.m();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
