package A1;

import android.os.IBinder;
import android.os.IInterface;

/* renamed from: A1.a1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0086a1 extends c2.c {
    @Override // c2.c
    public final /* synthetic */ Object a(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IMobileAdsSettingManagerCreator");
        if (queryLocalInterface instanceof C0103g0) {
            return (C0103g0) queryLocalInterface;
        }
        return new C0103g0(iBinder);
    }
}
