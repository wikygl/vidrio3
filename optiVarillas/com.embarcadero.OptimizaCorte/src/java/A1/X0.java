package A1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class X0 extends X1.a {
    public static final Parcelable.Creator<X0> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f98j;

    /* renamed from: k  reason: collision with root package name */
    public final int f99k;

    /* renamed from: l  reason: collision with root package name */
    public final String f100l;

    public X0() {
        this(241199800, 241199000, "23.1.0");
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f98j);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f99k);
        H.a.m(parcel, 3, this.f100l);
        H.a.v(parcel, r4);
    }

    public X0(int i4, int i5, String str) {
        this.f98j = i4;
        this.f99k = i5;
        this.f100l = str;
    }
}
