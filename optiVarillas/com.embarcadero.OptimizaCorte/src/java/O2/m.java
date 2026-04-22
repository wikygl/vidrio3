package o2;

import W1.C;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class m implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        T1.b bVar = null;
        C c4 = null;
        int i4 = 0;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c5 = (char) readInt;
            if (c5 != 1) {
                if (c5 != 2) {
                    if (c5 != 3) {
                        X1.c.n(parcel, readInt);
                    } else {
                        c4 = (C) X1.c.c(parcel, readInt, C.CREATOR);
                    }
                } else {
                    bVar = (T1.b) X1.c.c(parcel, readInt, T1.b.CREATOR);
                }
            } else {
                i4 = X1.c.k(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new l(i4, bVar, c4);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new l[i4];
    }
}
