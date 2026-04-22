package A1;

import android.os.Parcel;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class Y extends y8 implements Z {
    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 3) {
                    if (i4 != 4 && i4 != 5) {
                        return false;
                    }
                } else {
                    ((C0129s) this).r();
                }
            } else {
                ((C0129s) this).d();
            }
        } else {
            z8.b(parcel);
            ((C0129s) this).c0((N0) z8.a(parcel, N0.CREATOR));
        }
        parcel2.writeNoException();
        return true;
    }
}
