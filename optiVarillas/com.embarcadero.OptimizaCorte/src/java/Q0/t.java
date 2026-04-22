package Q0;

import android.view.View;
import com.google.android.gms.internal.ads.FH;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Lb;
import com.google.android.gms.internal.ads.Rk;
import com.google.android.gms.internal.ads.YH;
import com.google.android.gms.internal.ads.aI;
import com.google.android.gms.internal.ads.jl;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class t implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2004j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2005k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f2006l;

    public /* synthetic */ t(Object obj, int i4, Object obj2) {
        this.f2004j = i4;
        this.f2005k = obj;
        this.f2006l = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2004j) {
            case 0:
                a aVar = (a) this.f2005k;
                com.android.billingclient.api.a aVar2 = (com.android.billingclient.api.a) this.f2006l;
                if (aVar.f1927n.f2000b != null) {
                    ((Y0.a) aVar.f1927n.f2000b).f(aVar2, null);
                    return;
                } else {
                    com.google.android.gms.internal.play_billing.u.e("BillingClient", "No valid listener is set in BroadcastManager");
                    return;
                }
            case 1:
                Rk rk = ((jl) this.f2005k).o;
                if (rk != null) {
                    rk.c("exception", new String[]{"what", "ExoPlayerAdapter exception", "extra", (String) this.f2006l});
                    return;
                }
                return;
            case 2:
                ((YH) this.f2005k).a((String) this.f2006l, (FH) null);
                return;
            default:
                if (((Boolean) A1.r.f168d.f171c.a(Gb.v4)).booleanValue() && Lb.x.a) {
                    ((aI) this.f2005k).c((View) this.f2006l);
                    return;
                }
                return;
        }
    }
}
