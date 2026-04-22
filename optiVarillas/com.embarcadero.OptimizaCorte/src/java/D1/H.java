package D1;

import android.os.Parcel;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class H extends x8 implements J {
    @Override // D1.J
    public final void zze(InterfaceC0374a interfaceC0374a) {
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        p0(B4, 2);
    }

    @Override // D1.J
    public final boolean zzf(InterfaceC0374a interfaceC0374a, String str, String str2) {
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        B4.writeString(str);
        B4.writeString(str2);
        boolean z4 = true;
        Parcel Z3 = Z(B4, 1);
        if (Z3.readInt() == 0) {
            z4 = false;
        }
        Z3.recycle();
        return z4;
    }

    @Override // D1.J
    public final boolean zzg(InterfaceC0374a interfaceC0374a, B1.a aVar) {
        boolean z4;
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        z8.c(B4, aVar);
        Parcel Z3 = Z(B4, 3);
        if (Z3.readInt() != 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        Z3.recycle();
        return z4;
    }
}
