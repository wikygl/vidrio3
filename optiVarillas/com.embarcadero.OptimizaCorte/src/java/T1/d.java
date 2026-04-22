package T1;

import W1.C0323k;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class d extends X1.a {
    public static final Parcelable.Creator<d> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final String f2348j;
    @Deprecated

    /* renamed from: k  reason: collision with root package name */
    public final int f2349k;

    /* renamed from: l  reason: collision with root package name */
    public final long f2350l;

    public d(int i4, long j4, String str) {
        this.f2348j = str;
        this.f2349k = i4;
        this.f2350l = j4;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof d) {
            d dVar = (d) obj;
            String str = this.f2348j;
            if (((str != null && str.equals(dVar.f2348j)) || (str == null && dVar.f2348j == null)) && h() == dVar.h()) {
                return true;
            }
        }
        return false;
    }

    public final long h() {
        long j4 = this.f2350l;
        if (j4 == -1) {
            return this.f2349k;
        }
        return j4;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.f2348j, Long.valueOf(h())});
    }

    public final String toString() {
        C0323k.a aVar = new C0323k.a(this);
        aVar.a(this.f2348j, "name");
        aVar.a(Long.valueOf(h()), "version");
        return aVar.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.m(parcel, 1, this.f2348j);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f2349k);
        long h4 = h();
        H.a.x(parcel, 3, 8);
        parcel.writeLong(h4);
        H.a.v(parcel, r4);
    }

    public d(String str) {
        this.f2348j = str;
        this.f2350l = 1L;
        this.f2349k = -1;
    }
}
