package A1;

import android.os.Parcel;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class F0 extends y8 implements G0 {
    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 3) {
                    if (i4 != 4) {
                        if (i4 != 5) {
                            return false;
                        }
                        boolean f = z8.f(parcel);
                        z8.b(parcel);
                        ((r1) this).Q2(f);
                    } else {
                        ((r1) this).b();
                    }
                } else {
                    ((r1) this).h();
                }
            } else {
                ((r1) this).g();
            }
        } else {
            ((r1) this).f();
        }
        parcel2.writeNoException();
        return true;
    }
}
