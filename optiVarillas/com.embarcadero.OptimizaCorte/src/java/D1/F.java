package D1;

import com.google.android.gms.internal.ads.C5;
import com.google.android.gms.internal.ads.F5;
import com.google.android.gms.internal.ads.K5;
import com.google.android.gms.internal.ads.Y5;
import com.google.android.gms.internal.ads.zk;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class F extends F5 {

    /* renamed from: v  reason: collision with root package name */
    public final zk f634v;

    /* renamed from: w  reason: collision with root package name */
    public final E1.l f635w;

    public F(String str, zk zkVar) {
        super(0, str, new E(zkVar));
        this.f634v = zkVar;
        E1.l lVar = new E1.l();
        this.f635w = lVar;
        if (E1.l.c()) {
            lVar.d("onNetworkRequest", new E1.g(str, "GET", null, null));
        }
    }

    public final K5 a(C5 c5) {
        return new K5(c5, Y5.b(c5));
    }

    public final void f(Object obj) {
        byte[] bArr;
        C5 c5 = (C5) obj;
        Map map = c5.c;
        E1.l lVar = this.f635w;
        lVar.getClass();
        if (E1.l.c()) {
            int i4 = c5.a;
            lVar.d("onNetworkResponse", new E1.j(i4, map));
            if (i4 < 200 || i4 >= 300) {
                lVar.d("onNetworkRequestError", new E1.i(null));
            }
        }
        if (E1.l.c() && (bArr = c5.b) != null) {
            lVar.d("onNetworkResponseBody", new E1.h(0, bArr));
        }
        this.f634v.b(c5);
    }
}
