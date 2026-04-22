package A1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class I1 extends X1.a {
    public static final Parcelable.Creator<I1> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f37j;

    public I1(int i4) {
        this.f37j = i4;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f37j);
        H.a.v(parcel, r4);
    }
}
