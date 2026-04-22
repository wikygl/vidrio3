package m0;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import androidx.room.MultiInstanceInvalidationService;

/* renamed from: m0.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractBinderC0729e extends Binder implements IInterface {
    /* JADX WARN: Type inference failed for: r1v2, types: [java.lang.Object, m0.c] */
    /* JADX WARN: Type inference failed for: r1v6, types: [java.lang.Object, m0.c] */
    @Override // android.os.Binder
    public final boolean onTransact(int i4, Parcel parcel, Parcel parcel2, int i5) {
        InterfaceC0728d interfaceC0728d = null;
        InterfaceC0728d interfaceC0728d2 = null;
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 3) {
                    if (i4 != 1598968902) {
                        return super.onTransact(i4, parcel, parcel2, i5);
                    }
                    parcel2.writeString("androidx.room.IMultiInstanceInvalidationService");
                    return true;
                }
                parcel.enforceInterface("androidx.room.IMultiInstanceInvalidationService");
                ((MultiInstanceInvalidationService.b) this).B(parcel.readInt(), parcel.createStringArray());
                return true;
            }
            parcel.enforceInterface("androidx.room.IMultiInstanceInvalidationService");
            IBinder readStrongBinder = parcel.readStrongBinder();
            if (readStrongBinder != null) {
                IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("androidx.room.IMultiInstanceInvalidationCallback");
                if (queryLocalInterface != null && (queryLocalInterface instanceof InterfaceC0728d)) {
                    interfaceC0728d2 = (InterfaceC0728d) queryLocalInterface;
                } else {
                    ?? obj = new Object();
                    obj.f5287j = readStrongBinder;
                    interfaceC0728d2 = obj;
                }
            }
            int readInt = parcel.readInt();
            MultiInstanceInvalidationService.b bVar = (MultiInstanceInvalidationService.b) this;
            synchronized (bVar.j.l) {
                bVar.j.l.unregister(interfaceC0728d2);
                bVar.j.k.remove(Integer.valueOf(readInt));
            }
            parcel2.writeNoException();
            return true;
        }
        parcel.enforceInterface("androidx.room.IMultiInstanceInvalidationService");
        IBinder readStrongBinder2 = parcel.readStrongBinder();
        if (readStrongBinder2 != null) {
            IInterface queryLocalInterface2 = readStrongBinder2.queryLocalInterface("androidx.room.IMultiInstanceInvalidationCallback");
            if (queryLocalInterface2 != null && (queryLocalInterface2 instanceof InterfaceC0728d)) {
                interfaceC0728d = (InterfaceC0728d) queryLocalInterface2;
            } else {
                ?? obj2 = new Object();
                obj2.f5287j = readStrongBinder2;
                interfaceC0728d = obj2;
            }
        }
        int Z3 = ((MultiInstanceInvalidationService.b) this).Z(interfaceC0728d, parcel.readString());
        parcel2.writeNoException();
        parcel2.writeInt(Z3);
        return true;
    }

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this;
    }
}
