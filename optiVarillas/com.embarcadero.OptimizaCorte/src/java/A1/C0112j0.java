package A1;

import android.os.Parcel;
import com.google.android.gms.internal.ads.x8;

/* renamed from: A1.j0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0112j0 extends x8 implements InterfaceC0115k0 {
    @Override // A1.InterfaceC0115k0
    public final String b() {
        Parcel Z3 = Z(B(), 1);
        String readString = Z3.readString();
        Z3.recycle();
        return readString;
    }

    @Override // A1.InterfaceC0115k0
    public final String d() {
        Parcel Z3 = Z(B(), 2);
        String readString = Z3.readString();
        Z3.recycle();
        return readString;
    }
}
