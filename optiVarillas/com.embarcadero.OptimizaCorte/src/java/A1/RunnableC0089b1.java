package A1;

import D1.C0195o;
import android.os.RemoteException;

/* renamed from: A1.b1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class RunnableC0089b1 implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f103j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f104k;

    public /* synthetic */ RunnableC0089b1(int i4, Object obj) {
        this.f103j = i4;
        this.f104k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f103j) {
            case 0:
                InterfaceC0139x interfaceC0139x = ((C0092c1) this.f104k).f107j.f110j;
                if (interfaceC0139x != null) {
                    try {
                        interfaceC0139x.w(1);
                        return;
                    } catch (RemoteException e4) {
                        E1.m.h("Could not notify onAdFailedToLoad event.", e4);
                        return;
                    }
                }
                return;
            case 1:
                C0195o c0195o = (C0195o) this.f104k;
                c0195o.c(c0195o.f745a);
                return;
            default:
                ((V1.u) this.f104k).e();
                return;
        }
    }
}
