package W1;

import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: W1.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0316d extends X1.a {
    public static final Parcelable.Creator<C0316d> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final C0326n f2717j;

    /* renamed from: k  reason: collision with root package name */
    public final boolean f2718k;

    /* renamed from: l  reason: collision with root package name */
    public final boolean f2719l;

    /* renamed from: m  reason: collision with root package name */
    public final int[] f2720m;

    /* renamed from: n  reason: collision with root package name */
    public final int f2721n;

    /* renamed from: o  reason: collision with root package name */
    public final int[] f2722o;

    public C0316d(C0326n c0326n, boolean z4, boolean z5, int[] iArr, int i4, int[] iArr2) {
        this.f2717j = c0326n;
        this.f2718k = z4;
        this.f2719l = z5;
        this.f2720m = iArr;
        this.f2721n = i4;
        this.f2722o = iArr2;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.l(parcel, 1, this.f2717j, i4);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f2718k ? 1 : 0);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(this.f2719l ? 1 : 0);
        int[] iArr = this.f2720m;
        if (iArr != null) {
            int r5 = H.a.r(parcel, 4);
            parcel.writeIntArray(iArr);
            H.a.v(parcel, r5);
        }
        H.a.x(parcel, 5, 4);
        parcel.writeInt(this.f2721n);
        int[] iArr2 = this.f2722o;
        if (iArr2 != null) {
            int r6 = H.a.r(parcel, 6);
            parcel.writeIntArray(iArr2);
            H.a.v(parcel, r6);
        }
        H.a.v(parcel, r4);
    }
}
