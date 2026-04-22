package j2;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class i implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        long j4 = 0;
        long j5 = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        Bundle bundle = null;
        String str4 = null;
        boolean z4 = false;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 1:
                    j4 = X1.c.l(parcel, readInt);
                    break;
                case 2:
                    j5 = X1.c.l(parcel, readInt);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    z4 = X1.c.i(parcel, readInt);
                    break;
                case 4:
                    str = X1.c.d(parcel, readInt);
                    break;
                case 5:
                    str2 = X1.c.d(parcel, readInt);
                    break;
                case 6:
                    str3 = X1.c.d(parcel, readInt);
                    break;
                case 7:
                    bundle = X1.c.a(parcel, readInt);
                    break;
                case '\b':
                    str4 = X1.c.d(parcel, readInt);
                    break;
                default:
                    X1.c.n(parcel, readInt);
                    break;
            }
        }
        X1.c.h(parcel, o4);
        return new h(j4, j5, z4, str, str2, str3, bundle, str4);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new h[i4];
    }
}
