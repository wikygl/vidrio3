package o2;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Status;
import java.util.ArrayList;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class h extends X1.a implements U1.h {
    public static final Parcelable.Creator<h> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final List f5493j;

    /* renamed from: k  reason: collision with root package name */
    public final String f5494k;

    public h(ArrayList arrayList, String str) {
        this.f5493j = arrayList;
        this.f5494k = str;
    }

    @Override // U1.h
    public final Status b() {
        if (this.f5494k != null) {
            return Status.n;
        }
        return Status.p;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.o(parcel, 1, this.f5493j);
        H.a.m(parcel, 2, this.f5494k);
        H.a.v(parcel, r4);
    }
}
