package W1;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/* renamed from: W1.q  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0329q implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        int i4 = 0;
        while (true) {
            ArrayList arrayList = null;
            while (parcel.dataPosition() < o4) {
                int readInt = parcel.readInt();
                char c4 = (char) readInt;
                if (c4 != 1) {
                    if (c4 != 2) {
                        X1.c.n(parcel, readInt);
                    } else {
                        Parcelable.Creator<C0322j> creator = C0322j.CREATOR;
                        int m4 = X1.c.m(parcel, readInt);
                        int dataPosition = parcel.dataPosition();
                        if (m4 == 0) {
                            break;
                        }
                        arrayList = parcel.createTypedArrayList(creator);
                        parcel.setDataPosition(dataPosition + m4);
                    }
                } else {
                    i4 = X1.c.k(parcel, readInt);
                }
            }
            X1.c.h(parcel, o4);
            return new C0327o(i4, arrayList);
        }
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new C0327o[i4];
    }
}
