package C1;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class t implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        i iVar = null;
        IBinder iBinder = null;
        IBinder iBinder2 = null;
        IBinder iBinder3 = null;
        IBinder iBinder4 = null;
        String str = null;
        String str2 = null;
        IBinder iBinder5 = null;
        String str3 = null;
        E1.a aVar = null;
        String str4 = null;
        z1.g gVar = null;
        IBinder iBinder6 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        IBinder iBinder7 = null;
        IBinder iBinder8 = null;
        IBinder iBinder9 = null;
        boolean z4 = false;
        int i4 = 0;
        int i5 = 0;
        boolean z5 = false;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 2:
                    iVar = (i) X1.c.c(parcel, readInt, i.CREATOR);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    iBinder = X1.c.j(parcel, readInt);
                    break;
                case 4:
                    iBinder2 = X1.c.j(parcel, readInt);
                    break;
                case 5:
                    iBinder3 = X1.c.j(parcel, readInt);
                    break;
                case 6:
                    iBinder4 = X1.c.j(parcel, readInt);
                    break;
                case 7:
                    str = X1.c.d(parcel, readInt);
                    break;
                case '\b':
                    z4 = X1.c.i(parcel, readInt);
                    break;
                case '\t':
                    str2 = X1.c.d(parcel, readInt);
                    break;
                case '\n':
                    iBinder5 = X1.c.j(parcel, readInt);
                    break;
                case 11:
                    i4 = X1.c.k(parcel, readInt);
                    break;
                case '\f':
                    i5 = X1.c.k(parcel, readInt);
                    break;
                case '\r':
                    str3 = X1.c.d(parcel, readInt);
                    break;
                case 14:
                    aVar = (E1.a) X1.c.c(parcel, readInt, E1.a.CREATOR);
                    break;
                case 15:
                case 20:
                case 21:
                case 22:
                case 23:
                default:
                    X1.c.n(parcel, readInt);
                    break;
                case 16:
                    str4 = X1.c.d(parcel, readInt);
                    break;
                case 17:
                    gVar = (z1.g) X1.c.c(parcel, readInt, z1.g.CREATOR);
                    break;
                case 18:
                    iBinder6 = X1.c.j(parcel, readInt);
                    break;
                case 19:
                    str5 = X1.c.d(parcel, readInt);
                    break;
                case 24:
                    str6 = X1.c.d(parcel, readInt);
                    break;
                case 25:
                    str7 = X1.c.d(parcel, readInt);
                    break;
                case 26:
                    iBinder7 = X1.c.j(parcel, readInt);
                    break;
                case 27:
                    iBinder8 = X1.c.j(parcel, readInt);
                    break;
                case 28:
                    iBinder9 = X1.c.j(parcel, readInt);
                    break;
                case 29:
                    z5 = X1.c.i(parcel, readInt);
                    break;
            }
        }
        X1.c.h(parcel, o4);
        return new AdOverlayInfoParcel(iVar, iBinder, iBinder2, iBinder3, iBinder4, str, z4, str2, iBinder5, i4, i5, str3, aVar, str4, gVar, iBinder6, str5, str6, str7, iBinder7, iBinder8, iBinder9, z5);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new AdOverlayInfoParcel[i4];
    }
}
