package A1;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class B0 extends x8 implements D0 {
    public B0(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IVideoController");
    }

    @Override // A1.D0
    public final float b() {
        throw null;
    }

    @Override // A1.D0
    public final float d() {
        throw null;
    }

    @Override // A1.D0
    public final G0 f() {
        G0 e02;
        Parcel Z3 = Z(B(), 11);
        IBinder readStrongBinder = Z3.readStrongBinder();
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
        Z3.recycle();
        return e02;
    }

    @Override // A1.D0
    public final float h() {
        throw null;
    }

    @Override // A1.D0
    public final void h1(G0 g02) {
        Parcel B4 = B();
        z8.e(B4, g02);
        p0(B4, 8);
    }
}
