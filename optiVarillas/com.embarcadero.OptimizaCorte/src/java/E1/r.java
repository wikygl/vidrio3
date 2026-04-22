package E1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class r implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        String str = null;
        int i4 = 0;
        int i5 = 0;
        boolean z4 = false;
        boolean z5 = false;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 2) {
                if (c4 != 3) {
                    if (c4 != 4) {
                        if (c4 != 5) {
                            if (c4 != 6) {
                                X1.c.n(parcel, readInt);
                            } else {
                                z5 = X1.c.i(parcel, readInt);
                            }
                        } else {
                            z4 = X1.c.i(parcel, readInt);
                        }
                    } else {
                        i5 = X1.c.k(parcel, readInt);
                    }
                } else {
                    i4 = X1.c.k(parcel, readInt);
                }
            } else {
                str = X1.c.d(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new a(str, i4, i5, z4, z5);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new a[i4];
    }
}
