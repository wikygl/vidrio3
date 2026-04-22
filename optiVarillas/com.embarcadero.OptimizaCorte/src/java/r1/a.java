package R1;

import X1.c;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import java.util.ArrayList;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = c.o(parcel);
        String str = null;
        String str2 = null;
        String str3 = null;
        String str4 = null;
        Uri uri = null;
        String str5 = null;
        String str6 = null;
        ArrayList arrayList = null;
        String str7 = null;
        String str8 = null;
        long j4 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 1:
                    i4 = c.k(parcel, readInt);
                    break;
                case 2:
                    str = c.d(parcel, readInt);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    str2 = c.d(parcel, readInt);
                    break;
                case 4:
                    str3 = c.d(parcel, readInt);
                    break;
                case 5:
                    str4 = c.d(parcel, readInt);
                    break;
                case 6:
                    uri = (Uri) c.c(parcel, readInt, Uri.CREATOR);
                    break;
                case 7:
                    str5 = c.d(parcel, readInt);
                    break;
                case '\b':
                    j4 = c.l(parcel, readInt);
                    break;
                case '\t':
                    str6 = c.d(parcel, readInt);
                    break;
                case '\n':
                    Parcelable.Creator creator = Scope.CREATOR;
                    int m4 = c.m(parcel, readInt);
                    int dataPosition = parcel.dataPosition();
                    if (m4 == 0) {
                        arrayList = null;
                        break;
                    } else {
                        ArrayList createTypedArrayList = parcel.createTypedArrayList(creator);
                        parcel.setDataPosition(dataPosition + m4);
                        arrayList = createTypedArrayList;
                        break;
                    }
                case 11:
                    str7 = c.d(parcel, readInt);
                    break;
                case '\f':
                    str8 = c.d(parcel, readInt);
                    break;
                default:
                    c.n(parcel, readInt);
                    break;
            }
        }
        c.h(parcel, o4);
        return new GoogleSignInAccount(i4, str, str2, str3, str4, uri, str5, j4, str6, arrayList, str7, str8);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new GoogleSignInAccount[i4];
    }
}
