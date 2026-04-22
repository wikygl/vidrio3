package o2;

import W1.A;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class j extends X1.a {
    public static final Parcelable.Creator<j> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f5495j;

    /* renamed from: k  reason: collision with root package name */
    public final A f5496k;

    public j(int i4, A a4) {
        this.f5495j = i4;
        this.f5496k = a4;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f5495j);
        H.a.l(parcel, 2, this.f5496k, i4);
        H.a.v(parcel, r4);
    }
}
