package o2;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class c implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        Intent intent = null;
        int i4 = 0;
        int i5 = 0;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 1) {
                if (c4 != 2) {
                    if (c4 != 3) {
                        X1.c.n(parcel, readInt);
                    } else {
                        intent = (Intent) X1.c.c(parcel, readInt, Intent.CREATOR);
                    }
                } else {
                    i5 = X1.c.k(parcel, readInt);
                }
            } else {
                i4 = X1.c.k(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new C0749b(i4, i5, intent);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new C0749b[i4];
    }
}
