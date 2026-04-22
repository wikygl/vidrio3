package F1;

import K1.t;
import android.content.Context;
import com.google.android.gms.internal.ads.Tv;
import com.google.android.gms.internal.ads.Xh;
import com.google.android.gms.internal.ads.lf;
import java.util.ArrayDeque;
import t1.C0802d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class c implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f901j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f902k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f903l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ Object f904m;

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ Object f905n;

    public /* synthetic */ c(Object obj, Object obj2, Object obj3, Object obj4, int i4) {
        this.f901j = i4;
        this.f902k = obj;
        this.f903l = obj2;
        this.f904m = obj3;
        this.f905n = obj4;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f901j) {
            case 0:
                Context context = (Context) this.f902k;
                String str = (String) this.f903l;
                C0802d c0802d = (C0802d) this.f904m;
                try {
                    new lf(context, str).f(c0802d.f5787a, (b) this.f905n);
                    return;
                } catch (IllegalStateException e4) {
                    Xh.b(context).a("InterstitialAd.load", e4);
                    return;
                }
            default:
                t tVar = (t) this.f902k;
                Tv tv = (Tv) this.f903l;
                tVar.c(tv, (ArrayDeque) this.f904m, "to");
                tVar.c(tv, (ArrayDeque) this.f905n, "of");
                return;
        }
    }
}
