package W1;

import android.os.Parcel;
import android.os.Parcelable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class T implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        C0326n c0326n = null;
        int[] iArr = null;
        int[] iArr2 = null;
        boolean z4 = false;
        boolean z5 = false;
        int i4 = 0;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 1:
                    c0326n = (C0326n) X1.c.c(parcel, readInt, C0326n.CREATOR);
                    break;
                case 2:
                    z4 = X1.c.i(parcel, readInt);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    z5 = X1.c.i(parcel, readInt);
                    break;
                case 4:
                    int m4 = X1.c.m(parcel, readInt);
                    int dataPosition = parcel.dataPosition();
                    if (m4 == 0) {
                        iArr = null;
                        break;
                    } else {
                        iArr = parcel.createIntArray();
                        parcel.setDataPosition(dataPosition + m4);
                        break;
                    }
                case 5:
                    i4 = X1.c.k(parcel, readInt);
                    break;
                case 6:
                    int m5 = X1.c.m(parcel, readInt);
                    int dataPosition2 = parcel.dataPosition();
                    if (m5 == 0) {
                        iArr2 = null;
                        break;
                    } else {
                        iArr2 = parcel.createIntArray();
                        parcel.setDataPosition(dataPosition2 + m5);
                        break;
                    }
                default:
                    X1.c.n(parcel, readInt);
                    break;
            }
        }
        X1.c.h(parcel, o4);
        return new C0316d(c0326n, z4, z5, iArr, i4, iArr2);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new C0316d[i4];
    }
}
