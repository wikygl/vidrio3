package A1;

import W1.C0323k;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class y1 extends X1.a {
    public static final Parcelable.Creator<y1> CREATOR = new Object();
    @Deprecated

    /* renamed from: A  reason: collision with root package name */
    public final boolean f180A;

    /* renamed from: B  reason: collision with root package name */
    public final Q f181B;

    /* renamed from: C  reason: collision with root package name */
    public final int f182C;

    /* renamed from: D  reason: collision with root package name */
    public final String f183D;

    /* renamed from: E  reason: collision with root package name */
    public final List f184E;

    /* renamed from: F  reason: collision with root package name */
    public final int f185F;

    /* renamed from: G  reason: collision with root package name */
    public final String f186G;

    /* renamed from: H  reason: collision with root package name */
    public final int f187H;

    /* renamed from: I  reason: collision with root package name */
    public final long f188I;

    /* renamed from: j  reason: collision with root package name */
    public final int f189j;
    @Deprecated

    /* renamed from: k  reason: collision with root package name */
    public final long f190k;

    /* renamed from: l  reason: collision with root package name */
    public final Bundle f191l;
    @Deprecated

    /* renamed from: m  reason: collision with root package name */
    public final int f192m;

    /* renamed from: n  reason: collision with root package name */
    public final List f193n;

    /* renamed from: o  reason: collision with root package name */
    public final boolean f194o;

    /* renamed from: p  reason: collision with root package name */
    public final int f195p;

    /* renamed from: q  reason: collision with root package name */
    public final boolean f196q;

    /* renamed from: r  reason: collision with root package name */
    public final String f197r;

    /* renamed from: s  reason: collision with root package name */
    public final p1 f198s;

    /* renamed from: t  reason: collision with root package name */
    public final Location f199t;

    /* renamed from: u  reason: collision with root package name */
    public final String f200u;

    /* renamed from: v  reason: collision with root package name */
    public final Bundle f201v;

    /* renamed from: w  reason: collision with root package name */
    public final Bundle f202w;

    /* renamed from: x  reason: collision with root package name */
    public final List f203x;

    /* renamed from: y  reason: collision with root package name */
    public final String f204y;

    /* renamed from: z  reason: collision with root package name */
    public final String f205z;

    public y1(int i4, long j4, Bundle bundle, int i5, List list, boolean z4, int i6, boolean z5, String str, p1 p1Var, Location location, String str2, Bundle bundle2, Bundle bundle3, List list2, String str3, String str4, boolean z6, Q q4, int i7, String str5, List list3, int i8, String str6, int i9, long j5) {
        this.f189j = i4;
        this.f190k = j4;
        this.f191l = bundle == null ? new Bundle() : bundle;
        this.f192m = i5;
        this.f193n = list;
        this.f194o = z4;
        this.f195p = i6;
        this.f196q = z5;
        this.f197r = str;
        this.f198s = p1Var;
        this.f199t = location;
        this.f200u = str2;
        this.f201v = bundle2 == null ? new Bundle() : bundle2;
        this.f202w = bundle3;
        this.f203x = list2;
        this.f204y = str3;
        this.f205z = str4;
        this.f180A = z6;
        this.f181B = q4;
        this.f182C = i7;
        this.f183D = str5;
        this.f184E = list3 == null ? new ArrayList() : list3;
        this.f185F = i8;
        this.f186G = str6;
        this.f187H = i9;
        this.f188I = j5;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof y1)) {
            return false;
        }
        y1 y1Var = (y1) obj;
        if (this.f189j != y1Var.f189j || this.f190k != y1Var.f190k || !E1.n.b(this.f191l, y1Var.f191l) || this.f192m != y1Var.f192m || !C0323k.a(this.f193n, y1Var.f193n) || this.f194o != y1Var.f194o || this.f195p != y1Var.f195p || this.f196q != y1Var.f196q || !C0323k.a(this.f197r, y1Var.f197r) || !C0323k.a(this.f198s, y1Var.f198s) || !C0323k.a(this.f199t, y1Var.f199t) || !C0323k.a(this.f200u, y1Var.f200u) || !E1.n.b(this.f201v, y1Var.f201v) || !E1.n.b(this.f202w, y1Var.f202w) || !C0323k.a(this.f203x, y1Var.f203x) || !C0323k.a(this.f204y, y1Var.f204y) || !C0323k.a(this.f205z, y1Var.f205z) || this.f180A != y1Var.f180A || this.f182C != y1Var.f182C || !C0323k.a(this.f183D, y1Var.f183D) || !C0323k.a(this.f184E, y1Var.f184E) || this.f185F != y1Var.f185F || !C0323k.a(this.f186G, y1Var.f186G) || this.f187H != y1Var.f187H || this.f188I != y1Var.f188I) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.f189j), Long.valueOf(this.f190k), this.f191l, Integer.valueOf(this.f192m), this.f193n, Boolean.valueOf(this.f194o), Integer.valueOf(this.f195p), Boolean.valueOf(this.f196q), this.f197r, this.f198s, this.f199t, this.f200u, this.f201v, this.f202w, this.f203x, this.f204y, this.f205z, Boolean.valueOf(this.f180A), Integer.valueOf(this.f182C), this.f183D, this.f184E, Integer.valueOf(this.f185F), this.f186G, Integer.valueOf(this.f187H), Long.valueOf(this.f188I)});
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f189j);
        H.a.x(parcel, 2, 8);
        parcel.writeLong(this.f190k);
        H.a.h(parcel, 3, this.f191l);
        H.a.x(parcel, 4, 4);
        parcel.writeInt(this.f192m);
        H.a.o(parcel, 5, this.f193n);
        H.a.x(parcel, 6, 4);
        parcel.writeInt(this.f194o ? 1 : 0);
        H.a.x(parcel, 7, 4);
        parcel.writeInt(this.f195p);
        H.a.x(parcel, 8, 4);
        parcel.writeInt(this.f196q ? 1 : 0);
        H.a.m(parcel, 9, this.f197r);
        H.a.l(parcel, 10, this.f198s, i4);
        H.a.l(parcel, 11, this.f199t, i4);
        H.a.m(parcel, 12, this.f200u);
        H.a.h(parcel, 13, this.f201v);
        H.a.h(parcel, 14, this.f202w);
        H.a.o(parcel, 15, this.f203x);
        H.a.m(parcel, 16, this.f204y);
        H.a.m(parcel, 17, this.f205z);
        H.a.x(parcel, 18, 4);
        parcel.writeInt(this.f180A ? 1 : 0);
        H.a.l(parcel, 19, this.f181B, i4);
        H.a.x(parcel, 20, 4);
        parcel.writeInt(this.f182C);
        H.a.m(parcel, 21, this.f183D);
        H.a.o(parcel, 22, this.f184E);
        H.a.x(parcel, 23, 4);
        parcel.writeInt(this.f185F);
        H.a.m(parcel, 24, this.f186G);
        H.a.x(parcel, 25, 4);
        parcel.writeInt(this.f187H);
        H.a.x(parcel, 26, 8);
        parcel.writeLong(this.f188I);
        H.a.v(parcel, r4);
    }
}
