package A1;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class E0 extends x8 implements G0 {
    public E0(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IVideoLifecycleCallbacks");
    }

    @Override // A1.G0
    public final void Q2(boolean z4) {
        Parcel B4 = B();
        ClassLoader classLoader = z8.a;
        B4.writeInt(z4 ? 1 : 0);
        p0(B4, 5);
    }

    @Override // A1.G0
    public final void b() {
        p0(B(), 4);
    }

    @Override // A1.G0
    public final void f() {
        p0(B(), 1);
    }

    @Override // A1.G0
    public final void g() {
        p0(B(), 2);
    }

    @Override // A1.G0
    public final void h() {
        p0(B(), 3);
    }
}
