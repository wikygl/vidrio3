package K1;

import android.os.Bundle;
import android.util.Pair;
import android.webkit.CookieManager;
import com.google.android.gms.internal.ads.MY;
import com.google.android.gms.internal.ads.eZ;
import com.google.android.gms.internal.ads.s10;
import com.google.android.gms.internal.ads.v10;
import t1.C0802d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class q implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1393j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f1394k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f1395l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ Object f1396m;

    public /* synthetic */ q(Object obj, Object obj2, Object obj3, int i4) {
        this.f1393j = i4;
        this.f1394k = obj;
        this.f1395l = obj2;
        this.f1396m = obj3;
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean z4;
        switch (this.f1393j) {
            case 0:
                C0207a c0207a = (C0207a) this.f1394k;
                c0207a.getClass();
                CookieManager i4 = z1.p.f6575A.f6580e.i();
                if (i4 != null) {
                    z4 = i4.acceptThirdPartyCookies(c0207a.f1315b);
                } else {
                    z4 = false;
                }
                Bundle bundle = (Bundle) this.f1395l;
                bundle.putBoolean("accept_3p_cookie", z4);
                M1.a.a(c0207a.f1314a, new C0802d(new C0802d.a().a(bundle)), (G3.g) this.f1396m);
                return;
            case 1:
                eZ eZVar = ((MY) this.f1394k).k.h;
                Pair pair = (Pair) this.f1395l;
                eZVar.x(((Integer) pair.first).intValue(), (v10) pair.second, (s10) this.f1396m);
                return;
            default:
                ((o.e) this.f1396m).f5401k.e((String) this.f1394k, (Bundle) this.f1395l);
                return;
        }
    }

    public q(o.e eVar, String str, Bundle bundle) {
        this.f1393j = 2;
        this.f1396m = eVar;
        this.f1394k = str;
        this.f1395l = bundle;
    }
}
