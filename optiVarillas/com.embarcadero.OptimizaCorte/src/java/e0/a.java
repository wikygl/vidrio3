package E0;

import A1.N0;
import C0.i;
import E1.m;
import E1.o;
import L0.p;
import S0.C0284u0;
import a3.InterfaceFutureC0346a;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.webkit.WebView;
import androidx.work.ListenableWorker;
import androidx.work.impl.workers.ConstraintTrackingWorker;
import com.google.android.gms.internal.ads.B1;
import com.google.android.gms.internal.ads.B8;
import com.google.android.gms.internal.ads.C0;
import com.google.android.gms.internal.ads.C8;
import com.google.android.gms.internal.ads.D0;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.J10;
import com.google.android.gms.internal.ads.L9;
import com.google.android.gms.internal.ads.Lb;
import com.google.android.gms.internal.ads.N10;
import com.google.android.gms.internal.ads.RY;
import com.google.android.gms.internal.ads.T;
import com.google.android.gms.internal.ads.XY;
import com.google.android.gms.internal.ads.aI;
import com.google.android.gms.internal.ads.mF;
import com.google.android.gms.internal.ads.qY;
import com.google.android.gms.internal.ads.rY;
import com.google.android.gms.internal.ads.uI;
import com.google.android.gms.internal.ads.v10;
import com.google.android.gms.internal.ads.wY;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.xq;
import i2.H;
import i2.r;
import java.util.Arrays;
import java.util.List;
import o.e;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f828j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f829k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f830l;

    public /* synthetic */ a(Object obj, int i4, Object obj2) {
        this.f828j = i4;
        this.f830l = obj;
        this.f829k = obj2;
    }

    private final void a() {
        boolean booleanValue;
        r rVar = (r) this.f829k;
        String str = (String) this.f830l;
        synchronized (H.class) {
            if (H.f3681a == null) {
                try {
                    rVar.evaluateJavascript("(function(){})()", null);
                    H.f3681a = Boolean.TRUE;
                } catch (IllegalStateException unused) {
                    H.f3681a = Boolean.FALSE;
                }
            }
            booleanValue = H.f3681a.booleanValue();
        }
        if (booleanValue) {
            rVar.evaluateJavascript(str, null);
        } else {
            rVar.loadUrl("javascript:".concat(str));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.lang.Runnable
    public final void run() {
        C8 x8Var;
        long j4;
        boolean z4;
        boolean z5;
        D0 c02;
        long j5 = -9223372036854775807L;
        boolean z6 = false;
        int i4 = 1;
        switch (this.f828j) {
            case 0:
                i c4 = i.c();
                String str = b.f831d;
                p pVar = (p) this.f829k;
                c4.a(str, C0284u0.c("Scheduling work ", pVar.f1450a), new Throwable[0]);
                ((b) this.f830l).f832a.f(pVar);
                return;
            case 1:
                synchronized (((ConstraintTrackingWorker) this.f830l).p) {
                    if (((ConstraintTrackingWorker) this.f830l).q) {
                        ConstraintTrackingWorker constraintTrackingWorker = (ConstraintTrackingWorker) this.f830l;
                        constraintTrackingWorker.getClass();
                        constraintTrackingWorker.r.j(new ListenableWorker.a.b());
                    } else {
                        ((ConstraintTrackingWorker) this.f830l).r.l((InterfaceFutureC0346a) this.f829k);
                    }
                }
                return;
            case 2:
                boolean booleanValue = ((Boolean) A1.r.f168d.f171c.a(Gb.p4)).booleanValue();
                L9 l9 = (L9) this.f829k;
                Context context = (Context) this.f830l;
                if (booleanValue) {
                    try {
                        try {
                            IBinder b4 = E1.p.a(context).b("com.google.android.gms.ads.clearcut.DynamiteClearcutLogger");
                            int i5 = B8.j;
                            if (b4 == null) {
                                x8Var = null;
                            } else {
                                C8 queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.ads.clearcut.IClearcut");
                                if (queryLocalInterface instanceof C8) {
                                    x8Var = queryLocalInterface;
                                } else {
                                    x8Var = new x8(b4, "com.google.android.gms.ads.clearcut.IClearcut");
                                }
                            }
                            l9.a = x8Var;
                            l9.a.W(new c2.b(context));
                            l9.b = true;
                            return;
                        } catch (Exception e4) {
                            throw new Exception(e4);
                        }
                    } catch (o | RemoteException | NullPointerException unused) {
                        m.b("Cannot dynamite load clearcut");
                        return;
                    }
                }
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                if (((Boolean) A1.r.f168d.f171c.a(Gb.v4)).booleanValue() && Lb.x.a) {
                    ((aI) this.f829k).a((View) this.f830l);
                    return;
                }
                return;
            case 4:
                ((mF) this.f829k).n.d.i0((N0) this.f830l);
                return;
            case 5:
                uI.b((WebView) this.f829k, (String) this.f830l);
                return;
            case 6:
                rY rYVar = (rY) this.f829k;
                wY wYVar = (wY) this.f830l;
                int i6 = rYVar.x - wYVar.c;
                rYVar.x = i6;
                if (wYVar.d) {
                    rYVar.y = wYVar.e;
                    rYVar.z = true;
                }
                if (wYVar.f) {
                    rYVar.A = wYVar.g;
                }
                if (i6 == 0) {
                    XY xy = wYVar.b.a;
                    if (!rYVar.O.a.o() && xy.o()) {
                        rYVar.P = -1;
                        rYVar.Q = 0L;
                    }
                    if (!xy.o()) {
                        List asList = Arrays.asList(xy.h);
                        if (asList.size() == rYVar.n.size()) {
                            z5 = true;
                        } else {
                            z5 = false;
                        }
                        T.v(z5);
                        for (int i7 = 0; i7 < asList.size(); i7++) {
                            ((qY) rYVar.n.get(i7)).b = (xq) asList.get(i7);
                        }
                    }
                    if (rYVar.z) {
                        if (wYVar.b.b.equals(rYVar.O.b) && wYVar.b.d == rYVar.O.q) {
                            i4 = 0;
                        }
                        if (i4 != 0) {
                            if (!xy.o() && !wYVar.b.b.b()) {
                                RY ry = wYVar.b;
                                v10 v10Var = ry.b;
                                j5 = ry.d;
                                xy.n(v10Var.a, rYVar.m);
                            } else {
                                j5 = wYVar.b.d;
                            }
                        }
                        j4 = j5;
                        z4 = i4;
                    } else {
                        j4 = -9223372036854775807L;
                        z4 = 0;
                    }
                    rYVar.z = false;
                    rYVar.D(wYVar.b, 1, rYVar.A, z4, rYVar.y, j4, -1);
                    return;
                }
                return;
            case 7:
                N10 n10 = (N10) this.f829k;
                B1 b12 = n10.x;
                D0 d02 = (D0) this.f830l;
                if (b12 == null) {
                    c02 = d02;
                } else {
                    c02 = new C0(-9223372036854775807L, 0L);
                }
                n10.E = c02;
                if (d02.a() == -9223372036854775807L && n10.F != -9223372036854775807L) {
                    n10.E = new J10(n10, n10.E);
                }
                n10.F = n10.E.a();
                if (!n10.L && d02.a() == -9223372036854775807L) {
                    z6 = true;
                }
                n10.G = z6;
                if (true == z6) {
                    i4 = 7;
                }
                n10.H = i4;
                n10.n.r(n10.F, d02.g(), n10.G);
                if (!n10.B) {
                    n10.w();
                    return;
                }
                return;
            case 8:
                a();
                return;
            default:
                ((e) this.f830l).f5401k.c((Bundle) this.f829k);
                return;
        }
    }

    public /* synthetic */ a(Object obj, Object obj2, int i4, boolean z4) {
        this.f828j = i4;
        this.f829k = obj;
        this.f830l = obj2;
    }
}
