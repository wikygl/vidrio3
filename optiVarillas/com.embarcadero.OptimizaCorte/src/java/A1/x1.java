package A1;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import c2.c;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Xh;
import com.google.android.gms.internal.ads.eg;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class x1 extends c2.c {
    @Override // c2.c
    public final /* synthetic */ Object a(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdManagerCreator");
        if (queryLocalInterface instanceof M) {
            return (M) queryLocalInterface;
        }
        return new M(iBinder);
    }

    public final L c(Context context, C1 c12, String str, eg egVar, int i4) {
        M m4;
        L j4;
        L j5;
        Gb.a(context);
        if (((Boolean) r.f168d.f171c.a(Gb.v9)).booleanValue()) {
            try {
                c2.b bVar = new c2.b(context);
                try {
                    IBinder b4 = E1.p.a(context).b("com.google.android.gms.ads.ChimeraAdManagerCreatorImpl");
                    if (b4 == null) {
                        m4 = null;
                    } else {
                        IInterface queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdManagerCreator");
                        if (queryLocalInterface instanceof M) {
                            m4 = (M) queryLocalInterface;
                        } else {
                            m4 = new M(b4);
                        }
                    }
                    IBinder g22 = m4.g2(bVar, c12, str, egVar, i4);
                    if (g22 == null) {
                        return null;
                    }
                    IInterface queryLocalInterface2 = g22.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdManager");
                    if (queryLocalInterface2 instanceof L) {
                        j4 = (L) queryLocalInterface2;
                    } else {
                        j4 = new J(g22);
                    }
                    return j4;
                } catch (Exception e4) {
                    throw new Exception(e4);
                }
            } catch (E1.o e5) {
                e = e5;
                Xh.b(context).a("AdManagerCreator.newAdManagerByDynamiteLoader", e);
                E1.m.i("#007 Could not call remote method.", e);
                return null;
            } catch (RemoteException e6) {
                e = e6;
                Xh.b(context).a("AdManagerCreator.newAdManagerByDynamiteLoader", e);
                E1.m.i("#007 Could not call remote method.", e);
                return null;
            } catch (NullPointerException e7) {
                e = e7;
                Xh.b(context).a("AdManagerCreator.newAdManagerByDynamiteLoader", e);
                E1.m.i("#007 Could not call remote method.", e);
                return null;
            }
        }
        try {
            IBinder g23 = ((M) b(context)).g2(new c2.b(context), c12, str, egVar, i4);
            if (g23 == null) {
                return null;
            }
            IInterface queryLocalInterface3 = g23.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdManager");
            if (queryLocalInterface3 instanceof L) {
                j5 = (L) queryLocalInterface3;
            } else {
                j5 = new J(g23);
            }
            return j5;
        } catch (RemoteException e8) {
            e = e8;
            E1.m.c("Could not create remote AdManager.", e);
            return null;
        } catch (c.a e9) {
            e = e9;
            E1.m.c("Could not create remote AdManager.", e);
            return null;
        }
    }
}
