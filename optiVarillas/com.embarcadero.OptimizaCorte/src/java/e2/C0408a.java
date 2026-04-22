package e2;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* renamed from: e2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class C0408a implements IInterface {

    /* renamed from: j  reason: collision with root package name */
    public final IBinder f3356j;

    public C0408a(IBinder iBinder) {
        this.f3356j = iBinder;
    }

    public final Parcel B(Parcel parcel, int i4) {
        Parcel obtain = Parcel.obtain();
        try {
            try {
                this.f3356j.transact(i4, parcel, obtain, 0);
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

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this.f3356j;
    }
}
