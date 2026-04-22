package D1;

import A1.O0;
import android.os.RemoteException;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.google.android.gms.internal.ads.Xh;
import i2.C0475v;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import t1.AbstractC0806h;

/* renamed from: D1.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class RunnableC0186f implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f680j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f681k;

    public /* synthetic */ RunnableC0186f(int i4, Object obj) {
        this.f680j = i4;
        this.f681k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f680j) {
            case 0:
                C0195o c0195o = (C0195o) this.f681k;
                c0195o.f750g = 4;
                c0195o.b();
                return;
            case 1:
                Q0.l lVar = (Q0.l) this.f681k;
                lVar.f1986c.f1924k = 0;
                lVar.f1986c.f1930q = null;
                com.android.billingclient.api.a aVar = com.android.billingclient.api.b.k;
                lVar.f1986c.M(Q0.m.a(24, 6, aVar));
                lVar.a(aVar);
                return;
            case 2:
                ((V1.x) ((V1.G) this.f681k).f2549q).b(new T1.b(4));
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                FragmentStateAdapter fragmentStateAdapter = (FragmentStateAdapter) this.f681k;
                fragmentStateAdapter.i = false;
                fragmentStateAdapter.o();
                return;
            case 4:
                C0475v c0475v = (C0475v) this.f681k;
                c0475v.getClass();
                c0475v.f3807d.execute(new C1.j(1, c0475v));
                return;
            default:
                AbstractC0806h abstractC0806h = (AbstractC0806h) this.f681k;
                try {
                    O0 o02 = abstractC0806h.f5800j;
                    o02.getClass();
                    try {
                        A1.L l2 = o02.f74i;
                        if (l2 != null) {
                            l2.Y();
                        }
                    } catch (RemoteException e4) {
                        E1.m.i("#007 Could not call remote method.", e4);
                    }
                    return;
                } catch (IllegalStateException e5) {
                    Xh.b(abstractC0806h.getContext()).a("BaseAdView.pause", e5);
                    return;
                }
        }
    }
}
