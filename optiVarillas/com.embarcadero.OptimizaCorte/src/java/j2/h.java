package j2;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class h extends X1.a {
    public static final Parcelable.Creator<h> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final long f4796j;

    /* renamed from: k  reason: collision with root package name */
    public final long f4797k;

    /* renamed from: l  reason: collision with root package name */
    public final boolean f4798l;

    /* renamed from: m  reason: collision with root package name */
    public final String f4799m;

    /* renamed from: n  reason: collision with root package name */
    public final String f4800n;

    /* renamed from: o  reason: collision with root package name */
    public final String f4801o;

    /* renamed from: p  reason: collision with root package name */
    public final Bundle f4802p;

    /* renamed from: q  reason: collision with root package name */
    public final String f4803q;

    public h(long j4, long j5, boolean z4, String str, String str2, String str3, Bundle bundle, String str4) {
        this.f4796j = j4;
        this.f4797k = j5;
        this.f4798l = z4;
        this.f4799m = str;
        this.f4800n = str2;
        this.f4801o = str3;
        this.f4802p = bundle;
        this.f4803q = str4;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 8);
        parcel.writeLong(this.f4796j);
        H.a.x(parcel, 2, 8);
        parcel.writeLong(this.f4797k);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(this.f4798l ? 1 : 0);
        H.a.m(parcel, 4, this.f4799m);
        H.a.m(parcel, 5, this.f4800n);
        H.a.m(parcel, 6, this.f4801o);
        H.a.h(parcel, 7, this.f4802p);
        H.a.m(parcel, 8, this.f4803q);
        H.a.v(parcel, r4);
    }
}
