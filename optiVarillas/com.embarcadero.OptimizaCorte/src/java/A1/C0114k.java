package A1;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import c2.c;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Xh;
import com.google.android.gms.internal.ads.Yh;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.k  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0114k extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f139b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ C0120n f140c;

    public C0114k(C0120n c0120n, Context context) {
        this.f139b = context;
        this.f140c = c0120n;
    }

    @Override // A1.AbstractC0122o
    public final Object a() {
        C0120n.b(this.f139b, "mobile_ads_settings");
        return new AbstractC0097e0();
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.e0(new c2.b(this.f139b), 241199000);
    }

    @Override // A1.AbstractC0122o
    public final Object c() {
        C0103g0 c0103g0;
        Object c0094d0;
        Object c0094d02;
        Context context = this.f139b;
        Gb.a(context);
        boolean booleanValue = ((Boolean) r.f168d.f171c.a(Gb.v9)).booleanValue();
        C0120n c0120n = this.f140c;
        if (booleanValue) {
            try {
                c2.b bVar = new c2.b(context);
                try {
                    IBinder b4 = E1.p.a(context).b("com.google.android.gms.ads.ChimeraMobileAdsSettingManagerCreatorImpl");
                    if (b4 == null) {
                        c0103g0 = null;
                    } else {
                        IInterface queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.ads.internal.client.IMobileAdsSettingManagerCreator");
                        if (queryLocalInterface instanceof C0103g0) {
                            c0103g0 = (C0103g0) queryLocalInterface;
                        } else {
                            c0103g0 = new C0103g0(b4);
                        }
                    }
                    Parcel B4 = c0103g0.B();
                    z8.e(B4, bVar);
                    B4.writeInt(241199000);
                    Parcel Z3 = c0103g0.Z(B4, 1);
                    IBinder readStrongBinder = Z3.readStrongBinder();
                    Z3.recycle();
                    if (readStrongBinder == null) {
                        return null;
                    }
                    IInterface queryLocalInterface2 = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IMobileAdsSettingManager");
                    if (queryLocalInterface2 instanceof InterfaceC0100f0) {
                        c0094d0 = (InterfaceC0100f0) queryLocalInterface2;
                    } else {
                        c0094d0 = new C0094d0(readStrongBinder);
                    }
                    return c0094d0;
                } catch (Exception e4) {
                    throw new Exception(e4);
                }
            } catch (E1.o e5) {
                e = e5;
                Yh b5 = Xh.b(context);
                c0120n.getClass();
                b5.a("ClientApiBroker.getMobileAdsSettingsManager", e);
                return null;
            } catch (RemoteException e6) {
                e = e6;
                Yh b52 = Xh.b(context);
                c0120n.getClass();
                b52.a("ClientApiBroker.getMobileAdsSettingsManager", e);
                return null;
            } catch (NullPointerException e7) {
                e = e7;
                Yh b522 = Xh.b(context);
                c0120n.getClass();
                b522.a("ClientApiBroker.getMobileAdsSettingsManager", e);
                return null;
            }
        }
        C0086a1 c0086a1 = (C0086a1) c0120n.f155l;
        c0086a1.getClass();
        try {
            c2.b bVar2 = new c2.b(context);
            C0103g0 c0103g02 = (C0103g0) c0086a1.b(context);
            Parcel B5 = c0103g02.B();
            z8.e(B5, bVar2);
            B5.writeInt(241199000);
            Parcel Z4 = c0103g02.Z(B5, 1);
            IBinder readStrongBinder2 = Z4.readStrongBinder();
            Z4.recycle();
            if (readStrongBinder2 == null) {
                return null;
            }
            IInterface queryLocalInterface3 = readStrongBinder2.queryLocalInterface("com.google.android.gms.ads.internal.client.IMobileAdsSettingManager");
            if (queryLocalInterface3 instanceof InterfaceC0100f0) {
                c0094d02 = (InterfaceC0100f0) queryLocalInterface3;
            } else {
                c0094d02 = new C0094d0(readStrongBinder2);
            }
            return c0094d02;
        } catch (RemoteException e8) {
            e = e8;
            E1.m.h("Could not get remote MobileAdsSettingManager.", e);
            return null;
        } catch (c.a e9) {
            e = e9;
            E1.m.h("Could not get remote MobileAdsSettingManager.", e);
            return null;
        }
    }
}
