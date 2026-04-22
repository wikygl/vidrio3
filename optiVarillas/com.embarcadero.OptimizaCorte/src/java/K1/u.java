package K1;

import android.util.Pair;
import com.google.android.gms.internal.ads.Bs;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Tv;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class u implements Bs {

    /* renamed from: j  reason: collision with root package name */
    public final Tv f1408j;

    /* renamed from: k  reason: collision with root package name */
    public final t f1409k;

    /* renamed from: l  reason: collision with root package name */
    public final String f1410l;

    public u(Tv tv, t tVar, String str) {
        this.f1408j = tv;
        this.f1409k = tVar;
        this.f1410l = str;
    }

    public final void a(o oVar) {
        if (oVar != null) {
            if (((Boolean) A1.r.f168d.f171c.a(Gb.k6)).booleanValue()) {
                t tVar = this.f1409k;
                String str = this.f1410l;
                Tv tv = this.f1408j;
                String str2 = oVar.f1388b;
                synchronized (tVar) {
                    z1.p.f6575A.f6584j.getClass();
                    tVar.f1404e.put(str, new Pair(Long.valueOf(System.currentTimeMillis()), str2));
                    tVar.d();
                    tVar.b(tv);
                }
            }
        }
    }

    public final void A(String str) {
    }
}
