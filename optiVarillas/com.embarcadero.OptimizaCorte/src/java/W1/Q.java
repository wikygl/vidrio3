package W1;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class Q extends X1.a {
    public static final Parcelable.Creator<Q> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public Bundle f2658j;

    /* renamed from: k  reason: collision with root package name */
    public T1.d[] f2659k;

    /* renamed from: l  reason: collision with root package name */
    public int f2660l;

    /* renamed from: m  reason: collision with root package name */
    public C0316d f2661m;

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.h(parcel, 1, this.f2658j);
        H.a.p(parcel, 2, this.f2659k, i4);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(this.f2660l);
        H.a.l(parcel, 4, this.f2661m, i4);
        H.a.v(parcel, r4);
    }
}
