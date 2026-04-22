package A1;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.y0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0142y0 extends x8 {
    public final InterfaceC0140x0 g2(c2.b bVar, eg egVar) {
        InterfaceC0140x0 c0136v0;
        Parcel B4 = B();
        z8.e(B4, bVar);
        z8.e(B4, egVar);
        B4.writeInt(241199000);
        Parcel Z3 = Z(B4, 1);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            c0136v0 = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IOutOfContextTester");
            if (queryLocalInterface instanceof InterfaceC0140x0) {
                c0136v0 = (InterfaceC0140x0) queryLocalInterface;
            } else {
                c0136v0 = new C0136v0(readStrongBinder);
            }
        }
        Z3.recycle();
        return c0136v0;
    }
}
