package A1;

import android.os.Parcel;
import android.os.Parcelable;
import t1.C0814p;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class s1 extends X1.a {
    public static final Parcelable.Creator<s1> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final boolean f174j;

    /* renamed from: k  reason: collision with root package name */
    public final boolean f175k;

    /* renamed from: l  reason: collision with root package name */
    public final boolean f176l;

    public s1(C0814p c0814p) {
        this(c0814p.f5811a, c0814p.f5812b, c0814p.f5813c);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f174j ? 1 : 0);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(this.f175k ? 1 : 0);
        H.a.x(parcel, 4, 4);
        parcel.writeInt(this.f176l ? 1 : 0);
        H.a.v(parcel, r4);
    }

    public s1(boolean z4, boolean z5, boolean z6) {
        this.f174j = z4;
        this.f175k = z5;
        this.f176l = z6;
    }
}
