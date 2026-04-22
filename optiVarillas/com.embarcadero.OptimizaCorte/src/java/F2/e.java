package f2;

import android.os.IBinder;
import android.os.IInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class e implements IInterface {

    /* renamed from: j  reason: collision with root package name */
    public final IBinder f3399j;

    /* renamed from: k  reason: collision with root package name */
    public final String f3400k = "com.google.android.gms.appset.internal.IAppSetService";

    public e(IBinder iBinder) {
        this.f3399j = iBinder;
    }

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this.f3399j;
    }
}
