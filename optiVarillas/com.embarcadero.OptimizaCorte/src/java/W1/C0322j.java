package W1;

import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: W1.j  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0322j extends X1.a {
    public static final Parcelable.Creator<C0322j> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f2745j;

    /* renamed from: k  reason: collision with root package name */
    public final int f2746k;

    /* renamed from: l  reason: collision with root package name */
    public final int f2747l;

    /* renamed from: m  reason: collision with root package name */
    public final long f2748m;

    /* renamed from: n  reason: collision with root package name */
    public final long f2749n;

    /* renamed from: o  reason: collision with root package name */
    public final String f2750o;

    /* renamed from: p  reason: collision with root package name */
    public final String f2751p;

    /* renamed from: q  reason: collision with root package name */
    public final int f2752q;

    /* renamed from: r  reason: collision with root package name */
    public final int f2753r;

    public C0322j(int i4, int i5, int i6, long j4, long j5, String str, String str2, int i7, int i8) {
        this.f2745j = i4;
        this.f2746k = i5;
        this.f2747l = i6;
        this.f2748m = j4;
        this.f2749n = j5;
        this.f2750o = str;
        this.f2751p = str2;
        this.f2752q = i7;
        this.f2753r = i8;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f2745j);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f2746k);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(this.f2747l);
        H.a.x(parcel, 4, 8);
        parcel.writeLong(this.f2748m);
        H.a.x(parcel, 5, 8);
        parcel.writeLong(this.f2749n);
        H.a.m(parcel, 6, this.f2750o);
        H.a.m(parcel, 7, this.f2751p);
        H.a.x(parcel, 8, 4);
        parcel.writeInt(this.f2752q);
        H.a.x(parcel, 9, 4);
        parcel.writeInt(this.f2753r);
        H.a.v(parcel, r4);
    }
}
