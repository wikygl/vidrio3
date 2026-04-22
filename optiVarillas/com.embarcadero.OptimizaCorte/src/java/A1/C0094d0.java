package A1;

import android.os.IBinder;
import android.os.Parcel;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.Te;
import com.google.android.gms.internal.ads.af;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;
import java.util.ArrayList;
import java.util.List;

/* renamed from: A1.d0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0094d0 extends x8 implements InterfaceC0100f0 {
    public C0094d0(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IMobileAdsSettingManager");
    }

    @Override // A1.InterfaceC0100f0
    public final void F0(String str) {
        Parcel B4 = B();
        B4.writeString(str);
        p0(B4, 18);
    }

    @Override // A1.InterfaceC0100f0
    public final void R1(n1 n1Var) {
        Parcel B4 = B();
        z8.c(B4, n1Var);
        p0(B4, 14);
    }

    @Override // A1.InterfaceC0100f0
    public final void W0(af afVar) {
        Parcel B4 = B();
        z8.e(B4, afVar);
        p0(B4, 12);
    }

    @Override // A1.InterfaceC0100f0
    public final List h() {
        Parcel Z3 = Z(B(), 13);
        ArrayList createTypedArrayList = Z3.createTypedArrayList(Te.CREATOR);
        Z3.recycle();
        return createTypedArrayList;
    }

    @Override // A1.InterfaceC0100f0
    public final void k() {
        p0(B(), 1);
    }

    @Override // A1.InterfaceC0100f0
    public final void u3(eg egVar) {
        Parcel B4 = B();
        z8.e(B4, egVar);
        p0(B4, 11);
    }

    @Override // A1.InterfaceC0100f0
    public final void x2(InterfaceC0374a interfaceC0374a, String str) {
        Parcel B4 = B();
        B4.writeString(null);
        z8.e(B4, interfaceC0374a);
        p0(B4, 6);
    }
}
