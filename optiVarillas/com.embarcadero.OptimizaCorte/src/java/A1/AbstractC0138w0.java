package A1;

import android.os.Parcel;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.Xw;
import com.google.android.gms.internal.ads.y8;
import e0.C0405a;

/* renamed from: A1.w0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0138w0 extends y8 implements InterfaceC0140x0 {
    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 == 1) {
            ((Xw) this).e3(parcel.readString(), InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder()), C0405a.b(parcel, parcel));
            parcel2.writeNoException();
            return true;
        }
        return false;
    }
}
