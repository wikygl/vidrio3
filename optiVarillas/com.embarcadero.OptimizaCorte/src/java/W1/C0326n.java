package W1;

import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: W1.n  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0326n extends X1.a {
    public static final Parcelable.Creator<C0326n> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f2759j;

    /* renamed from: k  reason: collision with root package name */
    public final boolean f2760k;

    /* renamed from: l  reason: collision with root package name */
    public final boolean f2761l;

    /* renamed from: m  reason: collision with root package name */
    public final int f2762m;

    /* renamed from: n  reason: collision with root package name */
    public final int f2763n;

    public C0326n(int i4, boolean z4, boolean z5, int i5, int i6) {
        this.f2759j = i4;
        this.f2760k = z4;
        this.f2761l = z5;
        this.f2762m = i5;
        this.f2763n = i6;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f2759j);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f2760k ? 1 : 0);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(this.f2761l ? 1 : 0);
        H.a.x(parcel, 4, 4);
        parcel.writeInt(this.f2762m);
        H.a.x(parcel, 5, 4);
        parcel.writeInt(this.f2763n);
        H.a.v(parcel, r4);
    }
}
