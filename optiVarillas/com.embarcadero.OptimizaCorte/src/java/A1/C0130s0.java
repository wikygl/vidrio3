package A1;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.s0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0130s0 extends x8 implements InterfaceC0134u0 {
    public C0130s0(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IOnPaidEventListener");
    }

    @Override // A1.InterfaceC0134u0
    public final void T3(E1 e12) {
        Parcel B4 = B();
        z8.c(B4, e12);
        p0(B4, 1);
    }

    @Override // A1.InterfaceC0134u0
    public final boolean d() {
        boolean z4;
        Parcel Z3 = Z(B(), 2);
        ClassLoader classLoader = z8.a;
        if (Z3.readInt() != 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        Z3.recycle();
        return z4;
    }
}
