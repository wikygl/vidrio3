package D1;

import android.os.RemoteException;
import android.util.Log;
import android.webkit.WebView;
import com.google.android.gms.internal.ads.Bd;
import com.google.android.gms.internal.ads.Be;
import com.google.android.gms.internal.ads.DI;
import com.google.android.gms.internal.ads.FI;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.HZ;
import com.google.android.gms.internal.ads.KJ;
import com.google.android.gms.internal.ads.Lb;
import com.google.android.gms.internal.ads.Mk;
import com.google.android.gms.internal.ads.NB;
import com.google.android.gms.internal.ads.OJ;
import com.google.android.gms.internal.ads.Rk;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.WJ;
import com.google.android.gms.internal.ads.XB;
import com.google.android.gms.internal.ads.aG;
import com.google.android.gms.internal.ads.aI;
import com.google.android.gms.internal.ads.bx;
import com.google.android.gms.internal.ads.cL;
import com.google.android.gms.internal.ads.cp;
import com.google.android.gms.internal.ads.jZ;
import com.google.android.gms.internal.ads.lZ;
import com.google.android.gms.internal.ads.me;
import com.google.android.gms.internal.ads.mh;
import com.google.android.gms.internal.ads.n7;
import com.google.android.gms.internal.ads.of;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import p2.C0758g;

/* renamed from: D1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class RunnableC0176a implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f666j;

    /* renamed from: k  reason: collision with root package name */
    public final Object f667k;

    public /* synthetic */ RunnableC0176a(int i4, Object obj) {
        this.f666j = i4;
        this.f667k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean z4;
        boolean z5 = false;
        switch (this.f666j) {
            case 0:
                Thread.currentThread();
                AbstractC0200u abstractC0200u = (AbstractC0200u) this.f667k;
                abstractC0200u.getClass();
                abstractC0200u.a();
                return;
            case 1:
                C0195o c0195o = (C0195o) this.f667k;
                c0195o.getClass();
                z1.p.f6575A.f6587m.a(c0195o.f745a);
                return;
            case 2:
                ((j0) this.f667k).r();
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                com.google.android.gms.internal.ads.K k4 = (com.google.android.gms.internal.ads.K) this.f667k;
                k4.getClass();
                int i4 = cL.a;
                lZ lZVar = k4.b.j.p;
                lZVar.B(lZVar.F(), 1030, new FI(7, (byte) 0));
                return;
            case 4:
                if (((n7) this.f667k).b == null) {
                    synchronized (n7.c) {
                        if (((n7) this.f667k).b == null) {
                            try {
                                z4 = ((Boolean) Gb.g2.e()).booleanValue();
                            } catch (IllegalStateException unused) {
                                z4 = false;
                            }
                            if (z4) {
                                try {
                                    n7.d = KJ.a(((n7) this.f667k).a.a, "ADSHIELD");
                                } catch (Throwable unused2) {
                                }
                            }
                            z5 = z4;
                            ((n7) this.f667k).b = Boolean.valueOf(z5);
                            n7.c.open();
                            return;
                        }
                        return;
                    }
                }
                return;
            case 5:
                Be be = me.j;
                of ofVar = (of) this.f667k;
                ofVar.j0("/result", be);
                ofVar.r();
                return;
            case 6:
                Rk rk = ((Mk) this.f667k).y;
                if (rk != null) {
                    Rk rk2 = rk;
                    Runnable runnable = rk2.n;
                    ((mh) runnable).k = false;
                    WJ wj = t0.f774l;
                    wj.removeCallbacks(runnable);
                    wj.postDelayed(runnable, 250L);
                    wj.post(new com.google.android.gms.internal.ads.H(2, rk2));
                    return;
                }
                return;
            case 7:
                ((Rk) this.f667k).c("surfaceDestroyed", new String[0]);
                return;
            case 8:
                cp cpVar = (cp) this.f667k;
                Bd bd = cpVar.o.d;
                if (bd != null) {
                    try {
                        bd.w2((A1.L) cpVar.q.c(), new c2.b(cpVar.j));
                        return;
                    } catch (RemoteException e4) {
                        E1.m.e("RemoteException when notifyAdLoad is called", e4);
                        return;
                    }
                }
                return;
            case 9:
                ((TN) this.f667k).m(new bx(3));
                return;
            case 10:
                if (((Boolean) A1.r.f168d.f171c.a(Gb.v4)).booleanValue() && Lb.x.a) {
                    ((aI) this.f667k).b();
                    return;
                }
                return;
            case 11:
                ((XB) this.f667k).n.d.b.S();
                return;
            case 12:
                ((NB) this.f667k).S();
                return;
            case 13:
                ((aG) this.f667k).S();
                return;
            case 14:
                ((WebView) this.f667k).destroy();
                return;
            case 15:
                OJ oj = new OJ();
                Log.d("GASS", "Clearcut logging disabled");
                ((C0758g) this.f667k).f5552a.m(new KJ(oj));
                return;
            default:
                HZ hz = (HZ) this.f667k;
                hz.getClass();
                int i5 = cL.a;
                lZ lZVar2 = hz.b.j.p;
                lZVar2.B(lZVar2.F(), 1031, new jZ(0));
                return;
        }
    }

    public RunnableC0176a(DI di) {
        this.f666j = 14;
        this.f667k = di.e;
    }

    public /* synthetic */ RunnableC0176a(Object obj, int i4, Object obj2) {
        this.f666j = i4;
        this.f667k = obj;
    }
}
