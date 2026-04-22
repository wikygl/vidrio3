package A1;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class G1 extends X1.a {
    public static final Parcelable.Creator<G1> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f28j;

    /* renamed from: k  reason: collision with root package name */
    public long f29k;

    /* renamed from: l  reason: collision with root package name */
    public N0 f30l;

    /* renamed from: m  reason: collision with root package name */
    public final Bundle f31m;

    /* renamed from: n  reason: collision with root package name */
    public final String f32n;

    /* renamed from: o  reason: collision with root package name */
    public final String f33o;

    /* renamed from: p  reason: collision with root package name */
    public final String f34p;

    /* renamed from: q  reason: collision with root package name */
    public final String f35q;

    public G1(String str, long j4, N0 n02, Bundle bundle, String str2, String str3, String str4, String str5) {
        this.f28j = str;
        this.f29k = j4;
        this.f30l = n02;
        this.f31m = bundle;
        this.f32n = str2;
        this.f33o = str3;
        this.f34p = str4;
        this.f35q = str5;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 1, this.f28j);
        long j4 = this.f29k;
        H.a.x(parcel, 2, 8);
        parcel.writeLong(j4);
        H.a.l(parcel, 3, this.f30l, i4);
        H.a.h(parcel, 4, this.f31m);
        H.a.m(parcel, 5, this.f32n);
        H.a.m(parcel, 6, this.f33o);
        H.a.m(parcel, 7, this.f34p);
        H.a.m(parcel, 8, this.f35q);
        H.a.v(parcel, r4);
    }
}
