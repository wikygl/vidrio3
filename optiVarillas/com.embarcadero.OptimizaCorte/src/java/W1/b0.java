package W1;

import android.accounts.Account;
import android.os.Parcel;
import h2.C0438a;
import h2.C0440c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class b0 extends C0438a implements InterfaceC0320h {
    @Override // W1.InterfaceC0320h
    public final Account c() {
        Parcel B4 = B(Z(), 2);
        Account account = (Account) C0440c.a(B4, Account.CREATOR);
        B4.recycle();
        return account;
    }
}
