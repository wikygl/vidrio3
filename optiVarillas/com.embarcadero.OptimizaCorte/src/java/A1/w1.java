package A1;

import android.os.IBinder;
import android.os.IInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class w1 extends c2.c {
    @Override // c2.c
    public final /* synthetic */ Object a(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IAdLoaderBuilderCreator");
        if (queryLocalInterface instanceof H) {
            return (H) queryLocalInterface;
        }
        return new H(iBinder);
    }
}
