package A1;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.l9;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class J extends x8 implements L {
    public J(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IAdManager");
    }

    @Override // A1.L
    public final void B1(InterfaceC0133u interfaceC0133u) {
        Parcel B4 = B();
        z8.e(B4, interfaceC0133u);
        p0(B4, 20);
    }

    @Override // A1.L
    public final void E0(InterfaceC0374a interfaceC0374a) {
        Parcel B4 = B();
        z8.e(B4, interfaceC0374a);
        p0(B4, 44);
    }

    @Override // A1.L
    public final void I() {
        p0(B(), 2);
    }

    @Override // A1.L
    public final void J3(y1 y1Var, A a4) {
        Parcel B4 = B();
        z8.c(B4, y1Var);
        z8.e(B4, a4);
        p0(B4, 43);
    }

    @Override // A1.L
    public final void M() {
        p0(B(), 6);
    }

    @Override // A1.L
    public final void M0(C1 c12) {
        Parcel B4 = B();
        z8.c(B4, c12);
        p0(B4, 13);
    }

    @Override // A1.L
    public final void M2(boolean z4) {
        Parcel B4 = B();
        ClassLoader classLoader = z8.a;
        B4.writeInt(z4 ? 1 : 0);
        p0(B4, 34);
    }

    @Override // A1.L
    public final void N3(S s4) {
        Parcel B4 = B();
        z8.e(B4, s4);
        p0(B4, 8);
    }

    @Override // A1.L
    public final boolean V1(y1 y1Var) {
        boolean z4;
        Parcel B4 = B();
        z8.c(B4, y1Var);
        Parcel Z3 = Z(B4, 4);
        if (Z3.readInt() != 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        Z3.recycle();
        return z4;
    }

    @Override // A1.L
    public final void X1(InterfaceC0134u0 interfaceC0134u0) {
        Parcel B4 = B();
        z8.e(B4, interfaceC0134u0);
        p0(B4, 42);
    }

    @Override // A1.L
    public final void Y() {
        p0(B(), 5);
    }

    @Override // A1.L
    public final void Y0(Z z4) {
        Parcel B4 = B();
        z8.e(B4, z4);
        p0(B4, 45);
    }

    @Override // A1.L
    public final void b2(l9 l9Var) {
        Parcel B4 = B();
        z8.e(B4, l9Var);
        p0(B4, 40);
    }

    @Override // A1.L
    public final C1 h() {
        Parcel Z3 = Z(B(), 12);
        C1 c12 = (C1) z8.a(Z3, C1.CREATOR);
        Z3.recycle();
        return c12;
    }

    @Override // A1.L
    public final A0 k() {
        A0 c0144z0;
        Parcel Z3 = Z(B(), 41);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            c0144z0 = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IResponseInfo");
            if (queryLocalInterface instanceof A0) {
                c0144z0 = (A0) queryLocalInterface;
            } else {
                c0144z0 = new C0144z0(readStrongBinder);
            }
        }
        Z3.recycle();
        return c0144z0;
    }

    @Override // A1.L
    public final InterfaceC0374a l() {
        return I.b(Z(B(), 1));
    }

    @Override // A1.L
    public final D0 m() {
        D0 b02;
        Parcel Z3 = Z(B(), 26);
        IBinder readStrongBinder = Z3.readStrongBinder();
        if (readStrongBinder == null) {
            b02 = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IVideoController");
            if (queryLocalInterface instanceof D0) {
                b02 = (D0) queryLocalInterface;
            } else {
                b02 = new B0(readStrongBinder);
            }
        }
        Z3.recycle();
        return b02;
    }

    @Override // A1.L
    public final void m1(I1 i12) {
        Parcel B4 = B();
        z8.c(B4, i12);
        p0(B4, 39);
    }

    @Override // A1.L
    public final void p1(InterfaceC0139x interfaceC0139x) {
        Parcel B4 = B();
        z8.e(B4, interfaceC0139x);
        p0(B4, 7);
    }

    @Override // A1.L
    public final void p4(boolean z4) {
        Parcel B4 = B();
        ClassLoader classLoader = z8.a;
        B4.writeInt(z4 ? 1 : 0);
        p0(B4, 22);
    }

    @Override // A1.L
    public final String t() {
        Parcel Z3 = Z(B(), 31);
        String readString = Z3.readString();
        Z3.recycle();
        return readString;
    }

    @Override // A1.L
    public final void v0(s1 s1Var) {
        Parcel B4 = B();
        z8.c(B4, s1Var);
        p0(B4, 29);
    }
}
