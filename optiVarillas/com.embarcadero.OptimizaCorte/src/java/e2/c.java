package e2;

import android.os.Parcel;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class c extends C0408a implements e {
    @Override // e2.e
    public final boolean b() {
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
        int i4 = b.f3357a;
        boolean z4 = true;
        obtain.writeInt(1);
        Parcel B4 = B(obtain, 2);
        if (B4.readInt() == 0) {
            z4 = false;
        }
        B4.recycle();
        return z4;
    }

    @Override // e2.e
    public final String e() {
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
        Parcel B4 = B(obtain, 1);
        String readString = B4.readString();
        B4.recycle();
        return readString;
    }

    @Override // e2.e
    public final boolean i() {
        boolean z4;
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
        Parcel B4 = B(obtain, 6);
        int i4 = b.f3357a;
        if (B4.readInt() != 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        B4.recycle();
        return z4;
    }
}
