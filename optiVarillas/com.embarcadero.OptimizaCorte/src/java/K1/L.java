package K1;

import android.net.Uri;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.FH;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Qh;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.YH;
import java.util.Iterator;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class L implements TN {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ Qh f1311j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ boolean f1312k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ C0208b f1313l;

    public L(C0208b c0208b, Qh qh, boolean z4) {
        this.f1311j = qh;
        this.f1312k = z4;
        this.f1313l = c0208b;
    }

    public final void g(Object obj) {
        C0208b c0208b;
        List<Uri> list = (List) obj;
        try {
            Iterator it = list.iterator();
            while (true) {
                boolean hasNext = it.hasNext();
                c0208b = this.f1313l;
                if (hasNext) {
                    if (C0208b.I4((Uri) it.next(), c0208b.f1333H, c0208b.f1334I)) {
                        c0208b.f1329D.getAndIncrement();
                        break;
                    }
                } else {
                    break;
                }
            }
            this.f1311j.d2(list);
            if (!c0208b.f1355y && !this.f1312k) {
                return;
            }
            for (Uri uri : list) {
                boolean I4 = C0208b.I4(uri, c0208b.f1333H, c0208b.f1334I);
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
            Qh qh = this.f1311j;
            String message = th.getMessage();
            qh.A("Internal error: " + message);
        } catch (RemoteException e4) {
            E1.m.e("", e4);
        }
    }
}
