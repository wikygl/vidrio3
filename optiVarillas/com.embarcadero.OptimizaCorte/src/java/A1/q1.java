package A1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class q1 implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        String str = null;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            if (((char) readInt) != 15) {
                X1.c.n(parcel, readInt);
            } else {
                str = X1.c.d(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new p1(str);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new p1[i4];
    }
}
