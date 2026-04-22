package o2;

import W1.C;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class l extends X1.a {
    public static final Parcelable.Creator<l> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f5497j;

    /* renamed from: k  reason: collision with root package name */
    public final T1.b f5498k;

    /* renamed from: l  reason: collision with root package name */
    public final C f5499l;

    public l(int i4, T1.b bVar, C c4) {
        this.f5497j = i4;
        this.f5498k = bVar;
        this.f5499l = c4;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f5497j);
        H.a.l(parcel, 2, this.f5498k, i4);
        H.a.l(parcel, 3, this.f5499l, i4);
        H.a.v(parcel, r4);
    }
}
