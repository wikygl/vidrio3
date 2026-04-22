package D1;

import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: D1.w  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0202w extends X1.a {
    public static final Parcelable.Creator<C0202w> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f787j;

    /* renamed from: k  reason: collision with root package name */
    public final int f788k;

    public C0202w(String str, int i4) {
        this.f787j = str == null ? "" : str;
        this.f788k = i4;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 1, this.f787j);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f788k);
        H.a.v(parcel, r4);
    }
}
