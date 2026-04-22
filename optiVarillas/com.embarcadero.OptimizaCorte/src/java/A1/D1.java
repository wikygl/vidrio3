package A1;

import android.os.Parcel;
import android.os.Parcelable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class D1 implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        String str = null;
        C1[] c1Arr = null;
        int i4 = 0;
        int i5 = 0;
        boolean z4 = false;
        int i6 = 0;
        int i7 = 0;
        boolean z5 = false;
        boolean z6 = false;
        boolean z7 = false;
        boolean z8 = false;
        boolean z9 = false;
        boolean z10 = false;
        boolean z11 = false;
        boolean z12 = false;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 2:
                    str = X1.c.d(parcel, readInt);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    i4 = X1.c.k(parcel, readInt);
                    break;
                case 4:
                    i5 = X1.c.k(parcel, readInt);
                    break;
                case 5:
                    z4 = X1.c.i(parcel, readInt);
                    break;
                case 6:
                    i6 = X1.c.k(parcel, readInt);
                    break;
                case 7:
                    i7 = X1.c.k(parcel, readInt);
                    break;
                case '\b':
                    c1Arr = (C1[]) X1.c.g(parcel, readInt, C1.CREATOR);
                    break;
                case '\t':
                    z5 = X1.c.i(parcel, readInt);
                    break;
                case '\n':
                    z6 = X1.c.i(parcel, readInt);
                    break;
                case 11:
                    z7 = X1.c.i(parcel, readInt);
                    break;
                case '\f':
                    z8 = X1.c.i(parcel, readInt);
                    break;
                case '\r':
                    z9 = X1.c.i(parcel, readInt);
                    break;
                case 14:
                    z10 = X1.c.i(parcel, readInt);
                    break;
                case 15:
                    z11 = X1.c.i(parcel, readInt);
                    break;
                case 16:
                    z12 = X1.c.i(parcel, readInt);
                    break;
                default:
                    X1.c.n(parcel, readInt);
                    break;
            }
        }
        X1.c.h(parcel, o4);
        return new C1(str, i4, i5, z4, i6, i7, c1Arr, z5, z6, z7, z8, z9, z10, z11, z12);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new C1[i4];
    }
}
