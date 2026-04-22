package A1;

import android.app.Activity;
import android.os.IBinder;
import android.os.RemoteException;
import c2.c;
import com.google.android.gms.internal.ads.Dh;
import com.google.android.gms.internal.ads.Eh;
import com.google.android.gms.internal.ads.Fh;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Gh;
import com.google.android.gms.internal.ads.Hh;
import com.google.android.gms.internal.ads.Ih;
import com.google.android.gms.internal.ads.Jh;
import com.google.android.gms.internal.ads.Xh;
import com.google.android.gms.internal.ads.Yh;

/* renamed from: A1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0087b extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Activity f101b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ C0120n f102c;

    public C0087b(C0120n c0120n, Activity activity) {
        this.f101b = activity;
        this.f102c = c0120n;
    }

    @Override // A1.AbstractC0122o
    public final /* bridge */ /* synthetic */ Object a() {
        C0120n.b(this.f101b, "ad_overlay");
        return null;
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.X(new c2.b(this.f101b));
    }

    @Override // A1.AbstractC0122o
    public final Object c() {
        Jh hh;
        Gh eh;
        Gh eh2;
        Activity activity = this.f101b;
        Gb.a(activity);
        boolean booleanValue = ((Boolean) r.f168d.f171c.a(Gb.v9)).booleanValue();
        C0120n c0120n = this.f102c;
        if (booleanValue) {
            try {
                c2.b bVar = new c2.b(activity);
                try {
                    IBinder b4 = E1.p.a(activity).b("com.google.android.gms.ads.ChimeraAdOverlayCreatorImpl");
                    int i4 = Ih.j;
                    if (b4 == null) {
                        hh = null;
                    } else {
                        Jh queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.ads.internal.overlay.client.IAdOverlayCreator");
                        if (queryLocalInterface instanceof Jh) {
                            hh = queryLocalInterface;
                        } else {
                            hh = new Hh(b4);
                        }
                    }
                    IBinder E22 = hh.E2(bVar);
                    int i5 = Fh.j;
                    if (E22 == null) {
                        return null;
                    }
                    Gh queryLocalInterface2 = E22.queryLocalInterface("com.google.android.gms.ads.internal.overlay.client.IAdOverlay");
                    if (queryLocalInterface2 instanceof Gh) {
                        eh = queryLocalInterface2;
                    } else {
                        eh = new Eh(E22);
                    }
                    return eh;
                } catch (Exception e4) {
                    throw new Exception(e4);
                }
            } catch (E1.o | RemoteException | NullPointerException e5) {
                Yh b5 = Xh.b(activity.getApplicationContext());
                c0120n.getClass();
                b5.a("ClientApiBroker.createAdOverlay", e5);
                return null;
            }
        }
        Dh dh = (Dh) c0120n.f157n;
        dh.getClass();
        try {
            IBinder E23 = ((Jh) dh.b(activity)).E2(new c2.b(activity));
            if (E23 == null) {
                return null;
            }
            Gh queryLocalInterface3 = E23.queryLocalInterface("com.google.android.gms.ads.internal.overlay.client.IAdOverlay");
            if (queryLocalInterface3 instanceof Gh) {
                eh2 = queryLocalInterface3;
            } else {
                eh2 = new Eh(E23);
            }
            return eh2;
        } catch (RemoteException e6) {
            E1.m.h("Could not create remote AdOverlay.", e6);
            return null;
        } catch (c.a e7) {
            E1.m.h("Could not create remote AdOverlay.", e7);
            return null;
        }
    }
}
