package d2;

import A1.I;
import android.os.Parcel;
import c2.InterfaceC0374a;
import h2.C0438a;
import h2.C0440c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class i extends C0438a {
    public final InterfaceC0374a L1(c2.b bVar, String str, int i4, c2.b bVar2) {
        Parcel Z3 = Z();
        C0440c.c(Z3, bVar);
        Z3.writeString(str);
        Z3.writeInt(i4);
        C0440c.c(Z3, bVar2);
        return I.b(B(Z3, 3));
    }

    public final InterfaceC0374a p0(c2.b bVar, String str, int i4, c2.b bVar2) {
        Parcel Z3 = Z();
        C0440c.c(Z3, bVar);
        Z3.writeString(str);
        Z3.writeInt(i4);
        C0440c.c(Z3, bVar2);
        return I.b(B(Z3, 2));
    }
}
