package D1;

import android.os.Parcel;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class I extends y8 implements J {
    public I() {
        super("com.google.android.gms.ads.internal.util.IWorkManagerUtil");
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 3) {
                    return false;
                }
                z8.b(parcel);
                boolean zzg = zzg(InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder()), (B1.a) z8.a(parcel, B1.a.CREATOR));
                parcel2.writeNoException();
                parcel2.writeInt(zzg ? 1 : 0);
            } else {
                InterfaceC0374a Z3 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
                z8.b(parcel);
                zze(Z3);
                parcel2.writeNoException();
            }
        } else {
            InterfaceC0374a Z4 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
            String readString = parcel.readString();
            String readString2 = parcel.readString();
            z8.b(parcel);
            boolean zzf = zzf(Z4, readString, readString2);
            parcel2.writeNoException();
            parcel2.writeInt(zzf ? 1 : 0);
        }
        return true;
    }
}
