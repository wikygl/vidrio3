package W1;

import android.os.IBinder;
import android.os.Parcel;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class H implements InterfaceC0321i {

    /* renamed from: j  reason: collision with root package name */
    public final IBinder f2643j;

    public H(IBinder iBinder) {
        this.f2643j = iBinder;
    }

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this.f2643j;
    }

    @Override // W1.InterfaceC0321i
    public final void l3(M m4, C0317e c0317e) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("com.google.android.gms.common.internal.IGmsServiceBroker");
            obtain.writeStrongBinder(m4);
            obtain.writeInt(1);
            U.a(c0317e, obtain, 0);
            this.f2643j.transact(46, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }
}
