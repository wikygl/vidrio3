package W1;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class B implements Parcelable.Creator {
    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        int o4 = X1.c.o(parcel);
        Account account = null;
        GoogleSignInAccount googleSignInAccount = null;
        int i4 = 0;
        int i5 = 0;
        while (parcel.dataPosition() < o4) {
            int readInt = parcel.readInt();
            char c4 = (char) readInt;
            if (c4 != 1) {
                if (c4 != 2) {
                    if (c4 != 3) {
                        if (c4 != 4) {
                            X1.c.n(parcel, readInt);
                        } else {
                            googleSignInAccount = (GoogleSignInAccount) X1.c.c(parcel, readInt, GoogleSignInAccount.CREATOR);
                        }
                    } else {
                        i5 = X1.c.k(parcel, readInt);
                    }
                } else {
                    account = (Account) X1.c.c(parcel, readInt, Account.CREATOR);
                }
            } else {
                i4 = X1.c.k(parcel, readInt);
            }
        }
        X1.c.h(parcel, o4);
        return new A(i4, account, i5, googleSignInAccount);
    }

    @Override // android.os.Parcelable.Creator
    public final /* synthetic */ Object[] newArray(int i4) {
        return new A[i4];
    }
}
