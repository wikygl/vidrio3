package Y1;

import V1.InterfaceC0297c;
import V1.InterfaceC0303i;
import W1.AbstractC0318f;
import W1.C0315c;
import W1.C0328p;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import g2.C0425a;
import g2.e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class d extends AbstractC0318f {

    /* renamed from: A  reason: collision with root package name */
    public final C0328p f2830A;

    public d(Context context, Looper looper, C0315c c0315c, C0328p c0328p, InterfaceC0297c interfaceC0297c, InterfaceC0303i interfaceC0303i) {
        super(context, looper, 270, c0315c, interfaceC0297c, interfaceC0303i);
        this.f2830A = c0328p;
    }

    @Override // W1.AbstractC0314b, U1.a.e
    public final int f() {
        return 203400000;
    }

    @Override // W1.AbstractC0314b
    public final IInterface r(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.service.IClientTelemetryService");
        if (queryLocalInterface instanceof a) {
            return (a) queryLocalInterface;
        }
        return new C0425a(iBinder, "com.google.android.gms.common.internal.service.IClientTelemetryService");
    }

    @Override // W1.AbstractC0314b
    public final T1.d[] t() {
        return e.f3500b;
    }

    @Override // W1.AbstractC0314b
    public final Bundle u() {
        C0328p c0328p = this.f2830A;
        c0328p.getClass();
        Bundle bundle = new Bundle();
        String str = c0328p.f2767b;
        if (str != null) {
            bundle.putString("api", str);
        }
        return bundle;
    }

    @Override // W1.AbstractC0314b
    public final String x() {
        return "com.google.android.gms.common.internal.service.IClientTelemetryService";
    }

    @Override // W1.AbstractC0314b
    public final String y() {
        return "com.google.android.gms.common.telemetry.service.START";
    }

    @Override // W1.AbstractC0314b
    public final boolean z() {
        return true;
    }
}
