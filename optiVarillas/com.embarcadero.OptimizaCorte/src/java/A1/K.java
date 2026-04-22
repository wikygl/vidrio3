package A1;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.Lh;
import com.google.android.gms.internal.ads.Nh;
import com.google.android.gms.internal.ads.Qi;
import com.google.android.gms.internal.ads.Ri;
import com.google.android.gms.internal.ads.Xb;
import com.google.android.gms.internal.ads.l9;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class K extends y8 implements L {
    public K() {
        super("com.google.android.gms.ads.internal.client.IAdManager");
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        Xb xb = null;
        switch (i4) {
            case 1:
                InterfaceC0374a l2 = l();
                parcel2.writeNoException();
                z8.e(parcel2, l2);
                return true;
            case 2:
                I();
                parcel2.writeNoException();
                return true;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                boolean s02 = s0();
                parcel2.writeNoException();
                ClassLoader classLoader = z8.a;
                parcel2.writeInt(s02 ? 1 : 0);
                return true;
            case 4:
                z8.b(parcel);
                boolean V12 = V1((y1) z8.a(parcel, y1.CREATOR));
                parcel2.writeNoException();
                parcel2.writeInt(V12 ? 1 : 0);
                return true;
            case 5:
                Y();
                parcel2.writeNoException();
                return true;
            case 6:
                M();
                parcel2.writeNoException();
                return true;
            case 7:
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder != null) {
                    Xb queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdListener");
                    if (queryLocalInterface instanceof InterfaceC0139x) {
                        xb = (InterfaceC0139x) queryLocalInterface;
                    } else {
                        xb = new C0135v(readStrongBinder);
                    }
                }
                z8.b(parcel);
                p1(xb);
                parcel2.writeNoException();
                return true;
            case 8:
                IBinder readStrongBinder2 = parcel.readStrongBinder();
                if (readStrongBinder2 != null) {
                    Xb queryLocalInterface2 = readStrongBinder2.queryLocalInterface("com.google.android.gms.ads.internal.client.IAppEventListener");
                    if (queryLocalInterface2 instanceof S) {
                        xb = (S) queryLocalInterface2;
                    } else {
                        xb = new P(readStrongBinder2);
                    }
                }
                z8.b(parcel);
                N3(xb);
                parcel2.writeNoException();
                return true;
            case 9:
                q2();
                parcel2.writeNoException();
                return true;
            case 10:
                parcel2.writeNoException();
                return true;
            case 11:
                P();
                parcel2.writeNoException();
                return true;
            case 12:
                C1 h4 = h();
                parcel2.writeNoException();
                z8.d(parcel2, h4);
                return true;
            case 13:
                z8.b(parcel);
                M0((C1) z8.a(parcel, C1.CREATOR));
                parcel2.writeNoException();
                return true;
            case 14:
                IBinder readStrongBinder3 = parcel.readStrongBinder();
                if (readStrongBinder3 != null && (readStrongBinder3.queryLocalInterface("com.google.android.gms.ads.internal.purchase.client.IInAppPurchaseListener") instanceof Lh)) {
                }
                z8.b(parcel);
                H();
                parcel2.writeNoException();
                return true;
            case 15:
                IBinder readStrongBinder4 = parcel.readStrongBinder();
                if (readStrongBinder4 != null && (readStrongBinder4.queryLocalInterface("com.google.android.gms.ads.internal.purchase.client.IPlayStorePurchaseListener") instanceof Nh)) {
                }
                parcel.readString();
                z8.b(parcel);
                m0();
                parcel2.writeNoException();
                return true;
            case 16:
            case 17:
            case 27:
            case 28:
            default:
                return false;
            case 18:
                String C4 = C();
                parcel2.writeNoException();
                parcel2.writeString(C4);
                return true;
            case 19:
                IBinder readStrongBinder5 = parcel.readStrongBinder();
                if (readStrongBinder5 != null) {
                    Xb queryLocalInterface3 = readStrongBinder5.queryLocalInterface("com.google.android.gms.ads.internal.customrenderedad.client.IOnCustomRenderedAdLoadedListener");
                    if (queryLocalInterface3 instanceof Xb) {
                        xb = queryLocalInterface3;
                    } else {
                        xb = new x8(readStrongBinder5, "com.google.android.gms.ads.internal.customrenderedad.client.IOnCustomRenderedAdLoadedListener");
                    }
                }
                z8.b(parcel);
                P3(xb);
                parcel2.writeNoException();
                return true;
            case 20:
                IBinder readStrongBinder6 = parcel.readStrongBinder();
                if (readStrongBinder6 != null) {
                    Xb queryLocalInterface4 = readStrongBinder6.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdClickListener");
                    if (queryLocalInterface4 instanceof InterfaceC0133u) {
                        xb = (InterfaceC0133u) queryLocalInterface4;
                    } else {
                        xb = new x8(readStrongBinder6, "com.google.android.gms.ads.internal.client.IAdClickListener");
                    }
                }
                z8.b(parcel);
                B1(xb);
                parcel2.writeNoException();
                return true;
            case 21:
                IBinder readStrongBinder7 = parcel.readStrongBinder();
                if (readStrongBinder7 != null) {
                    Xb queryLocalInterface5 = readStrongBinder7.queryLocalInterface("com.google.android.gms.ads.internal.client.ICorrelationIdProvider");
                    if (queryLocalInterface5 instanceof W) {
                        xb = (W) queryLocalInterface5;
                    } else {
                        xb = new W(readStrongBinder7);
                    }
                }
                z8.b(parcel);
                x0(xb);
                parcel2.writeNoException();
                return true;
            case 22:
                boolean f = z8.f(parcel);
                z8.b(parcel);
                p4(f);
                parcel2.writeNoException();
                return true;
            case 23:
                boolean l0 = l0();
                parcel2.writeNoException();
                ClassLoader classLoader2 = z8.a;
                parcel2.writeInt(l0 ? 1 : 0);
                return true;
            case 24:
                IBinder readStrongBinder8 = parcel.readStrongBinder();
                if (readStrongBinder8 != null) {
                    Xb queryLocalInterface6 = readStrongBinder8.queryLocalInterface("com.google.android.gms.ads.internal.reward.client.IRewardedVideoAdListener");
                    if (queryLocalInterface6 instanceof Ri) {
                        xb = (Ri) queryLocalInterface6;
                    } else {
                        xb = new Qi(readStrongBinder8);
                    }
                }
                z8.b(parcel);
                H1(xb);
                parcel2.writeNoException();
                return true;
            case 25:
                parcel.readString();
                z8.b(parcel);
                d0();
                parcel2.writeNoException();
                return true;
            case 26:
                D0 m4 = m();
                parcel2.writeNoException();
                z8.e(parcel2, m4);
                return true;
            case 29:
                z8.b(parcel);
                v0((s1) z8.a(parcel, s1.CREATOR));
                parcel2.writeNoException();
                return true;
            case 30:
                H0 h02 = (H0) z8.a(parcel, H0.CREATOR);
                z8.b(parcel);
                b0();
                parcel2.writeNoException();
                return true;
            case 31:
                String t3 = t();
                parcel2.writeNoException();
                parcel2.writeString(t3);
                return true;
            case 32:
                S j4 = j();
                parcel2.writeNoException();
                z8.e(parcel2, j4);
                return true;
            case 33:
                InterfaceC0139x f4 = f();
                parcel2.writeNoException();
                z8.e(parcel2, f4);
                return true;
            case 34:
                boolean f5 = z8.f(parcel);
                z8.b(parcel);
                M2(f5);
                parcel2.writeNoException();
                return true;
            case 35:
                String D4 = D();
                parcel2.writeNoException();
                parcel2.writeString(D4);
                return true;
            case 36:
                IBinder readStrongBinder9 = parcel.readStrongBinder();
                if (readStrongBinder9 != null) {
                    IInterface queryLocalInterface7 = readStrongBinder9.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdMetadataListener");
                    if (queryLocalInterface7 instanceof O) {
                        O o4 = (O) queryLocalInterface7;
                    }
                }
                z8.b(parcel);
                O();
                parcel2.writeNoException();
                return true;
            case 37:
                Bundle i5 = i();
                parcel2.writeNoException();
                z8.d(parcel2, i5);
                return true;
            case 38:
                parcel.readString();
                z8.b(parcel);
                t0();
                parcel2.writeNoException();
                return true;
            case 39:
                z8.b(parcel);
                m1((I1) z8.a(parcel, I1.CREATOR));
                parcel2.writeNoException();
                return true;
            case 40:
                IBinder readStrongBinder10 = parcel.readStrongBinder();
                if (readStrongBinder10 != null) {
                    Xb queryLocalInterface8 = readStrongBinder10.queryLocalInterface("com.google.android.gms.ads.internal.appopen.client.IAppOpenAdLoadCallback");
                    if (queryLocalInterface8 instanceof l9) {
                        xb = (l9) queryLocalInterface8;
                    } else {
                        xb = new x8(readStrongBinder10, "com.google.android.gms.ads.internal.appopen.client.IAppOpenAdLoadCallback");
                    }
                }
                z8.b(parcel);
                b2(xb);
                parcel2.writeNoException();
                return true;
            case 41:
                A0 k4 = k();
                parcel2.writeNoException();
                z8.e(parcel2, k4);
                return true;
            case 42:
                IBinder readStrongBinder11 = parcel.readStrongBinder();
                if (readStrongBinder11 != null) {
                    Xb queryLocalInterface9 = readStrongBinder11.queryLocalInterface("com.google.android.gms.ads.internal.client.IOnPaidEventListener");
                    if (queryLocalInterface9 instanceof InterfaceC0134u0) {
                        xb = (InterfaceC0134u0) queryLocalInterface9;
                    } else {
                        xb = new C0130s0(readStrongBinder11);
                    }
                }
                z8.b(parcel);
                X1(xb);
                parcel2.writeNoException();
                return true;
            case 43:
                y1 y1Var = (y1) z8.a(parcel, y1.CREATOR);
                IBinder readStrongBinder12 = parcel.readStrongBinder();
                if (readStrongBinder12 != null) {
                    Xb queryLocalInterface10 = readStrongBinder12.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdLoadCallback");
                    if (queryLocalInterface10 instanceof A) {
                        xb = (A) queryLocalInterface10;
                    } else {
                        xb = new x8(readStrongBinder12, "com.google.android.gms.ads.internal.client.IAdLoadCallback");
                    }
                }
                z8.b(parcel);
                J3(y1Var, xb);
                parcel2.writeNoException();
                return true;
            case 44:
                InterfaceC0374a Z3 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                z8.b(parcel);
                E0(Z3);
                parcel2.writeNoException();
                return true;
            case 45:
                IBinder readStrongBinder13 = parcel.readStrongBinder();
                if (readStrongBinder13 != null) {
                    Xb queryLocalInterface11 = readStrongBinder13.queryLocalInterface("com.google.android.gms.ads.internal.client.IFullScreenContentCallback");
                    if (queryLocalInterface11 instanceof Z) {
                        xb = (Z) queryLocalInterface11;
                    } else {
                        xb = new x8(readStrongBinder13, "com.google.android.gms.ads.internal.client.IFullScreenContentCallback");
                    }
                }
                z8.b(parcel);
                Y0(xb);
                parcel2.writeNoException();
                return true;
        }
    }
}
