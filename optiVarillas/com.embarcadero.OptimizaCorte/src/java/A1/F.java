package A1;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.internal.ads.Bd;
import com.google.android.gms.internal.ads.Ed;
import com.google.android.gms.internal.ads.Qc;
import com.google.android.gms.internal.ads.df;
import com.google.android.gms.internal.ads.kf;
import com.google.android.gms.internal.ads.sd;
import com.google.android.gms.internal.ads.ud;
import com.google.android.gms.internal.ads.wd;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.yd;
import com.google.android.gms.internal.ads.z8;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import w1.C0837a;
import w1.C0841e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class F extends y8 implements G {
    public F() {
        super("com.google.android.gms.ads.internal.client.IAdLoaderBuilder");
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        yd x8Var;
        sd sdVar = null;
        switch (i4) {
            case 1:
                D b4 = b();
                parcel2.writeNoException();
                z8.e(parcel2, b4);
                return true;
            case 2:
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder != null) {
                    sd queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdListener");
                    if (queryLocalInterface instanceof InterfaceC0139x) {
                        sdVar = (InterfaceC0139x) queryLocalInterface;
                    } else {
                        sdVar = new C0135v(readStrongBinder);
                    }
                }
                z8.b(parcel);
                W2(sdVar);
                parcel2.writeNoException();
                return true;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                IBinder readStrongBinder2 = parcel.readStrongBinder();
                if (readStrongBinder2 != null) {
                    sd queryLocalInterface2 = readStrongBinder2.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.IOnAppInstallAdLoadedListener");
                    if (queryLocalInterface2 instanceof sd) {
                        sdVar = queryLocalInterface2;
                    } else {
                        sdVar = new x8(readStrongBinder2, "com.google.android.gms.ads.internal.formats.client.IOnAppInstallAdLoadedListener");
                    }
                }
                z8.b(parcel);
                Y2(sdVar);
                parcel2.writeNoException();
                return true;
            case 4:
                IBinder readStrongBinder3 = parcel.readStrongBinder();
                if (readStrongBinder3 != null) {
                    sd queryLocalInterface3 = readStrongBinder3.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.IOnContentAdLoadedListener");
                    if (queryLocalInterface3 instanceof ud) {
                        sdVar = (ud) queryLocalInterface3;
                    } else {
                        sdVar = new x8(readStrongBinder3, "com.google.android.gms.ads.internal.formats.client.IOnContentAdLoadedListener");
                    }
                }
                z8.b(parcel);
                k4(sdVar);
                parcel2.writeNoException();
                return true;
            case 5:
                String readString = parcel.readString();
                IBinder readStrongBinder4 = parcel.readStrongBinder();
                if (readStrongBinder4 == null) {
                    x8Var = null;
                } else {
                    yd queryLocalInterface4 = readStrongBinder4.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.IOnCustomTemplateAdLoadedListener");
                    if (queryLocalInterface4 instanceof yd) {
                        x8Var = queryLocalInterface4;
                    } else {
                        x8Var = new x8(readStrongBinder4, "com.google.android.gms.ads.internal.formats.client.IOnCustomTemplateAdLoadedListener");
                    }
                }
                IBinder readStrongBinder5 = parcel.readStrongBinder();
                if (readStrongBinder5 != null) {
                    sd queryLocalInterface5 = readStrongBinder5.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.IOnCustomClickListener");
                    if (queryLocalInterface5 instanceof wd) {
                        sdVar = (wd) queryLocalInterface5;
                    } else {
                        sdVar = new x8(readStrongBinder5, "com.google.android.gms.ads.internal.formats.client.IOnCustomClickListener");
                    }
                }
                z8.b(parcel);
                W3(readString, x8Var, sdVar);
                parcel2.writeNoException();
                return true;
            case 6:
                z8.b(parcel);
                O0(z8.a(parcel, Qc.CREATOR));
                parcel2.writeNoException();
                return true;
            case 7:
                IBinder readStrongBinder6 = parcel.readStrongBinder();
                if (readStrongBinder6 != null) {
                    sd queryLocalInterface6 = readStrongBinder6.queryLocalInterface("com.google.android.gms.ads.internal.client.ICorrelationIdProvider");
                    if (queryLocalInterface6 instanceof W) {
                        sdVar = (W) queryLocalInterface6;
                    } else {
                        sdVar = new W(readStrongBinder6);
                    }
                }
                z8.b(parcel);
                V2(sdVar);
                parcel2.writeNoException();
                return true;
            case 8:
                IBinder readStrongBinder7 = parcel.readStrongBinder();
                if (readStrongBinder7 != null) {
                    sd queryLocalInterface7 = readStrongBinder7.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.IOnPublisherAdViewLoadedListener");
                    if (queryLocalInterface7 instanceof Bd) {
                        sdVar = (Bd) queryLocalInterface7;
                    } else {
                        sdVar = new x8(readStrongBinder7, "com.google.android.gms.ads.internal.formats.client.IOnPublisherAdViewLoadedListener");
                    }
                }
                z8.b(parcel);
                M3(sdVar, (C1) z8.a(parcel, C1.CREATOR));
                parcel2.writeNoException();
                return true;
            case 9:
                z8.b(parcel);
                y4((C0841e) z8.a(parcel, C0841e.CREATOR));
                parcel2.writeNoException();
                return true;
            case 10:
                IBinder readStrongBinder8 = parcel.readStrongBinder();
                if (readStrongBinder8 != null) {
                    sd queryLocalInterface8 = readStrongBinder8.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.IOnUnifiedNativeAdLoadedListener");
                    if (queryLocalInterface8 instanceof Ed) {
                        sdVar = (Ed) queryLocalInterface8;
                    } else {
                        sdVar = new x8(readStrongBinder8, "com.google.android.gms.ads.internal.formats.client.IOnUnifiedNativeAdLoadedListener");
                    }
                }
                z8.b(parcel);
                i4(sdVar);
                parcel2.writeNoException();
                return true;
            case 11:
            case 12:
            default:
                return false;
            case 13:
                z8.b(parcel);
                X0(z8.a(parcel, df.CREATOR));
                parcel2.writeNoException();
                return true;
            case 14:
                IBinder readStrongBinder9 = parcel.readStrongBinder();
                if (readStrongBinder9 != null) {
                    sd queryLocalInterface9 = readStrongBinder9.queryLocalInterface("com.google.android.gms.ads.internal.instream.client.IInstreamAdLoadCallback");
                    if (queryLocalInterface9 instanceof kf) {
                        sdVar = (kf) queryLocalInterface9;
                    } else {
                        sdVar = new x8(readStrongBinder9, "com.google.android.gms.ads.internal.instream.client.IInstreamAdLoadCallback");
                    }
                }
                z8.b(parcel);
                i1(sdVar);
                parcel2.writeNoException();
                return true;
            case 15:
                z8.b(parcel);
                E3((C0837a) z8.a(parcel, C0837a.CREATOR));
                parcel2.writeNoException();
                return true;
        }
    }
}
