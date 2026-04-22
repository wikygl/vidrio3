package A1;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class H1 implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        String str = null;
        N0 n02 = null;
        Bundle bundle = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        long j4 = 0;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 1:
                    str = X1.c.d(parcel, readInt);
                    break;
                case 2:
                    j4 = X1.c.l(parcel, readInt);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    n02 = (N0) X1.c.c(parcel, readInt, N0.CREATOR);
                    break;
                case 4:
                    bundle = X1.c.a(parcel, readInt);
                    break;
                case 5:
                    str2 = X1.c.d(parcel, readInt);
                    break;
                case 6:
                    str3 = X1.c.d(parcel, readInt);
                    break;
                case 7:
                    str4 = X1.c.d(parcel, readInt);
                    break;
                case '\b':
                    str5 = X1.c.d(parcel, readInt);
                    break;
                default:
                    X1.c.n(parcel, readInt);
                    break;
            }
        }
        X1.c.h(parcel, o4);
        return new G1(str, j4, n02, bundle, str2, str3, str4, str5);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new G1[i4];
    }
}
