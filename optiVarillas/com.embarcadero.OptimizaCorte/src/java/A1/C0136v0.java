package A1;

import android.os.IBinder;
import android.os.Parcel;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.v0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0136v0 extends x8 implements InterfaceC0140x0 {
    public C0136v0(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IOutOfContextTester");
    }

    @Override // A1.InterfaceC0140x0
    public final void e3(String str, InterfaceC0374a interfaceC0374a, InterfaceC0374a interfaceC0374a2) {
        Parcel B4 = B();
        B4.writeString(str);
        z8.e(B4, interfaceC0374a);
        z8.e(B4, interfaceC0374a2);
        p0(B4, 1);
    }
}
