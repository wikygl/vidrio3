package A1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class H0 extends X1.a {
    public static final Parcelable.Creator<H0> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f36j;

    public H0(int i4) {
        this.f36j = i4;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f36j);
        H.a.v(parcel, r4);
    }
}
