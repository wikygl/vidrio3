package K0;

import a3.InterfaceFutureC0346a;
import android.app.Notification;
import android.graphics.Typeface;
import android.widget.TextView;
import androidx.work.impl.foreground.SystemForegroundService;
import com.google.android.gms.internal.ads.DN;
import com.google.android.gms.internal.ads.VN;
import com.google.android.gms.internal.ads.WL;
import java.util.concurrent.ExecutionException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class d implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1274j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ int f1275k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f1276l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ Object f1277m;

    public /* synthetic */ d(int i4, int i5, Object obj, Object obj2) {
        this.f1274j = i5;
        this.f1276l = obj;
        this.f1277m = obj2;
        this.f1275k = i4;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f1274j) {
            case 0:
                ((SystemForegroundService) this.f1277m).n.notify(this.f1275k, (Notification) this.f1276l);
                return;
            case 1:
                InterfaceFutureC0346a interfaceFutureC0346a = (InterfaceFutureC0346a) this.f1277m;
                int i4 = this.f1275k;
                DN dn = (DN) this.f1276l;
                dn.getClass();
                try {
                    if (interfaceFutureC0346a.isCancelled()) {
                        dn.u = null;
                        dn.cancel(false);
                    } else {
                        try {
                            dn.u(i4, VN.C(interfaceFutureC0346a));
                        } catch (ExecutionException e4) {
                            dn.s(e4.getCause());
                        } catch (Throwable th) {
                            dn.s(th);
                        }
                    }
                    return;
                } finally {
                    dn.r((WL) null);
                }
            default:
                int i5 = this.f1275k;
                ((TextView) this.f1276l).setTypeface((Typeface) this.f1277m, i5);
                return;
        }
    }

    public d(SystemForegroundService systemForegroundService, int i4, Notification notification) {
        this.f1274j = 0;
        this.f1277m = systemForegroundService;
        this.f1275k = i4;
        this.f1276l = notification;
    }
}
