package A1;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: A1.j1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0113j1 implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        String str = null;
        String str2 = null;
        N0 n02 = null;
        IBinder iBinder = null;
        int i4 = 0;
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
                                iBinder = X1.c.j(parcel, readInt);
                            }
                        } else {
                            n02 = (N0) X1.c.c(parcel, readInt, N0.CREATOR);
                        }
                    } else {
                        str2 = X1.c.d(parcel, readInt);
                    }
                } else {
                    str = X1.c.d(parcel, readInt);
                }
            } else {
                i4 = X1.c.k(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new N0(i4, str, str2, n02, iBinder);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new N0[i4];
    }
}
