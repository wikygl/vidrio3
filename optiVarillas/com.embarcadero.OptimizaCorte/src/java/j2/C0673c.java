package j2;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* renamed from: j2.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0673c implements InterfaceC0675e, IInterface {

    /* renamed from: j  reason: collision with root package name */
    public final IBinder f4794j;

    public C0673c(IBinder iBinder) {
        this.f4794j = iBinder;
    }

    @Override // j2.InterfaceC0675e
    public final void A0(Bundle bundle, long j4) {
        Parcel B4 = B();
        C0671a.a(B4, bundle);
        B4.writeLong(j4);
        Z(B4, 8);
    }

    public final Parcel B() {
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken("com.google.android.gms.measurement.api.internal.IAppMeasurementDynamiteService");
        return obtain;
    }

    @Override // j2.InterfaceC0675e
    public final void B3(String str, long j4) {
        Parcel B4 = B();
        B4.writeString(str);
        B4.writeLong(j4);
        Z(B4, 24);
    }

    @Override // j2.InterfaceC0675e
    public final void D0(String str, String str2, c2.b bVar, boolean z4, long j4) {
        Parcel B4 = B();
        B4.writeString(str);
        B4.writeString(str2);
        C0671a.b(B4, bVar);
        B4.writeInt(z4 ? 1 : 0);
        B4.writeLong(j4);
        Z(B4, 4);
    }

    @Override // j2.InterfaceC0675e
    public final void E1(String str, c2.b bVar, c2.b bVar2, c2.b bVar3) {
        Parcel B4 = B();
        B4.writeInt(5);
        B4.writeString(str);
        C0671a.b(B4, bVar);
        C0671a.b(B4, bVar2);
        C0671a.b(B4, bVar3);
        Z(B4, 33);
    }

    @Override // j2.InterfaceC0675e
    public final void G1(InterfaceC0677g interfaceC0677g) {
        Parcel B4 = B();
        C0671a.b(B4, interfaceC0677g);
        Z(B4, 16);
    }

    @Override // j2.InterfaceC0675e
    public final void G2(c2.b bVar, InterfaceC0677g interfaceC0677g, long j4) {
        Parcel B4 = B();
        C0671a.b(B4, bVar);
        C0671a.b(B4, interfaceC0677g);
        B4.writeLong(j4);
        Z(B4, 31);
    }

    @Override // j2.InterfaceC0675e
    public final void H0(String str, long j4) {
        Parcel B4 = B();
        B4.writeString(str);
        B4.writeLong(j4);
        Z(B4, 23);
    }

    @Override // j2.InterfaceC0675e
    public final void L0(String str, InterfaceC0677g interfaceC0677g) {
        Parcel B4 = B();
        B4.writeString(str);
        C0671a.b(B4, interfaceC0677g);
        Z(B4, 6);
    }

    @Override // j2.InterfaceC0675e
    public final void Q3(c2.b bVar, long j4) {
        Parcel B4 = B();
        C0671a.b(B4, bVar);
        B4.writeLong(j4);
        Z(B4, 28);
    }

    @Override // j2.InterfaceC0675e
    public final void S0(InterfaceC0677g interfaceC0677g) {
        Parcel B4 = B();
        C0671a.b(B4, interfaceC0677g);
        Z(B4, 19);
    }

    @Override // j2.InterfaceC0675e
    public final void T2(Bundle bundle, long j4) {
        Parcel B4 = B();
        C0671a.a(B4, bundle);
        B4.writeLong(j4);
        Z(B4, 44);
    }

    @Override // j2.InterfaceC0675e
    public final void W1(String str, String str2, InterfaceC0677g interfaceC0677g) {
        Parcel B4 = B();
        B4.writeString(str);
        B4.writeString(str2);
        C0671a.b(B4, interfaceC0677g);
        Z(B4, 10);
    }

    public final void Z(Parcel parcel, int i4) {
        Parcel obtain = Parcel.obtain();
        try {
            this.f4794j.transact(i4, parcel, obtain, 0);
            obtain.readException();
        } finally {
            parcel.recycle();
            obtain.recycle();
        }
    }

    @Override // j2.InterfaceC0675e
    public final void a3(InterfaceC0677g interfaceC0677g) {
        Parcel B4 = B();
        C0671a.b(B4, interfaceC0677g);
        Z(B4, 17);
    }

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this.f4794j;
    }

    @Override // j2.InterfaceC0675e
    public final void b3(String str, String str2, boolean z4, InterfaceC0677g interfaceC0677g) {
        Parcel B4 = B();
        B4.writeString(str);
        B4.writeString(str2);
        int i4 = C0671a.f4791a;
        B4.writeInt(z4 ? 1 : 0);
        C0671a.b(B4, interfaceC0677g);
        Z(B4, 5);
    }

    @Override // j2.InterfaceC0675e
    public final void c1(c2.b bVar, long j4) {
        Parcel B4 = B();
        C0671a.b(B4, bVar);
        B4.writeLong(j4);
        Z(B4, 30);
    }

    @Override // j2.InterfaceC0675e
    public final void c3(c2.b bVar, long j4) {
        Parcel B4 = B();
        C0671a.b(B4, bVar);
        B4.writeLong(j4);
        Z(B4, 29);
    }

    @Override // j2.InterfaceC0675e
    public final void d3(c2.b bVar, h hVar, long j4) {
        Parcel B4 = B();
        C0671a.b(B4, bVar);
        C0671a.a(B4, hVar);
        B4.writeLong(j4);
        Z(B4, 1);
    }

    @Override // j2.InterfaceC0675e
    public final void i2(String str, String str2, Bundle bundle, boolean z4, boolean z5, long j4) {
        Parcel B4 = B();
        B4.writeString(str);
        B4.writeString(str2);
        C0671a.a(B4, bundle);
        B4.writeInt(z4 ? 1 : 0);
        B4.writeInt(z5 ? 1 : 0);
        B4.writeLong(j4);
        Z(B4, 2);
    }

    @Override // j2.InterfaceC0675e
    public final void j2(c2.b bVar, String str, String str2, long j4) {
        Parcel B4 = B();
        C0671a.b(B4, bVar);
        B4.writeString(str);
        B4.writeString(str2);
        B4.writeLong(j4);
        Z(B4, 15);
    }

    @Override // j2.InterfaceC0675e
    public final void m4(c2.b bVar, long j4) {
        Parcel B4 = B();
        C0671a.b(B4, bVar);
        B4.writeLong(j4);
        Z(B4, 25);
    }

    @Override // j2.InterfaceC0675e
    public final void n3(Bundle bundle, InterfaceC0677g interfaceC0677g, long j4) {
        Parcel B4 = B();
        C0671a.a(B4, bundle);
        C0671a.b(B4, interfaceC0677g);
        B4.writeLong(j4);
        Z(B4, 32);
    }

    @Override // j2.InterfaceC0675e
    public final void q3(InterfaceC0677g interfaceC0677g) {
        Parcel B4 = B();
        C0671a.b(B4, interfaceC0677g);
        Z(B4, 21);
    }

    @Override // j2.InterfaceC0675e
    public final void r3(InterfaceC0677g interfaceC0677g) {
        Parcel B4 = B();
        C0671a.b(B4, interfaceC0677g);
        Z(B4, 22);
    }

    @Override // j2.InterfaceC0675e
    public final void r4(c2.b bVar, Bundle bundle, long j4) {
        Parcel B4 = B();
        C0671a.b(B4, bVar);
        C0671a.a(B4, bundle);
        B4.writeLong(j4);
        Z(B4, 27);
    }

    @Override // j2.InterfaceC0675e
    public final void w4(c2.b bVar, long j4) {
        Parcel B4 = B();
        C0671a.b(B4, bVar);
        B4.writeLong(j4);
        Z(B4, 26);
    }

    @Override // j2.InterfaceC0675e
    public final void x1(String str, String str2, Bundle bundle) {
        Parcel B4 = B();
        B4.writeString(str);
        B4.writeString(str2);
        C0671a.a(B4, bundle);
        Z(B4, 9);
    }
}
