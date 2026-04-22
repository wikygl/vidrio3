package m0;

import android.os.IBinder;
import android.os.Parcel;

/* renamed from: m0.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0727c implements InterfaceC0728d {

    /* renamed from: j  reason: collision with root package name */
    public IBinder f5287j;

    @Override // m0.InterfaceC0728d
    public final void P1(String[] strArr) {
        Parcel obtain = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("androidx.room.IMultiInstanceInvalidationCallback");
            obtain.writeStringArray(strArr);
            this.f5287j.transact(1, obtain, null, 1);
        } finally {
            obtain.recycle();
        }
    }

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this.f5287j;
    }
}
