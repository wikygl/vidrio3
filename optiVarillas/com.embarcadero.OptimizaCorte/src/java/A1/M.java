package A1;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class M extends x8 {
    public M(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IAdManagerCreator");
    }

    public final IBinder g2(c2.b bVar, C1 c12, String str, eg egVar, int i4) {
        Parcel B4 = B();
        z8.e(B4, bVar);
        z8.c(B4, c12);
        B4.writeString(str);
        z8.e(B4, egVar);
        B4.writeInt(241199000);
        B4.writeInt(i4);
        Parcel Z3 = Z(B4, 2);
        IBinder readStrongBinder = Z3.readStrongBinder();
        Z3.recycle();
        return readStrongBinder;
    }
}
