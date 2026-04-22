package W1;

import W1.AbstractC0314b;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class O extends E {

    /* renamed from: g  reason: collision with root package name */
    public final IBinder f2655g;

    /* renamed from: h  reason: collision with root package name */
    public final /* synthetic */ AbstractC0314b f2656h;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public O(AbstractC0314b abstractC0314b, int i4, IBinder iBinder, Bundle bundle) {
        super(abstractC0314b, i4, bundle);
        this.f2656h = abstractC0314b;
        this.f2655g = iBinder;
    }

    @Override // W1.E
    public final void d(T1.b bVar) {
        AbstractC0314b.InterfaceC0032b interfaceC0032b = this.f2656h.f2697p;
        if (interfaceC0032b != null) {
            interfaceC0032b.p0(bVar);
        }
        System.currentTimeMillis();
    }

    @Override // W1.E
    public final boolean e() {
        IBinder iBinder = this.f2655g;
        try {
            C0324l.d(iBinder);
            String interfaceDescriptor = iBinder.getInterfaceDescriptor();
            AbstractC0314b abstractC0314b = this.f2656h;
            if (!abstractC0314b.x().equals(interfaceDescriptor)) {
                String x4 = abstractC0314b.x();
                Log.w("GmsClient", "service descriptor mismatch: " + x4 + " vs. " + interfaceDescriptor);
                return false;
            }
            IInterface r4 = abstractC0314b.r(iBinder);
            if (r4 == null || (!AbstractC0314b.B(abstractC0314b, 2, 4, r4) && !AbstractC0314b.B(abstractC0314b, 3, 4, r4))) {
                return false;
            }
            abstractC0314b.f2701t = null;
            AbstractC0314b.a aVar = abstractC0314b.f2696o;
            if (aVar != null) {
                aVar.Z();
                return true;
            }
            return true;
        } catch (RemoteException unused) {
            Log.w("GmsClient", "service probably died");
            return false;
        }
    }
}
