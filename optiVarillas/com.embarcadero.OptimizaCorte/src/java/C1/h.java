package C1;

import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class h implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        Intent intent = null;
        IBinder iBinder = null;
        boolean z4 = false;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 2:
                    str = X1.c.d(parcel, readInt);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    str2 = X1.c.d(parcel, readInt);
                    break;
                case 4:
                    str3 = X1.c.d(parcel, readInt);
                    break;
                case 5:
                    str4 = X1.c.d(parcel, readInt);
                    break;
                case 6:
                    str5 = X1.c.d(parcel, readInt);
                    break;
                case 7:
                    str6 = X1.c.d(parcel, readInt);
                    break;
                case '\b':
                    str7 = X1.c.d(parcel, readInt);
                    break;
                case '\t':
                    intent = (Intent) X1.c.c(parcel, readInt, Intent.CREATOR);
                    break;
                case '\n':
                    iBinder = X1.c.j(parcel, readInt);
                    break;
                case 11:
                    z4 = X1.c.i(parcel, readInt);
                    break;
                default:
                    X1.c.n(parcel, readInt);
                    break;
            }
        }
        X1.c.h(parcel, o4);
        return new i(str, str2, str3, str4, str5, str6, str7, intent, iBinder, z4);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new i[i4];
    }
}
