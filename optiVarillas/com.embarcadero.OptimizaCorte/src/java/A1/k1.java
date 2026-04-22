package A1;

import android.os.RemoteException;
import com.google.android.gms.internal.ads.Ao;
import com.google.android.gms.internal.ads.Bq;
import com.google.android.gms.internal.ads.HZ;
import com.google.android.gms.internal.ads.MG;
import com.google.android.gms.internal.ads.Mk;
import com.google.android.gms.internal.ads.N10;
import com.google.android.gms.internal.ads.N7;
import com.google.android.gms.internal.ads.Rk;
import com.google.android.gms.internal.ads.W10;
import com.google.android.gms.internal.ads.WF;
import com.google.android.gms.internal.ads.YB;
import com.google.android.gms.internal.ads.b30;
import com.google.android.gms.internal.ads.c10;
import com.google.android.gms.internal.ads.cL;
import com.google.android.gms.internal.ads.ey;
import com.google.android.gms.internal.ads.g00;
import com.google.android.gms.internal.ads.i00;
import com.google.android.gms.internal.ads.jl;
import com.google.android.gms.internal.ads.lZ;
import com.google.android.gms.internal.ads.lj;
import com.google.android.gms.internal.ads.m7;
import com.google.android.gms.internal.ads.mQ;
import com.google.android.gms.internal.ads.yF;
import com.google.android.gms.internal.ads.zx;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class k1 implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f141j;

    /* renamed from: k  reason: collision with root package name */
    public final Object f142k;

    public /* synthetic */ k1(int i4, Object obj) {
        this.f141j = i4;
        this.f142k = obj;
    }

    private final void a() {
        Bq bq = (Bq) this.f142k;
        synchronized (bq) {
            try {
                if (!bq.n.isDone()) {
                    bq.n.f(Boolean.TRUE);
                }
            } finally {
            }
        }
    }

    @Override // java.lang.Runnable
    public final void run() {
        W10[] w10Arr;
        switch (this.f141j) {
            case 0:
                lj ljVar = (lj) this.f142k;
                if (ljVar != null) {
                    try {
                        ljVar.w(1);
                        return;
                    } catch (RemoteException e4) {
                        E1.m.i("#007 Could not call remote method.", e4);
                        return;
                    }
                }
                return;
            case 1:
                synchronized (((m7) this.f142k).x) {
                    if (!((m7) this.f142k).y) {
                        ((m7) this.f142k).y = true;
                        try {
                            m7.k((m7) this.f142k);
                        } catch (Exception e5) {
                            ((m7) this.f142k).o.b(2023, -1L, e5);
                        }
                        synchronized (((m7) this.f142k).x) {
                            ((m7) this.f142k).y = false;
                        }
                        return;
                    }
                    return;
                }
            case 2:
                Rk rk = ((Mk) this.f142k).y;
                if (rk != null) {
                    rk.d();
                    return;
                }
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                Rk rk2 = ((jl) this.f142k).o;
                if (rk2 != null) {
                    Rk rk3 = rk2;
                    rk3.l.setVisibility(4);
                    D1.t0.f774l.post(new N7(3, rk3));
                    return;
                }
                return;
            case 4:
                ((Ao) this.f142k).j.d.b();
                return;
            case 5:
                a();
                return;
            case 6:
                ((zx) this.f142k).a();
                return;
            case 7:
                ((ey) this.f142k).b();
                return;
            case 8:
                ((YB) this.f142k).d.c.i0(MG.d(4, (String) null, (N0) null));
                return;
            case 9:
                yF yFVar = (yF) this.f142k;
                yFVar.getClass();
                yFVar.d.i0(MG.d(6, (String) null, (N0) null));
                return;
            case 10:
                ((WF) this.f142k).n.d.S();
                return;
            case 11:
                HZ hz = (HZ) this.f142k;
                hz.getClass();
                int i4 = cL.a;
                lZ lZVar = hz.b.j.p;
                lZVar.B(lZVar.F(), 1010, new mQ(6));
                return;
            case 12:
                g00 g00Var = (g00) this.f142k;
                if (g00Var.V >= 300000) {
                    ((i00) g00Var.m.f2063k).R0 = true;
                    g00Var.V = 0L;
                    return;
                }
                return;
            default:
                N10 n10 = (b30) this.f142k;
                for (W10 w10 : n10.y) {
                    w10.o(true);
                    if (w10.A != null) {
                        w10.A = null;
                        w10.f = null;
                    }
                }
                c10 c10Var = n10.q;
                if (c10Var.b != null) {
                    c10Var.b = null;
                }
                c10Var.c = null;
                return;
        }
    }

    public /* synthetic */ k1(HZ hz, long j4) {
        this.f141j = 11;
        this.f142k = hz;
    }
}
