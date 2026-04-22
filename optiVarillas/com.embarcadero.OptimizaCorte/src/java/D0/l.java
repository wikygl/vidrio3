package D0;

import a3.InterfaceFutureC0346a;
import android.view.View;
import androidx.appcompat.app.AlertController;
import com.google.android.gms.internal.ads.Mk;
import com.google.android.gms.internal.ads.Rk;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class l implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f593j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f594k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f595l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ Object f596m;

    public /* synthetic */ l(Object obj, Object obj2, Object obj3, int i4) {
        this.f593j = i4;
        this.f596m = obj;
        this.f594k = obj2;
        this.f595l = obj3;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f593j) {
            case 0:
                N0.c cVar = (N0.c) this.f595l;
                n nVar = (n) this.f596m;
                try {
                    ((InterfaceFutureC0346a) this.f594k).get();
                    C0.i c4 = C0.i.c();
                    String str = n.f600C;
                    String str2 = nVar.f607n.f1452c;
                    c4.a(str, "Starting work for " + str2, new Throwable[0]);
                    nVar.f601A = nVar.f608o.startWork();
                    cVar.l(nVar.f601A);
                    return;
                } catch (Throwable th) {
                    cVar.k(th);
                    return;
                }
            case 1:
                Rk rk = ((Mk) this.f596m).y;
                if (rk != null) {
                    rk.c("error", new String[]{"what", (String) this.f594k, "extra", (String) this.f595l});
                    return;
                }
                return;
            default:
                AlertController.b(((AlertController) this.f596m).g, (View) this.f594k, (View) this.f595l);
                return;
        }
    }
}
