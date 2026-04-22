package A1;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.internal.ads.x8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class P extends x8 implements S {
    public P(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IAppEventListener");
    }

    @Override // A1.S
    public final void K2(String str, String str2) {
        Parcel B4 = B();
        B4.writeString(str);
        B4.writeString(str2);
        p0(B4, 1);
    }
}
