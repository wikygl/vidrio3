package A1;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.af;
import com.google.android.gms.internal.ads.dg;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;
import java.util.List;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: A1.e0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0097e0 extends y8 implements InterfaceC0100f0 {
    public AbstractC0097e0() {
        super("com.google.android.gms.ads.internal.client.IMobileAdsSettingManager");
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        af afVar = null;
        switch (i4) {
            case 1:
                k();
                parcel2.writeNoException();
                return true;
            case 2:
                float readFloat = parcel.readFloat();
                z8.b(parcel);
                C0(readFloat);
                parcel2.writeNoException();
                return true;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                String readString = parcel.readString();
                z8.b(parcel);
                I2(readString);
                parcel2.writeNoException();
                return true;
            case 4:
                boolean f = z8.f(parcel);
                z8.b(parcel);
                u4(f);
                parcel2.writeNoException();
                return true;
            case 5:
                InterfaceC0374a Z3 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                String readString2 = parcel.readString();
                z8.b(parcel);
                D2(Z3, readString2);
                parcel2.writeNoException();
                return true;
            case 6:
                String readString3 = parcel.readString();
                InterfaceC0374a Z4 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                z8.b(parcel);
                x2(Z4, readString3);
                parcel2.writeNoException();
                return true;
            case 7:
                float b4 = b();
                parcel2.writeNoException();
                parcel2.writeFloat(b4);
                return true;
            case 8:
                boolean y4 = y();
                parcel2.writeNoException();
                ClassLoader classLoader = z8.a;
                parcel2.writeInt(y4 ? 1 : 0);
                return true;
            case 9:
                String d4 = d();
                parcel2.writeNoException();
                parcel2.writeString(d4);
                return true;
            case 10:
                String readString4 = parcel.readString();
                z8.b(parcel);
                T(readString4);
                parcel2.writeNoException();
                return true;
            case 11:
                eg C4 = dg.C4(parcel.readStrongBinder());
                z8.b(parcel);
                u3(C4);
                parcel2.writeNoException();
                return true;
            case 12:
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder != null) {
                    IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.initialization.IInitializationCallback");
                    if (queryLocalInterface instanceof af) {
                        afVar = (af) queryLocalInterface;
                    } else {
                        afVar = new x8(readStrongBinder, "com.google.android.gms.ads.internal.initialization.IInitializationCallback");
                    }
                }
                z8.b(parcel);
                W0(afVar);
                parcel2.writeNoException();
                return true;
            case 13:
                List h4 = h();
                parcel2.writeNoException();
                parcel2.writeTypedList(h4);
                return true;
            case 14:
                z8.b(parcel);
                R1((n1) z8.a(parcel, n1.CREATOR));
                parcel2.writeNoException();
                return true;
            case 15:
                f();
                parcel2.writeNoException();
                return true;
            case 16:
                IBinder readStrongBinder2 = parcel.readStrongBinder();
                if (readStrongBinder2 != null) {
                    IInterface queryLocalInterface2 = readStrongBinder2.queryLocalInterface("com.google.android.gms.ads.internal.client.IOnAdInspectorClosedListener");
                    if (queryLocalInterface2 instanceof InterfaceC0125p0) {
                        afVar = (InterfaceC0125p0) queryLocalInterface2;
                    } else {
                        afVar = new x8(readStrongBinder2, "com.google.android.gms.ads.internal.client.IOnAdInspectorClosedListener");
                    }
                }
                z8.b(parcel);
                z3(afVar);
                parcel2.writeNoException();
                return true;
            case 17:
                boolean f4 = z8.f(parcel);
                z8.b(parcel);
                f0(f4);
                parcel2.writeNoException();
                return true;
            case 18:
                String readString5 = parcel.readString();
                z8.b(parcel);
                F0(readString5);
                parcel2.writeNoException();
                return true;
            default:
                return false;
        }
    }
}
