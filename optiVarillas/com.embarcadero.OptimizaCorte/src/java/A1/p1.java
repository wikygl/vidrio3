package A1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class p1 extends X1.a {
    public static final Parcelable.Creator<p1> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f166j;

    public p1(String str) {
        this.f166j = str;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 15, this.f166j);
        H.a.v(parcel, r4);
    }
}
