package w1;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/* renamed from: w1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0837a extends X1.a {
    public static final Parcelable.Creator<C0837a> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final boolean f6386j;

    /* renamed from: k  reason: collision with root package name */
    public final IBinder f6387k;

    public C0837a(boolean z4, IBinder iBinder) {
        this.f6386j = z4;
        this.f6387k = iBinder;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f6386j ? 1 : 0);
        H.a.j(parcel, 2, this.f6387k);
        H.a.v(parcel, r4);
    }
}
