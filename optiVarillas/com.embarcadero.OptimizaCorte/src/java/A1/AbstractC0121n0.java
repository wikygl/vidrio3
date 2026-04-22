package A1;

import android.os.Parcel;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.n0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0121n0 extends y8 implements InterfaceC0125p0 {
    public AbstractC0121n0() {
        super("com.google.android.gms.ads.internal.client.IOnAdInspectorClosedListener");
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 == 1) {
            z8.b(parcel);
            Z2((N0) z8.a(parcel, N0.CREATOR));
            parcel2.writeNoException();
            return true;
        }
        return false;
    }
}
