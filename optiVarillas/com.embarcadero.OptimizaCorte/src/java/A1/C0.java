package A1;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class C0 extends y8 implements D0 {
    public C0() {
        super("com.google.android.gms.ads.internal.client.IVideoController");
    }

    public static D0 C4(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IVideoController");
        if (queryLocalInterface instanceof D0) {
            return (D0) queryLocalInterface;
        }
        return new B0(iBinder);
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        G0 e02;
        switch (i4) {
            case 1:
                m();
                parcel2.writeNoException();
                return true;
            case 2:
                k();
                parcel2.writeNoException();
                return true;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                boolean f = z8.f(parcel);
                z8.b(parcel);
                f0(f);
                parcel2.writeNoException();
                return true;
            case 4:
                boolean q4 = q();
                parcel2.writeNoException();
                ClassLoader classLoader = z8.a;
                parcel2.writeInt(q4 ? 1 : 0);
                return true;
            case 5:
                int g4 = g();
                parcel2.writeNoException();
                parcel2.writeInt(g4);
                return true;
            case 6:
                float h4 = h();
                parcel2.writeNoException();
                parcel2.writeFloat(h4);
                return true;
            case 7:
                float d4 = d();
                parcel2.writeNoException();
                parcel2.writeFloat(d4);
                return true;
            case 8:
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder == null) {
                    e02 = null;
                } else {
                    IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IVideoLifecycleCallbacks");
                    if (queryLocalInterface instanceof G0) {
                        e02 = (G0) queryLocalInterface;
                    } else {
                        e02 = new E0(readStrongBinder);
                    }
                }
                z8.b(parcel);
                h1(e02);
                parcel2.writeNoException();
                return true;
            case 9:
                float b4 = b();
                parcel2.writeNoException();
                parcel2.writeFloat(b4);
                return true;
            case 10:
                boolean p4 = p();
                parcel2.writeNoException();
                ClassLoader classLoader2 = z8.a;
                parcel2.writeInt(p4 ? 1 : 0);
                return true;
            case 11:
                G0 f4 = f();
                parcel2.writeNoException();
                z8.e(parcel2, f4);
                return true;
            case 12:
                boolean n4 = n();
                parcel2.writeNoException();
                ClassLoader classLoader3 = z8.a;
                parcel2.writeInt(n4 ? 1 : 0);
                return true;
            case 13:
                l();
                parcel2.writeNoException();
                return true;
            default:
                return false;
        }
    }
}
