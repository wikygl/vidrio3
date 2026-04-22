package A1;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.internal.ads.gA;
import t1.C0807i;
import t1.C0811m;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class N0 extends X1.a {
    public static final Parcelable.Creator<N0> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f62j;

    /* renamed from: k  reason: collision with root package name */
    public final String f63k;

    /* renamed from: l  reason: collision with root package name */
    public final String f64l;

    /* renamed from: m  reason: collision with root package name */
    public N0 f65m;

    /* renamed from: n  reason: collision with root package name */
    public IBinder f66n;

    public N0(int i4, String str, String str2, N0 n02, IBinder iBinder) {
        this.f62j = i4;
        this.f63k = str;
        this.f64l = str2;
        this.f65m = n02;
        this.f66n = iBinder;
    }

    public final gA h() {
        N0 n02 = this.f65m;
        gA gAVar = null;
        if (n02 != null) {
            gAVar = new gA(n02.f62j, n02.f63k, n02.f64l, (gA) null);
        }
        return new gA(this.f62j, this.f63k, this.f64l, gAVar);
    }

    public final C0807i i() {
        gA gAVar;
        A0 c0144z0;
        N0 n02 = this.f65m;
        C0811m c0811m = null;
        if (n02 == null) {
            gAVar = null;
        } else {
            gAVar = new gA(n02.f62j, n02.f63k, n02.f64l, (gA) null);
        }
        IBinder iBinder = this.f66n;
        if (iBinder == null) {
            c0144z0 = null;
        } else {
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IResponseInfo");
            if (queryLocalInterface instanceof A0) {
                c0144z0 = (A0) queryLocalInterface;
            } else {
                c0144z0 = new C0144z0(iBinder);
            }
        }
        if (c0144z0 != null) {
            c0811m = new C0811m(c0144z0);
        }
        return new C0807i(this.f62j, this.f63k, this.f64l, gAVar, c0811m);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f62j);
        H.a.m(parcel, 2, this.f63k);
        H.a.m(parcel, 3, this.f64l);
        H.a.l(parcel, 4, this.f65m, i4);
        H.a.j(parcel, 5, this.f66n);
        H.a.v(parcel, r4);
    }
}
