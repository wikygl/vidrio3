package A1;

import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: A1.o0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0123o0 implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        String str = null;
        String str2 = null;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 1) {
                if (c4 != 2) {
                    X1.c.n(parcel, readInt);
                } else {
                    str2 = X1.c.d(parcel, readInt);
                }
            } else {
                str = X1.c.d(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new Q(str, str2);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new Q[i4];
    }
}
