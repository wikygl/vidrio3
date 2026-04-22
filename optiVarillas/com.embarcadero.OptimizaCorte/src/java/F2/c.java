package f2;

import W1.AbstractC0318f;
import android.os.IBinder;
import android.os.IInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class c extends AbstractC0318f<e> {
    @Override // W1.AbstractC0314b, U1.a.e
    public final int f() {
        return 212800000;
    }

    @Override // W1.AbstractC0314b
    public final /* synthetic */ IInterface r(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.appset.internal.IAppSetService");
        if (queryLocalInterface instanceof e) {
            return (e) queryLocalInterface;
        }
        return new e(iBinder);
    }

    @Override // W1.AbstractC0314b
    public final T1.d[] t() {
        return Q1.g.f2020b;
    }

    @Override // W1.AbstractC0314b
    public final String x() {
        return "com.google.android.gms.appset.internal.IAppSetService";
    }

    @Override // W1.AbstractC0314b
    public final String y() {
        return "com.google.android.gms.appset.service.START";
    }

    @Override // W1.AbstractC0314b
    public final boolean z() {
        return true;
    }
}
