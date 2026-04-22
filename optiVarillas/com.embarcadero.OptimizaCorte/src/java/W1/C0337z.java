package W1;

import android.os.Parcel;
import android.os.Parcelable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: W1.z  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0337z implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        String str = null;
        String str2 = null;
        long j4 = 0;
        long j5 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = -1;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 1:
                    i4 = X1.c.k(parcel, readInt);
                    break;
                case 2:
                    i5 = X1.c.k(parcel, readInt);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    i6 = X1.c.k(parcel, readInt);
                    break;
                case 4:
                    j4 = X1.c.l(parcel, readInt);
                    break;
                case 5:
                    j5 = X1.c.l(parcel, readInt);
                    break;
                case 6:
                    str = X1.c.d(parcel, readInt);
                    break;
                case 7:
                    str2 = X1.c.d(parcel, readInt);
                    break;
                case '\b':
                    i7 = X1.c.k(parcel, readInt);
                    break;
                case '\t':
                    i8 = X1.c.k(parcel, readInt);
                    break;
                default:
                    X1.c.n(parcel, readInt);
                    break;
            }
        }
        X1.c.h(parcel, o4);
        return new C0322j(i4, i5, i6, j4, j5, str, str2, i7, i8);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new C0322j[i4];
    }
}
