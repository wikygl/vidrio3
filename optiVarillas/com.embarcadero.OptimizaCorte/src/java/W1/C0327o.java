package W1;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/* renamed from: W1.o  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0327o extends X1.a {
    public static final Parcelable.Creator<C0327o> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f2764j;

    /* renamed from: k  reason: collision with root package name */
    public List f2765k;

    public C0327o(int i4, List list) {
        this.f2764j = i4;
        this.f2765k = list;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f2764j);
        H.a.q(parcel, 2, this.f2765k);
        H.a.v(parcel, r4);
    }
}
