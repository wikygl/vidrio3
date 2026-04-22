package A1;

import android.os.Parcel;
import com.google.android.gms.internal.ads.dg;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.a0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0085a0 extends x8 implements InterfaceC0091c0 {
    @Override // A1.InterfaceC0091c0
    public final eg getAdapterCreator() {
        Parcel Z3 = Z(B(), 2);
        eg C4 = dg.C4(Z3.readStrongBinder());
        Z3.recycle();
        return C4;
    }

    @Override // A1.InterfaceC0091c0
    public final X0 getLiteSdkVersion() {
        Parcel Z3 = Z(B(), 1);
        X0 x02 = (X0) z8.a(Z3, X0.CREATOR);
        Z3.recycle();
        return x02;
    }
}
