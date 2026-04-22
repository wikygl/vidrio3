package A1;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.y8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class V0 extends y8 implements InterfaceC0115k0 {

    /* renamed from: j  reason: collision with root package name */
    public final String f95j;

    /* renamed from: k  reason: collision with root package name */
    public final String f96k;

    public V0(String str, String str2) {
        super("com.google.android.gms.ads.internal.client.IMuteThisAdReason");
        this.f95j = str;
        this.f96k = str2;
    }

    public static InterfaceC0115k0 C4(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IMuteThisAdReason");
        if (queryLocalInterface instanceof InterfaceC0115k0) {
            return (InterfaceC0115k0) queryLocalInterface;
        }
        return new x8(iBinder, "com.google.android.gms.ads.internal.client.IMuteThisAdReason");
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 != 1) {
            if (i4 != 2) {
                return false;
            }
            parcel2.writeNoException();
            parcel2.writeString(this.f96k);
        } else {
            parcel2.writeNoException();
            parcel2.writeString(this.f95j);
        }
        return true;
    }

    @Override // A1.InterfaceC0115k0
    public final String b() {
        return this.f95j;
    }

    @Override // A1.InterfaceC0115k0
    public final String d() {
        return this.f96k;
    }
}
