package z1;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class g extends X1.a {
    public static final Parcelable.Creator<g> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final boolean f6548j;

    /* renamed from: k  reason: collision with root package name */
    public final boolean f6549k;

    /* renamed from: l  reason: collision with root package name */
    public final String f6550l;

    /* renamed from: m  reason: collision with root package name */
    public final boolean f6551m;

    /* renamed from: n  reason: collision with root package name */
    public final float f6552n;

    /* renamed from: o  reason: collision with root package name */
    public final int f6553o;

    /* renamed from: p  reason: collision with root package name */
    public final boolean f6554p;

    /* renamed from: q  reason: collision with root package name */
    public final boolean f6555q;

    /* renamed from: r  reason: collision with root package name */
    public final boolean f6556r;

    public g(boolean z4, boolean z5, String str, boolean z6, float f, int i4, boolean z7, boolean z8, boolean z9) {
        this.f6548j = z4;
        this.f6549k = z5;
        this.f6550l = str;
        this.f6551m = z6;
        this.f6552n = f;
        this.f6553o = i4;
        this.f6554p = z7;
        this.f6555q = z8;
        this.f6556r = z9;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f6548j ? 1 : 0);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(this.f6549k ? 1 : 0);
        H.a.m(parcel, 4, this.f6550l);
        H.a.x(parcel, 5, 4);
        parcel.writeInt(this.f6551m ? 1 : 0);
        H.a.x(parcel, 6, 4);
        parcel.writeFloat(this.f6552n);
        H.a.x(parcel, 7, 4);
        parcel.writeInt(this.f6553o);
        H.a.x(parcel, 8, 4);
        parcel.writeInt(this.f6554p ? 1 : 0);
        H.a.x(parcel, 9, 4);
        parcel.writeInt(this.f6555q ? 1 : 0);
        H.a.x(parcel, 10, 4);
        parcel.writeInt(this.f6556r ? 1 : 0);
        H.a.v(parcel, r4);
    }

    public g(boolean z4, boolean z5, boolean z6, float f, boolean z7, boolean z8, boolean z9) {
        this(z4, z5, null, z6, f, -1, z7, z8, z9);
    }
}
