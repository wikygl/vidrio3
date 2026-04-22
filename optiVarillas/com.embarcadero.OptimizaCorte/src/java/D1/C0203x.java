package D1;

import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: D1.x  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0203x implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        int i4 = 0;
        String str = null;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 1) {
                if (c4 != 2) {
                    X1.c.n(parcel, readInt);
                } else {
                    i4 = X1.c.k(parcel, readInt);
                }
            } else {
                str = X1.c.d(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new C0202w(str, i4);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new C0202w[i4];
    }
}
