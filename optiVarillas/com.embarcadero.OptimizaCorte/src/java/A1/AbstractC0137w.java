package A1;

import android.os.Parcel;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: A1.w  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0137w extends y8 implements InterfaceC0139x {
    public AbstractC0137w() {
        super("com.google.android.gms.ads.internal.client.IAdListener");
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        switch (i4) {
            case 1:
                i();
                break;
            case 2:
                int readInt = parcel.readInt();
                z8.b(parcel);
                w(readInt);
                break;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                break;
            case 4:
                f();
                break;
            case 5:
                j();
                break;
            case 6:
                r();
                break;
            case 7:
                h();
                break;
            case 8:
                z8.b(parcel);
                s((N0) z8.a(parcel, N0.CREATOR));
                break;
            case 9:
                k();
                break;
            default:
                return false;
        }
        parcel2.writeNoException();
        return true;
    }
}
