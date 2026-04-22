package A1;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.t0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0132t0 extends y8 implements InterfaceC0134u0 {
    public static InterfaceC0134u0 C4(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IOnPaidEventListener");
        if (queryLocalInterface instanceof InterfaceC0134u0) {
            return (InterfaceC0134u0) queryLocalInterface;
        }
        return new C0130s0(iBinder);
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 != 1) {
            if (i4 != 2) {
                return false;
            }
            parcel2.writeNoException();
            ClassLoader classLoader = z8.a;
            parcel2.writeInt(1);
        } else {
            E1 e12 = (E1) z8.a(parcel, E1.CREATOR);
            z8.b(parcel);
            parcel2.writeNoException();
        }
        return true;
    }
}
