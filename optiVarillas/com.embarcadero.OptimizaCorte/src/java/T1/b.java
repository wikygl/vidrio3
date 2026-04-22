package T1;

import W1.C0323k;
import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class b extends X1.a {

    /* renamed from: j  reason: collision with root package name */
    public final int f2341j;

    /* renamed from: k  reason: collision with root package name */
    public final int f2342k;

    /* renamed from: l  reason: collision with root package name */
    public final PendingIntent f2343l;

    /* renamed from: m  reason: collision with root package name */
    public final String f2344m;

    /* renamed from: n  reason: collision with root package name */
    public static final b f2340n = new b(0);
    public static final Parcelable.Creator<b> CREATOR = new Object();

    public b(int i4, int i5, PendingIntent pendingIntent, String str) {
        this.f2341j = i4;
        this.f2342k = i5;
        this.f2343l = pendingIntent;
        this.f2344m = str;
    }

    public static String h(int i4) {
        if (i4 != 99) {
            if (i4 != 1500) {
                switch (i4) {
                    case -1:
                        return "UNKNOWN";
                    case 0:
                        return "SUCCESS";
                    case 1:
                        return "SERVICE_MISSING";
                    case 2:
                        return "SERVICE_VERSION_UPDATE_REQUIRED";
                    case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                        return "SERVICE_DISABLED";
                    case 4:
                        return "SIGN_IN_REQUIRED";
                    case 5:
                        return "INVALID_ACCOUNT";
                    case 6:
                        return "RESOLUTION_REQUIRED";
                    case 7:
                        return "NETWORK_ERROR";
                    case 8:
                        return "INTERNAL_ERROR";
                    case 9:
                        return "SERVICE_INVALID";
                    case 10:
                        return "DEVELOPER_ERROR";
                    case 11:
                        return "LICENSE_CHECK_FAILED";
                    default:
                        switch (i4) {
                            case 13:
                                return "CANCELED";
                            case 14:
                                return "TIMEOUT";
                            case 15:
                                return "INTERRUPTED";
                            case 16:
                                return "API_UNAVAILABLE";
                            case 17:
                                return "SIGN_IN_FAILED";
                            case 18:
                                return "SERVICE_UPDATING";
                            case 19:
                                return "SERVICE_MISSING_PERMISSION";
                            case 20:
                                return "RESTRICTED_PROFILE";
                            case 21:
                                return "API_VERSION_UPDATE_REQUIRED";
                            case 22:
                                return "RESOLUTION_ACTIVITY_NOT_FOUND";
                            case 23:
                                return "API_DISABLED";
                            case 24:
                                return "API_DISABLED_FOR_CONNECTION";
                            default:
                                return I.h.b(i4, "UNKNOWN_ERROR_CODE(", ")");
                        }
                }
            }
            return "DRIVE_EXTERNAL_STORAGE_REQUIRED";
        }
        return "UNFINISHED";
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        if (this.f2342k == bVar.f2342k && C0323k.a(this.f2343l, bVar.f2343l) && C0323k.a(this.f2344m, bVar.f2344m)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.f2342k), this.f2343l, this.f2344m});
    }

    public final String toString() {
        C0323k.a aVar = new C0323k.a(this);
        aVar.a(h(this.f2342k), "statusCode");
        aVar.a(this.f2343l, "resolution");
        aVar.a(this.f2344m, "message");
        return aVar.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f2341j);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f2342k);
        H.a.l(parcel, 3, this.f2343l, i4);
        H.a.m(parcel, 4, this.f2344m);
        H.a.v(parcel, r4);
    }

    public b(int i4) {
        this(1, i4, null, null);
    }

    public b(int i4, PendingIntent pendingIntent) {
        this(1, i4, pendingIntent, null);
    }
}
