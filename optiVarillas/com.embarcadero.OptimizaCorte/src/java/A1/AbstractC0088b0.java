package A1;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.b0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0088b0 extends y8 implements InterfaceC0091c0 {
    public AbstractC0088b0() {
        super("com.google.android.gms.ads.internal.client.ILiteSdkInfo");
    }

    public static InterfaceC0091c0 asInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.ILiteSdkInfo");
        if (queryLocalInterface instanceof InterfaceC0091c0) {
            return (InterfaceC0091c0) queryLocalInterface;
        }
        return new x8(iBinder, "com.google.android.gms.ads.internal.client.ILiteSdkInfo");
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 != 1) {
            if (i4 != 2) {
                return false;
            }
            eg adapterCreator = getAdapterCreator();
            parcel2.writeNoException();
            z8.e(parcel2, adapterCreator);
        } else {
            X0 liteSdkVersion = getLiteSdkVersion();
            parcel2.writeNoException();
            z8.d(parcel2, liteSdkVersion);
        }
        return true;
    }
}
