package h2;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* renamed from: h2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class C0438a implements IInterface {

    /* renamed from: j  reason: collision with root package name */
    public final IBinder f3584j;

    /* renamed from: k  reason: collision with root package name */
    public final String f3585k;

    public C0438a(IBinder iBinder, String str) {
        this.f3584j = iBinder;
        this.f3585k = str;
    }

    public final Parcel B(Parcel parcel, int i4) {
        Parcel obtain = Parcel.obtain();
        try {
            try {
                this.f3584j.transact(i4, parcel, obtain, 0);
                obtain.readException();
                return obtain;
            } catch (RuntimeException e4) {
                obtain.recycle();
                throw e4;
            }
        } finally {
            parcel.recycle();
        }
    }

    public final Parcel Z() {
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken(this.f3585k);
        return obtain;
    }

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this.f3584j;
    }
}
