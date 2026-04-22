package A1;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;

/* renamed from: A1.v  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0135v extends x8 implements InterfaceC0139x {
    public C0135v(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IAdListener");
    }

    @Override // A1.InterfaceC0139x
    public final void f() {
        p0(B(), 4);
    }

    @Override // A1.InterfaceC0139x
    public final void g() {
        p0(B(), 3);
    }

    @Override // A1.InterfaceC0139x
    public final void h() {
        p0(B(), 7);
    }

    @Override // A1.InterfaceC0139x
    public final void i() {
        p0(B(), 1);
    }

    @Override // A1.InterfaceC0139x
    public final void j() {
        p0(B(), 5);
    }

    @Override // A1.InterfaceC0139x
    public final void k() {
        p0(B(), 9);
    }

    @Override // A1.InterfaceC0139x
    public final void r() {
        p0(B(), 6);
    }

    @Override // A1.InterfaceC0139x
    public final void s(N0 n02) {
        Parcel B4 = B();
        z8.c(B4, n02);
        p0(B4, 8);
    }

    @Override // A1.InterfaceC0139x
    public final void w(int i4) {
        Parcel B4 = B();
        B4.writeInt(i4);
        p0(B4, 2);
    }
}
