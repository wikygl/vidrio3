package A1;

import android.os.Parcel;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.z  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0143z extends y8 implements A {
    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 != 1) {
            if (i4 != 2) {
                return false;
            }
            z8.b(parcel);
            ((v1) this).a4((N0) z8.a(parcel, N0.CREATOR));
        } else {
            ((v1) this).r();
        }
        parcel2.writeNoException();
        return true;
    }
}
