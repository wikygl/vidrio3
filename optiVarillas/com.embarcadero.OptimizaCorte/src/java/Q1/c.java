package Q1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class c extends X1.a {
    public static final Parcelable.Creator<c> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f2015j;

    /* renamed from: k  reason: collision with root package name */
    public final String f2016k;

    public c(String str, String str2) {
        this.f2015j = str;
        this.f2016k = str2;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 1, this.f2015j);
        H.a.m(parcel, 2, this.f2016k);
        H.a.v(parcel, r4);
    }
}
