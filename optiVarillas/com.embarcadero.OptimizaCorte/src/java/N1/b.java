package N1;

import A1.r;
import E1.m;
import W1.C0324l;
import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.A9;
import com.google.android.gms.internal.ads.C9;
import com.google.android.gms.internal.ads.D9;
import com.google.android.gms.internal.ads.F9;
import com.google.android.gms.internal.ads.G9;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Uw;
import com.google.android.gms.internal.ads.Xh;
import com.google.android.gms.internal.ads.nj;
import com.google.android.gms.internal.ads.pc;
import com.google.android.gms.internal.ads.v9;
import com.google.android.gms.internal.ads.x9;
import com.google.android.gms.internal.ads.y9;
import com.google.android.gms.internal.ads.z8;
import com.google.android.gms.internal.ads.zk;
import java.io.IOException;
import t1.C0802d;
import t1.C0811m;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public abstract class b {
    public static void b(final Context context, final String str, final C0802d c0802d, final Uw uw) {
        C0324l.e(context, "Context cannot be null.");
        C0324l.e(str, "AdUnitId cannot be null.");
        C0324l.b("#008 Must be called on the main UI thread.");
        Gb.a(context);
        if (((Boolean) pc.k.e()).booleanValue()) {
            if (((Boolean) r.f168d.f171c.a(Gb.T9)).booleanValue()) {
                E1.c.f852b.execute(new Runnable() { // from class: N1.c
                    @Override // java.lang.Runnable
                    public final void run() {
                        v9 a4;
                        switch (r5) {
                            case 0:
                                Context context2 = (Context) context;
                                String str2 = (String) str;
                                C0802d c0802d2 = (C0802d) c0802d;
                                try {
                                    new nj(context2, str2).d(c0802d2.f5787a, (Uw) uw);
                                    return;
                                } catch (IllegalStateException e4) {
                                    Xh.b(context2).a("RewardedAd.load", e4);
                                    return;
                                }
                            default:
                                D9 d9 = (D9) context;
                                x9 x9Var = (x9) str;
                                zk zkVar = (zk) uw;
                                try {
                                    A9 w4 = x9Var.w();
                                    boolean D4 = x9Var.D();
                                    y9 y9Var = (y9) c0802d;
                                    if (D4) {
                                        Parcel B4 = w4.B();
                                        z8.c(B4, y9Var);
                                        Parcel Z3 = w4.Z(B4, 2);
                                        a4 = (v9) z8.a(Z3, v9.CREATOR);
                                        Z3.recycle();
                                    } else {
                                        Parcel B5 = w4.B();
                                        z8.c(B5, y9Var);
                                        Parcel Z4 = w4.Z(B5, 1);
                                        a4 = z8.a(Z4, v9.CREATOR);
                                        Z4.recycle();
                                    }
                                    if (!a4.p()) {
                                        zkVar.c(new RuntimeException("No entry contents."));
                                        F9.a(d9.c);
                                        return;
                                    }
                                    C9 c9 = new C9(d9, a4.i());
                                    int read = c9.read();
                                    if (read != -1) {
                                        c9.unread(read);
                                        zkVar.b(new G9(c9, a4.o(), a4.r(), a4.h(), a4.q()));
                                        return;
                                    }
                                    throw new IOException("Unable to read from cache.");
                                } catch (RemoteException e5) {
                                    e = e5;
                                    m.e("Unable to obtain a cache service instance.", e);
                                    zkVar.c(e);
                                    F9.a(d9.c);
                                    return;
                                } catch (IOException e6) {
                                    e = e6;
                                    m.e("Unable to obtain a cache service instance.", e);
                                    zkVar.c(e);
                                    F9.a(d9.c);
                                    return;
                                }
                        }
                    }
                });
                return;
            }
        }
        m.b("Loading on UI thread");
        new nj(context, str).d(c0802d.f5787a, uw);
    }

    public abstract C0811m a();

    public abstract void c(Activity activity);
}
