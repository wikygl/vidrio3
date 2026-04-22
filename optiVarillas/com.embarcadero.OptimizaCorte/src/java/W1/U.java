package W1;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Scope;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class U implements Parcelable.Creator {
    public static void a(C0317e c0317e, Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        int i5 = c0317e.f2725j;
        H.a.x(parcel, 1, 4);
        parcel.writeInt(i5);
        H.a.x(parcel, 2, 4);
        parcel.writeInt(c0317e.f2726k);
        H.a.x(parcel, 3, 4);
        parcel.writeInt(c0317e.f2727l);
        H.a.m(parcel, 4, c0317e.f2728m);
        H.a.j(parcel, 5, c0317e.f2729n);
        H.a.p(parcel, 6, c0317e.f2730o, i4);
        H.a.h(parcel, 7, c0317e.f2731p);
        H.a.l(parcel, 8, c0317e.f2732q, i4);
        H.a.p(parcel, 10, c0317e.f2733r, i4);
        H.a.p(parcel, 11, c0317e.f2734s, i4);
        H.a.x(parcel, 12, 4);
        parcel.writeInt(c0317e.f2735t ? 1 : 0);
        H.a.x(parcel, 13, 4);
        parcel.writeInt(c0317e.f2736u);
        boolean z4 = c0317e.f2737v;
        H.a.x(parcel, 14, 4);
        parcel.writeInt(z4 ? 1 : 0);
        H.a.m(parcel, 15, c0317e.f2738w);
        H.a.v(parcel, r4);
    }

    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        Scope[] scopeArr = C0317e.f2723x;
        Bundle bundle = new Bundle();
        T1.d[] dVarArr = C0317e.f2724y;
        T1.d[] dVarArr2 = dVarArr;
        String str = null;
        IBinder iBinder = null;
        Account account = null;
        String str2 = null;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        boolean z4 = false;
        int i7 = 0;
        boolean z5 = false;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            switch ((char) readInt) {
                case 1:
                    i4 = X1.c.k(parcel, readInt);
                    break;
                case 2:
                    i5 = X1.c.k(parcel, readInt);
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    i6 = X1.c.k(parcel, readInt);
                    break;
                case 4:
                    str = X1.c.d(parcel, readInt);
                    break;
                case 5:
                    iBinder = X1.c.j(parcel, readInt);
                    break;
                case 6:
                    scopeArr = (Scope[]) X1.c.g(parcel, readInt, Scope.CREATOR);
                    break;
                case 7:
                    bundle = X1.c.a(parcel, readInt);
                    break;
                case '\b':
                    account = (Account) X1.c.c(parcel, readInt, Account.CREATOR);
                    break;
                case '\t':
                default:
                    X1.c.n(parcel, readInt);
                    break;
                case '\n':
                    dVarArr = (T1.d[]) X1.c.g(parcel, readInt, T1.d.CREATOR);
                    break;
                case 11:
                    dVarArr2 = (T1.d[]) X1.c.g(parcel, readInt, T1.d.CREATOR);
                    break;
                case '\f':
                    z4 = X1.c.i(parcel, readInt);
                    break;
                case '\r':
                    i7 = X1.c.k(parcel, readInt);
                    break;
                case 14:
                    z5 = X1.c.i(parcel, readInt);
                    break;
                case 15:
                    str2 = X1.c.d(parcel, readInt);
                    break;
            }
        }
        X1.c.h(parcel, o4);
        return new C0317e(i4, i5, i6, str, iBinder, scopeArr, bundle, account, dVarArr, dVarArr2, z4, i7, z5, str2);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new C0317e[i4];
    }
}
