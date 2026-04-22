package V1;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import java.util.Map;
import p2.C0758g;
import p2.C0759h;
import p2.C0763l;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class J extends A {

    /* renamed from: b  reason: collision with root package name */
    public final AbstractC0305k f2552b;

    /* renamed from: c  reason: collision with root package name */
    public final C0758g f2553c;

    /* renamed from: d  reason: collision with root package name */
    public final C3.C f2554d;

    public J(int i4, H h4, C0758g c0758g, C3.C c4) {
        super(i4);
        this.f2553c = c0758g;
        this.f2552b = h4;
        this.f2554d = c4;
        if (i4 == 2 && h4.f2590b) {
            throw new IllegalArgumentException("Best-effort write calls cannot pass methods that should auto-resolve missing features.");
        }
    }

    @Override // V1.L
    public final void a(Status status) {
        U1.b bVar;
        this.f2554d.getClass();
        if (status.l != null) {
            bVar = new U1.b(status);
        } else {
            bVar = new U1.b(status);
        }
        this.f2553c.a(bVar);
    }

    @Override // V1.L
    public final void b(RuntimeException runtimeException) {
        this.f2553c.a(runtimeException);
    }

    @Override // V1.L
    public final void c(u uVar) {
        C0758g c0758g = this.f2553c;
        try {
            AbstractC0305k abstractC0305k = this.f2552b;
            ((H) abstractC0305k).f2550d.f2592a.d(uVar.f2605k, c0758g);
        } catch (DeadObjectException e4) {
            throw e4;
        } catch (RemoteException e5) {
            a(L.e(e5));
        } catch (RuntimeException e6) {
            c0758g.a(e6);
        }
    }

    @Override // V1.L
    public final void d(C0307m c0307m, boolean z4) {
        Boolean valueOf = Boolean.valueOf(z4);
        Map map = c0307m.f2597b;
        C0758g c0758g = this.f2553c;
        map.put(c0758g, valueOf);
        p2.q qVar = c0758g.f5552a;
        C0306l c0306l = new C0306l(c0307m, 0, c0758g);
        qVar.getClass();
        qVar.f5573b.b(new C0763l(C0759h.f5553a, c0306l));
        qVar.p();
    }

    @Override // V1.A
    public final boolean f(u uVar) {
        return this.f2552b.f2590b;
    }

    @Override // V1.A
    public final T1.d[] g(u uVar) {
        return this.f2552b.f2589a;
    }
}
