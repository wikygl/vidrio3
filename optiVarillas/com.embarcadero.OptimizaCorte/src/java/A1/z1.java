package A1;

import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class z1 implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        long j4 = 0;
        long j5 = 0;
        Bundle bundle = null;
        ArrayList<String> arrayList = null;
        String str = null;
        p1 p1Var = null;
        Location location = null;
        String str2 = null;
        Bundle bundle2 = null;
        Bundle bundle3 = null;
        ArrayList<String> arrayList2 = null;
        String str3 = null;
        String str4 = null;
        Q q4 = null;
        String str5 = null;
        ArrayList<String> arrayList3 = null;
        String str6 = null;
        int i4 = 0;
        int i5 = 0;
        boolean z4 = false;
        int i6 = 0;
        boolean z5 = false;
        boolean z6 = false;
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 1:
                    i4 = X1.c.k(parcel, readInt);
                    break;
                case 2:
                    j4 = X1.c.l(parcel, readInt);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    bundle = X1.c.a(parcel, readInt);
                    break;
                case 4:
                    i5 = X1.c.k(parcel, readInt);
                    break;
                case 5:
                    arrayList = X1.c.f(parcel, readInt);
                    break;
                case 6:
                    z4 = X1.c.i(parcel, readInt);
                    break;
                case 7:
                    i6 = X1.c.k(parcel, readInt);
                    break;
                case '\b':
                    z5 = X1.c.i(parcel, readInt);
                    break;
                case '\t':
                    str = X1.c.d(parcel, readInt);
                    break;
                case '\n':
                    p1Var = (p1) X1.c.c(parcel, readInt, p1.CREATOR);
                    break;
                case 11:
                    location = (Location) X1.c.c(parcel, readInt, Location.CREATOR);
                    break;
                case '\f':
                    str2 = X1.c.d(parcel, readInt);
                    break;
                case '\r':
                    bundle2 = X1.c.a(parcel, readInt);
                    break;
                case 14:
                    bundle3 = X1.c.a(parcel, readInt);
                    break;
                case 15:
                    arrayList2 = X1.c.f(parcel, readInt);
                    break;
                case 16:
                    str3 = X1.c.d(parcel, readInt);
                    break;
                case 17:
                    str4 = X1.c.d(parcel, readInt);
                    break;
                case 18:
                    z6 = X1.c.i(parcel, readInt);
                    break;
                case 19:
                    q4 = (Q) X1.c.c(parcel, readInt, Q.CREATOR);
                    break;
                case 20:
                    i7 = X1.c.k(parcel, readInt);
                    break;
                case 21:
                    str5 = X1.c.d(parcel, readInt);
                    break;
                case 22:
                    arrayList3 = X1.c.f(parcel, readInt);
                    break;
                case 23:
                    i8 = X1.c.k(parcel, readInt);
                    break;
                case 24:
                    str6 = X1.c.d(parcel, readInt);
                    break;
                case 25:
                    i9 = X1.c.k(parcel, readInt);
                    break;
                case 26:
                    j5 = X1.c.l(parcel, readInt);
                    break;
                default:
                    X1.c.n(parcel, readInt);
                    break;
            }
        }
        X1.c.h(parcel, o4);
        return new y1(i4, j4, bundle, i5, arrayList, z4, i6, z5, str, p1Var, location, str2, bundle2, bundle3, arrayList2, str3, str4, z6, q4, i7, str5, arrayList3, i8, str6, i9, j5);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new y1[i4];
    }
}
