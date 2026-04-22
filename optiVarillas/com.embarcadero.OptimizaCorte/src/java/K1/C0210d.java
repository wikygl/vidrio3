package K1;

import com.google.android.gms.internal.ads.Cs;
import com.google.android.gms.internal.ads.HP;
import com.google.android.gms.internal.ads.TN;
import p2.AbstractC0757f;
import p2.InterfaceC0752a;

/* renamed from: K1.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0210d implements TN, InterfaceC0752a {

    /* renamed from: j  reason: collision with root package name */
    public final Object f1367j;

    public /* synthetic */ C0210d(Object obj) {
        this.f1367j = obj;
    }

    @Override // p2.InterfaceC0752a
    public Object f(AbstractC0757f abstractC0757f) {
        if (!abstractC0757f.k() && !abstractC0757f.i()) {
            Exception g4 = abstractC0757f.g();
            if (g4 instanceof U1.b) {
                int i4 = ((U1.b) g4).f2377j.j;
                if (i4 != 43001 && i4 != 43002 && i4 != 43003 && i4 != 17) {
                    if (i4 == 43000) {
                        Exception exc = new Exception("Failed to get app set ID due to an internal error. Please try again later.");
                        p2.q qVar = new p2.q();
                        qVar.l(exc);
                        abstractC0757f = qVar;
                    } else if (i4 == 15) {
                        Exception exc2 = new Exception("The operation to get app set ID timed out. Please try again later.");
                        p2.q qVar2 = new p2.q();
                        qVar2.l(exc2);
                        return qVar2;
                    }
                } else {
                    abstractC0757f = ((f2.k) this.f1367j).f3409b.a();
                }
            }
        }
        return abstractC0757f;
    }

    public void g(Object obj) {
        Cs cs = (Cs) this.f1367j;
        o oVar = (o) obj;
        synchronized (cs) {
            cs.V0(new E1.h(6, oVar));
        }
    }

    public void m(Throwable th) {
        Cs cs = (Cs) this.f1367j;
        String message = th.getMessage();
        synchronized (cs) {
            cs.V0(new HP(message, 2));
        }
    }
}
