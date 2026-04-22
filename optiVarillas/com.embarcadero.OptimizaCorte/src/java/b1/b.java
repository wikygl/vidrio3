package B1;

import X1.c;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = c.o(parcel);
        String str = null;
        String str2 = null;
        String str3 = null;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 1) {
                if (c4 != 2) {
                    if (c4 != 3) {
                        c.n(parcel, readInt);
                    } else {
                        str3 = c.d(parcel, readInt);
                    }
                } else {
                    str2 = c.d(parcel, readInt);
                }
            } else {
                str = c.d(parcel, readInt);
            }
        }
        c.h(parcel, o4);
        return new a(str, str2, str3);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new a[i4];
    }
}
