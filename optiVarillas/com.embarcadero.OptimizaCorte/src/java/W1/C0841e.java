package w1;

import A1.P;
import A1.S;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.internal.ads.L8;

@Deprecated
/* renamed from: w1.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0841e extends X1.a {
    public static final Parcelable.Creator<C0841e> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final boolean f6400j;

    /* renamed from: k  reason: collision with root package name */
    public final S f6401k;

    /* renamed from: l  reason: collision with root package name */
    public final IBinder f6402l;

    public C0841e(boolean z4, IBinder iBinder, IBinder iBinder2) {
        S s4;
        this.f6400j = z4;
        if (iBinder != null) {
            int i4 = L8.k;
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAppEventListener");
            if (queryLocalInterface instanceof S) {
                s4 = (S) queryLocalInterface;
            } else {
                s4 = new P(iBinder);
            }
        } else {
            s4 = null;
        }
        this.f6401k = s4;
        this.f6402l = iBinder2;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        IBinder asBinder;
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f6400j ? 1 : 0);
        S s4 = this.f6401k;
        if (s4 == null) {
            asBinder = null;
        } else {
            asBinder = s4.asBinder();
        }
        H.a.j(parcel, 2, asBinder);
        H.a.j(parcel, 3, this.f6402l);
        H.a.v(parcel, r4);
    }
}
