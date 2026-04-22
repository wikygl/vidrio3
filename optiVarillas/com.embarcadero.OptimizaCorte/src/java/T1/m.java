package T1;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class m implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        PendingIntent pendingIntent = null;
        String str = null;
        int i4 = 0;
        int i5 = 0;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 1) {
                if (c4 != 2) {
                    if (c4 != 3) {
                        if (c4 != 4) {
                            X1.c.n(parcel, readInt);
                        } else {
                            str = X1.c.d(parcel, readInt);
                        }
                    } else {
                        pendingIntent = (PendingIntent) X1.c.c(parcel, readInt, PendingIntent.CREATOR);
                    }
                } else {
                    i5 = X1.c.k(parcel, readInt);
                }
            } else {
                i4 = X1.c.k(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new b(i4, i5, pendingIntent, str);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new b[i4];
    }
}
