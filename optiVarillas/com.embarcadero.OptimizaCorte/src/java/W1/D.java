package W1;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class D implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        IBinder iBinder = null;
        T1.b bVar = null;
        int i4 = 0;
        boolean z4 = false;
        boolean z5 = false;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 1) {
                if (c4 != 2) {
                    if (c4 != 3) {
                        if (c4 != 4) {
                            if (c4 != 5) {
                                X1.c.n(parcel, readInt);
                            } else {
                                z5 = X1.c.i(parcel, readInt);
                            }
                        } else {
                            z4 = X1.c.i(parcel, readInt);
                        }
                    } else {
                        bVar = (T1.b) X1.c.c(parcel, readInt, T1.b.CREATOR);
                    }
                } else {
                    iBinder = X1.c.j(parcel, readInt);
                }
            } else {
                i4 = X1.c.k(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new C(i4, iBinder, bVar, z4, z5);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new C[i4];
    }
}
