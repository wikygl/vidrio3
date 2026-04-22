package K1;

import android.net.Uri;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.FH;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Qh;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.YH;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class K implements TN {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ Qh f1308j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ boolean f1309k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ C0208b f1310l;

    public K(C0208b c0208b, Qh qh, boolean z4) {
        this.f1308j = qh;
        this.f1309k = z4;
        this.f1310l = c0208b;
    }

    public final void g(Object obj) {
        C0208b c0208b = this.f1310l;
        ArrayList arrayList = (ArrayList) obj;
        try {
            this.f1308j.d2(arrayList);
            if (!c0208b.f1354x && !this.f1309k) {
                return;
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Uri uri = (Uri) it.next();
                boolean I4 = C0208b.I4(uri, c0208b.f1335J, c0208b.f1336K);
                YH yh = c0208b.f1353w;
                if (I4) {
                    yh.a(C0208b.J4(uri, c0208b.f1332G, "1").toString(), (FH) null);
                } else {
                    if (((Boolean) A1.r.f168d.f171c.a(Gb.I6)).booleanValue()) {
                        yh.a(uri.toString(), (FH) null);
                    }
                }
            }
        } catch (RemoteException e4) {
            E1.m.e("", e4);
        }
    }

    public final void m(Throwable th) {
        try {
            Qh qh = this.f1308j;
            String message = th.getMessage();
            qh.A("Internal error: " + message);
        } catch (RemoteException e4) {
            E1.m.e("", e4);
        }
    }
}
