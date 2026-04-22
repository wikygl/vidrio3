package w1;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: w1.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0843g implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        IBinder iBinder = null;
        boolean z4 = false;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 1) {
                if (c4 != 2) {
                    X1.c.n(parcel, readInt);
                } else {
                    iBinder = X1.c.j(parcel, readInt);
                }
            } else {
                z4 = X1.c.i(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new C0837a(z4, iBinder);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new C0837a[i4];
    }
}
