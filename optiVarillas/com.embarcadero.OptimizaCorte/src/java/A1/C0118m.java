package A1;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.bg;
import com.google.android.gms.internal.ads.cj;
import com.google.android.gms.internal.ads.dj;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.ej;
import com.google.android.gms.internal.ads.ij;
import com.google.android.gms.internal.ads.x8;

/* renamed from: A1.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0118m extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f150b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ String f151c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ eg f152d;

    public C0118m(Context context, String str, bg bgVar) {
        this.f150b = context;
        this.f151c = str;
        this.f152d = bgVar;
    }

    @Override // A1.AbstractC0122o
    public final Object a() {
        C0120n.b(this.f150b, "rewarded");
        return new dj();
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.z1(new c2.b(this.f150b), this.f151c, this.f152d, 241199000);
    }

    @Override // A1.AbstractC0122o
    public final Object c() {
        ij x8Var;
        ej cjVar;
        String str = this.f151c;
        eg egVar = this.f152d;
        Context context = this.f150b;
        c2.b bVar = new c2.b(context);
        try {
            try {
                IBinder b4 = E1.p.a(context).b("com.google.android.gms.ads.rewarded.ChimeraRewardedAdCreatorImpl");
                if (b4 == null) {
                    x8Var = null;
                } else {
                    ij queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.ads.internal.rewarded.client.IRewardedAdCreator");
                    if (queryLocalInterface instanceof ij) {
                        x8Var = queryLocalInterface;
                    } else {
                        x8Var = new x8(b4, "com.google.android.gms.ads.internal.rewarded.client.IRewardedAdCreator");
                    }
                }
                IBinder g22 = x8Var.g2(bVar, str, egVar);
                if (g22 == null) {
                    return null;
                }
                ej queryLocalInterface2 = g22.queryLocalInterface("com.google.android.gms.ads.internal.rewarded.client.IRewardedAd");
                if (queryLocalInterface2 instanceof ej) {
                    cjVar = queryLocalInterface2;
                } else {
                    cjVar = new cj(g22);
                }
                return cjVar;
            } catch (Exception e4) {
                throw new Exception(e4);
            }
        } catch (E1.o e5) {
            e = e5;
            E1.m.i("#007 Could not call remote method.", e);
            return null;
        } catch (RemoteException e6) {
            e = e6;
            E1.m.i("#007 Could not call remote method.", e);
            return null;
        }
    }
}
