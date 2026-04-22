package M1;

import A1.K0;
import A1.W0;
import A1.r;
import C1.k;
import E1.c;
import G3.g;
import android.content.Context;
import android.webkit.ValueCallback;
import com.google.android.gms.internal.ads.C10;
import com.google.android.gms.internal.ads.D10;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.HZ;
import com.google.android.gms.internal.ads.O3;
import com.google.android.gms.internal.ads.Sh;
import com.google.android.gms.internal.ads.bY;
import com.google.android.gms.internal.ads.cL;
import com.google.android.gms.internal.ads.fZ;
import com.google.android.gms.internal.ads.lZ;
import com.google.android.gms.internal.ads.oY;
import com.google.android.gms.internal.ads.pc;
import com.google.android.gms.internal.ads.rY;
import com.google.android.gms.internal.ads.rm;
import com.google.android.gms.internal.ads.s10;
import t1.C0802d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a {

    /* renamed from: a  reason: collision with root package name */
    public final W0 f1725a;

    public a(W0 w02) {
        this.f1725a = w02;
    }

    public static void a(final Context context, final C0802d c0802d, final g gVar) {
        Gb.a(context);
        if (((Boolean) pc.j.e()).booleanValue()) {
            if (((Boolean) r.f168d.f171c.a(Gb.T9)).booleanValue()) {
                c.f852b.execute(new Runnable() { // from class: M1.b
                    @Override // java.lang.Runnable
                    public final void run() {
                        K0 k02;
                        Object obj = c0802d;
                        Object obj2 = gVar;
                        Object obj3 = context;
                        switch (r4) {
                            case 0:
                                C0802d c0802d2 = (C0802d) obj;
                                if (c0802d2 == null) {
                                    k02 = null;
                                } else {
                                    k02 = c0802d2.f5787a;
                                }
                                new Sh((Context) obj3, k02).a((g) obj2);
                                return;
                            case 1:
                                ((rm) obj3).U0((String) obj, (ValueCallback) obj2);
                                return;
                            case 2:
                                HZ hz = (HZ) obj3;
                                hz.getClass();
                                int i4 = cL.a;
                                oY oYVar = hz.b;
                                oYVar.getClass();
                                int i5 = rY.T;
                                rY rYVar = oYVar.j;
                                rYVar.getClass();
                                lZ lZVar = rYVar.p;
                                fZ F4 = lZVar.F();
                                lZVar.B(F4, 1009, new k(F4, (O3) obj, (bY) obj2));
                                return;
                            default:
                                ((D10) obj).x(0, ((C10) obj3).a, (s10) obj2);
                                return;
                        }
                    }
                });
                return;
            }
        }
        new Sh(context, c0802d.f5787a).a(gVar);
    }
}
