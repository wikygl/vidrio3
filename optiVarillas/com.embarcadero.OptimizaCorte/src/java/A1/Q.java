package A1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class Q extends X1.a {
    public static final Parcelable.Creator<Q> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f80j;

    /* renamed from: k  reason: collision with root package name */
    public final String f81k;

    public Q(String str, String str2) {
        this.f80j = str;
        this.f81k = str2;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 1, this.f80j);
        H.a.m(parcel, 2, this.f81k);
        H.a.v(parcel, r4);
    }
}
