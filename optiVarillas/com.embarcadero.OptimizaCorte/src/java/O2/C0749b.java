package o2;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Status;

/* renamed from: o2.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0749b extends X1.a implements U1.h {
    public static final Parcelable.Creator<C0749b> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f5490j;

    /* renamed from: k  reason: collision with root package name */
    public final int f5491k;

    /* renamed from: l  reason: collision with root package name */
    public final Intent f5492l;

    public C0749b() {
        this(2, 0, null);
    }

    @Override // U1.h
    public final Status b() {
        if (this.f5491k == 0) {
            return Status.n;
        }
        return Status.p;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f5490j);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(this.f5491k);
        H.a.l(parcel, 3, this.f5492l, i4);
        H.a.v(parcel, r4);
    }

    public C0749b(int i4, int i5, Intent intent) {
        this.f5490j = i4;
        this.f5491k = i5;
        this.f5492l = intent;
    }
}
