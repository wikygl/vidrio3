package A1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class t1 implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 2) {
                if (c4 != 3) {
                    if (c4 != 4) {
                        X1.c.n(parcel, readInt);
                    } else {
                        z6 = X1.c.i(parcel, readInt);
                    }
                } else {
                    z5 = X1.c.i(parcel, readInt);
                }
            } else {
                z4 = X1.c.i(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new s1(z4, z5, z6);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new s1[i4];
    }
}
