package W1;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class S implements Parcelable.Creator {
    /* JADX WARN: Type inference failed for: r9v1, types: [X1.a, W1.Q, java.lang.Object] */
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        Bundle bundle = null;
        T1.d[] dVarArr = null;
        C0316d c0316d = null;
        int i4 = 0;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 1) {
                if (c4 != 2) {
                    if (c4 != 3) {
                        if (c4 != 4) {
                            X1.c.n(parcel, readInt);
                        } else {
                            c0316d = (C0316d) X1.c.c(parcel, readInt, C0316d.CREATOR);
                        }
                    } else {
                        i4 = X1.c.k(parcel, readInt);
                    }
                } else {
                    dVarArr = (T1.d[]) X1.c.g(parcel, readInt, T1.d.CREATOR);
                }
            } else {
                bundle = X1.c.a(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        ?? aVar = new X1.a();
        aVar.f2658j = bundle;
        aVar.f2659k = dVarArr;
        aVar.f2660l = i4;
        aVar.f2661m = c0316d;
        return aVar;
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new Q[i4];
    }
}
