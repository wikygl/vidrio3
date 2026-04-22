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
import com.google.android.gms.internal.ads.bg;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.j  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0111j extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f135b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ String f136c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ eg f137d;

    /* renamed from: e  reason: collision with root package name */
    public final /* synthetic */ C0120n f138e;

    public C0111j(C0120n c0120n, Context context, String str, bg bgVar) {
        this.f135b = context;
        this.f136c = str;
        this.f137d = bgVar;
        this.f138e = c0120n;
    }

    @Override // A1.AbstractC0122o
    public final Object a() {
        C0120n.b(this.f135b, "native_ad");
        return new F();
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.w3(new c2.b(this.f135b), this.f136c, this.f137d, 241199000);
    }

    @Override // A1.AbstractC0122o
    public final Object c() {
        H h4;
        Object e4;
        Object e5;
        Context context = this.f135b;
        Gb.a(context);
        boolean booleanValue = ((Boolean) r.f168d.f171c.a(Gb.v9)).booleanValue();
        eg egVar = this.f137d;
        String str = this.f136c;
        C0120n c0120n = this.f138e;
        if (booleanValue) {
            try {
                c2.b bVar = new c2.b(context);
                try {
                    IBinder b4 = E1.p.a(context).b("com.google.android.gms.ads.ChimeraAdLoaderBuilderCreatorImpl");
                    if (b4 == null) {
                        h4 = null;
                    } else {
                        IInterface queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdLoaderBuilderCreator");
                        if (queryLocalInterface instanceof H) {
                            h4 = (H) queryLocalInterface;
                        } else {
                            h4 = new H(b4);
                        }
                    }
                    Parcel B4 = h4.B();
                    z8.e(B4, bVar);
                    B4.writeString(str);
                    z8.e(B4, egVar);
                    B4.writeInt(241199000);
                    Parcel Z3 = h4.Z(B4, 1);
                    IBinder readStrongBinder = Z3.readStrongBinder();
                    Z3.recycle();
                    if (readStrongBinder == null) {
                        return null;
                    }
                    IInterface queryLocalInterface2 = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdLoaderBuilder");
                    if (queryLocalInterface2 instanceof G) {
                        e4 = (G) queryLocalInterface2;
                    } else {
                        e4 = new E(readStrongBinder);
                    }
                    return e4;
                } catch (Exception e6) {
                    throw new Exception(e6);
                }
            } catch (E1.o e7) {
                e = e7;
                Yh b5 = Xh.b(context);
                c0120n.getClass();
                b5.a("ClientApiBroker.createAdLoaderBuilder", e);
                return null;
            } catch (RemoteException e8) {
                e = e8;
                Yh b52 = Xh.b(context);
                c0120n.getClass();
                b52.a("ClientApiBroker.createAdLoaderBuilder", e);
                return null;
            } catch (NullPointerException e9) {
                e = e9;
                Yh b522 = Xh.b(context);
                c0120n.getClass();
                b522.a("ClientApiBroker.createAdLoaderBuilder", e);
                return null;
            }
        }
        w1 w1Var = (w1) c0120n.f154k;
        w1Var.getClass();
        try {
            c2.b bVar2 = new c2.b(context);
            H h5 = (H) w1Var.b(context);
            Parcel B5 = h5.B();
            z8.e(B5, bVar2);
            B5.writeString(str);
            z8.e(B5, egVar);
            B5.writeInt(241199000);
            Parcel Z4 = h5.Z(B5, 1);
            IBinder readStrongBinder2 = Z4.readStrongBinder();
            Z4.recycle();
            if (readStrongBinder2 == null) {
                return null;
            }
            IInterface queryLocalInterface3 = readStrongBinder2.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdLoaderBuilder");
            if (queryLocalInterface3 instanceof G) {
                e5 = (G) queryLocalInterface3;
            } else {
                e5 = new E(readStrongBinder2);
            }
            return e5;
        } catch (RemoteException e10) {
            e = e10;
            E1.m.h("Could not create remote builder for AdLoader.", e);
            return null;
        } catch (c.a e11) {
            e = e11;
            E1.m.h("Could not create remote builder for AdLoader.", e);
            return null;
        }
    }
}
