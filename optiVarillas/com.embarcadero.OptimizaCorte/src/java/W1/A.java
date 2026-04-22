package W1;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class A extends X1.a {
    public static final Parcelable.Creator<A> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f2632j;

    /* renamed from: k  reason: collision with root package name */
    public final Account f2633k;

    /* renamed from: l  reason: collision with root package name */
    public final int f2634l;

    /* renamed from: m  reason: collision with root package name */
    public final GoogleSignInAccount f2635m;

    public A(int i4, Account account, int i5, GoogleSignInAccount googleSignInAccount) {
        this.f2632j = i4;
        this.f2633k = account;
        this.f2634l = i5;
        this.f2635m = googleSignInAccount;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f2632j);
        H.a.l(parcel, 2, this.f2633k, i4);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(this.f2634l);
        H.a.l(parcel, 4, this.f2635m, i4);
        H.a.v(parcel, r4);
    }
}
