package t1;

import A1.L;
import A1.O0;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.Xh;

/* renamed from: t1.q  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class RunnableC0815q implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ AbstractC0806h f5814j;

    @Override // java.lang.Runnable
    public final void run() {
        AbstractC0806h abstractC0806h = this.f5814j;
        try {
            O0 o02 = abstractC0806h.f5800j;
            o02.getClass();
            try {
                L l2 = o02.f74i;
                if (l2 != null) {
                    l2.M();
                }
            } catch (RemoteException e4) {
                E1.m.i("#007 Could not call remote method.", e4);
            }
        } catch (IllegalStateException e5) {
            Xh.b(abstractC0806h.getContext()).a("BaseAdView.resume", e5);
        }
    }
}
