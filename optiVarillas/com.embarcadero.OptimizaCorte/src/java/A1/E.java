package A1;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.internal.ads.Ed;
import com.google.android.gms.internal.ads.Qc;
import com.google.android.gms.internal.ads.wd;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.yd;
import com.google.android.gms.internal.ads.z8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class E extends x8 implements G {
    public E(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IAdLoaderBuilder");
    }

    @Override // A1.G
    public final void O0(Qc qc) {
        Parcel B4 = B();
        z8.c(B4, qc);
        p0(B4, 6);
    }

    @Override // A1.G
    public final void W2(InterfaceC0139x interfaceC0139x) {
        Parcel B4 = B();
        z8.e(B4, interfaceC0139x);
        p0(B4, 2);
    }

    @Override // A1.G
    public final void W3(String str, yd ydVar, wd wdVar) {
        Parcel B4 = B();
        B4.writeString(str);
        z8.e(B4, ydVar);
        z8.e(B4, wdVar);
        p0(B4, 5);
    }

    @Override // A1.G
    public final D b() {
        D x8Var;
        Parcel Z3 = Z(B(), 1);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            x8Var = null;
        } else {
            x8 queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdLoader");
            if (queryLocalInterface instanceof D) {
                x8Var = (D) queryLocalInterface;
            } else {
                x8Var = new x8(readStrongBinder, "com.google.android.gms.ads.internal.client.IAdLoader");
            }
        }
        Z3.recycle();
        return x8Var;
    }

    @Override // A1.G
    public final void i4(Ed ed) {
        Parcel B4 = B();
        z8.e(B4, ed);
        p0(B4, 10);
    }
}
