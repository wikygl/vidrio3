package A1;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import t1.C0803e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C1 extends X1.a {
    public static final Parcelable.Creator<C1> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f9j;

    /* renamed from: k  reason: collision with root package name */
    public final int f10k;

    /* renamed from: l  reason: collision with root package name */
    public final int f11l;

    /* renamed from: m  reason: collision with root package name */
    public final boolean f12m;

    /* renamed from: n  reason: collision with root package name */
    public final int f13n;

    /* renamed from: o  reason: collision with root package name */
    public final int f14o;

    /* renamed from: p  reason: collision with root package name */
    public final C1[] f15p;

    /* renamed from: q  reason: collision with root package name */
    public final boolean f16q;

    /* renamed from: r  reason: collision with root package name */
    public final boolean f17r;

    /* renamed from: s  reason: collision with root package name */
    public boolean f18s;

    /* renamed from: t  reason: collision with root package name */
    public final boolean f19t;

    /* renamed from: u  reason: collision with root package name */
    public final boolean f20u;

    /* renamed from: v  reason: collision with root package name */
    public final boolean f21v;

    /* renamed from: w  reason: collision with root package name */
    public final boolean f22w;

    /* renamed from: x  reason: collision with root package name */
    public final boolean f23x;

    public C1() {
        this("interstitial_mb", 0, 0, true, 0, 0, null, false, false, false, false, false, false, false, false);
    }

    public static C1 h() {
        return new C1("interstitial_mb", 0, 0, false, 0, 0, null, false, false, false, false, true, false, false, false);
    }

    public static C1 i() {
        return new C1("320x50_mb", 0, 0, false, 0, 0, null, true, false, false, false, false, false, false, false);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 2, this.f9j);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(this.f10k);
        H.a.x(parcel, 4, 4);
        parcel.writeInt(this.f11l);
        H.a.x(parcel, 5, 4);
        parcel.writeInt(this.f12m ? 1 : 0);
        H.a.x(parcel, 6, 4);
        parcel.writeInt(this.f13n);
        H.a.x(parcel, 7, 4);
        parcel.writeInt(this.f14o);
        H.a.p(parcel, 8, this.f15p, i4);
        H.a.x(parcel, 9, 4);
        parcel.writeInt(this.f16q ? 1 : 0);
        H.a.x(parcel, 10, 4);
        parcel.writeInt(this.f17r ? 1 : 0);
        boolean z4 = this.f18s;
        H.a.x(parcel, 11, 4);
        parcel.writeInt(z4 ? 1 : 0);
        H.a.x(parcel, 12, 4);
        parcel.writeInt(this.f19t ? 1 : 0);
        H.a.x(parcel, 13, 4);
        parcel.writeInt(this.f20u ? 1 : 0);
        H.a.x(parcel, 14, 4);
        parcel.writeInt(this.f21v ? 1 : 0);
        H.a.x(parcel, 15, 4);
        parcel.writeInt(this.f22w ? 1 : 0);
        H.a.x(parcel, 16, 4);
        parcel.writeInt(this.f23x ? 1 : 0);
        H.a.v(parcel, r4);
    }

    public C1(Context context, C0803e c0803e) {
        this(context, new C0803e[]{c0803e});
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x00e7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public C1(android.content.Context r18, t1.C0803e[] r19) {
        /*
            Method dump skipped, instructions count: 395
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: A1.C1.<init>(android.content.Context, t1.e[]):void");
    }

    public C1(String str, int i4, int i5, boolean z4, int i6, int i7, C1[] c1Arr, boolean z5, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, boolean z11, boolean z12) {
        this.f9j = str;
        this.f10k = i4;
        this.f11l = i5;
        this.f12m = z4;
        this.f13n = i6;
        this.f14o = i7;
        this.f15p = c1Arr;
        this.f16q = z5;
        this.f17r = z6;
        this.f18s = z7;
        this.f19t = z8;
        this.f20u = z9;
        this.f21v = z10;
        this.f22w = z11;
        this.f23x = z12;
    }
}
