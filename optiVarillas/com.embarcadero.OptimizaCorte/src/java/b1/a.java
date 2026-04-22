package B1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class a extends X1.a {
    public static final Parcelable.Creator<a> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f294j;

    /* renamed from: k  reason: collision with root package name */
    public final String f295k;

    /* renamed from: l  reason: collision with root package name */
    public final String f296l;

    public a(String str, String str2, String str3) {
        this.f294j = str;
        this.f295k = str2;
        this.f296l = str3;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 1, this.f294j);
        H.a.m(parcel, 2, this.f295k);
        H.a.m(parcel, 3, this.f296l);
        H.a.v(parcel, r4);
    }
}
