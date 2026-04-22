package Q1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class e extends X1.a {
    public static final Parcelable.Creator<e> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f2017j;

    /* renamed from: k  reason: collision with root package name */
    public final int f2018k;

    public e(String str, int i4) {
        this.f2017j = str;
        this.f2018k = i4;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 1, this.f2017j);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f2018k);
        H.a.v(parcel, r4);
    }
}
