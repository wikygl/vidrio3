package z1;

import android.os.Parcel;
import android.os.Parcelable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class h implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        String str = null;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        float f = 0.0f;
        int i4 = 0;
        boolean z7 = false;
        boolean z8 = false;
        boolean z9 = false;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 2:
                    z4 = X1.c.i(parcel, readInt);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    z5 = X1.c.i(parcel, readInt);
                    break;
                case 4:
                    str = X1.c.d(parcel, readInt);
                    break;
                case 5:
                    z6 = X1.c.i(parcel, readInt);
                    break;
                case 6:
                    X1.c.p(parcel, readInt, 4);
                    f = parcel.readFloat();
                    break;
                case 7:
                    i4 = X1.c.k(parcel, readInt);
                    break;
                case '\b':
                    z7 = X1.c.i(parcel, readInt);
                    break;
                case '\t':
                    z8 = X1.c.i(parcel, readInt);
                    break;
                case '\n':
                    z9 = X1.c.i(parcel, readInt);
                    break;
                default:
                    X1.c.n(parcel, readInt);
                    break;
            }
        }
        X1.c.h(parcel, o4);
        return new g(z4, z5, str, z6, f, i4, z7, z8, z9);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new g[i4];
    }
}
