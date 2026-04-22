package A1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class n1 extends X1.a {
    public static final Parcelable.Creator<n1> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f158j;

    /* renamed from: k  reason: collision with root package name */
    public final int f159k;

    public n1(int i4, int i5) {
        this.f158j = i4;
        this.f159k = i5;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f158j);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f159k);
        H.a.v(parcel, r4);
    }
}
