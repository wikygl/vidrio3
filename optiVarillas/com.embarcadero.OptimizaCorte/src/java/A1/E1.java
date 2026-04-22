package A1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class E1 extends X1.a {
    public static final Parcelable.Creator<E1> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f24j;

    /* renamed from: k  reason: collision with root package name */
    public final int f25k;

    /* renamed from: l  reason: collision with root package name */
    public final String f26l;

    /* renamed from: m  reason: collision with root package name */
    public final long f27m;

    public E1(int i4, int i5, long j4, String str) {
        this.f24j = i4;
        this.f25k = i5;
        this.f26l = str;
        this.f27m = j4;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f24j);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f25k);
        H.a.m(parcel, 3, this.f26l);
        H.a.x(parcel, 4, 8);
        parcel.writeLong(this.f27m);
        H.a.v(parcel, r4);
    }
}
