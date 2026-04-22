package C1;

import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import c2.InterfaceC0374a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class i extends X1.a {
    public static final Parcelable.Creator<i> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f363j;

    /* renamed from: k  reason: collision with root package name */
    public final String f364k;

    /* renamed from: l  reason: collision with root package name */
    public final String f365l;

    /* renamed from: m  reason: collision with root package name */
    public final String f366m;

    /* renamed from: n  reason: collision with root package name */
    public final String f367n;

    /* renamed from: o  reason: collision with root package name */
    public final String f368o;

    /* renamed from: p  reason: collision with root package name */
    public final String f369p;

    /* renamed from: q  reason: collision with root package name */
    public final Intent f370q;

    /* renamed from: r  reason: collision with root package name */
    public final C f371r;

    /* renamed from: s  reason: collision with root package name */
    public final boolean f372s;

    public i(Intent intent, C c4) {
        this(null, null, null, null, null, null, null, intent, new c2.b(c4), false);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 2, this.f363j);
        H.a.m(parcel, 3, this.f364k);
        H.a.m(parcel, 4, this.f365l);
        H.a.m(parcel, 5, this.f366m);
        H.a.m(parcel, 6, this.f367n);
        H.a.m(parcel, 7, this.f368o);
        H.a.m(parcel, 8, this.f369p);
        H.a.l(parcel, 9, this.f370q, i4);
        H.a.j(parcel, 10, new c2.b(this.f371r));
        H.a.x(parcel, 11, 4);
        parcel.writeInt(this.f372s ? 1 : 0);
        H.a.v(parcel, r4);
    }

    public i(String str, String str2, String str3, String str4, String str5, String str6, String str7, C c4) {
        this(str, str2, str3, str4, str5, str6, str7, null, new c2.b(c4), false);
    }

    public i(String str, String str2, String str3, String str4, String str5, String str6, String str7, Intent intent, IBinder iBinder, boolean z4) {
        this.f363j = str;
        this.f364k = str2;
        this.f365l = str3;
        this.f366m = str4;
        this.f367n = str5;
        this.f368o = str6;
        this.f369p = str7;
        this.f370q = intent;
        this.f371r = (C) c2.b.p0(InterfaceC0374a.AbstractBinderC0042a.Z(iBinder));
        this.f372s = z4;
    }
}
