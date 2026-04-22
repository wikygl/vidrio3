package W1;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class N implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    public final int f2653a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ AbstractC0314b f2654b;

    public N(AbstractC0314b abstractC0314b, int i4) {
        this.f2654b = abstractC0314b;
        this.f2653a = i4;
    }

    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        InterfaceC0321i h4;
        AbstractC0314b abstractC0314b = this.f2654b;
        if (iBinder == null) {
            AbstractC0314b.A(abstractC0314b);
            return;
        }
        synchronized (abstractC0314b.f2689h) {
            try {
                AbstractC0314b abstractC0314b2 = this.f2654b;
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IGmsServiceBroker");
                if (queryLocalInterface != null && (queryLocalInterface instanceof InterfaceC0321i)) {
                    h4 = (InterfaceC0321i) queryLocalInterface;
                } else {
                    h4 = new H(iBinder);
                }
                abstractC0314b2.f2690i = h4;
            } catch (Throwable th) {
                throw th;
            }
        }
        AbstractC0314b abstractC0314b3 = this.f2654b;
        int i4 = this.f2653a;
        abstractC0314b3.getClass();
        P p4 = new P(abstractC0314b3, 0);
        K k4 = abstractC0314b3.f;
        k4.sendMessage(k4.obtainMessage(7, i4, -1, p4));
    }

    @Override // android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName componentName) {
        AbstractC0314b abstractC0314b;
        synchronized (this.f2654b.f2689h) {
            abstractC0314b = this.f2654b;
            abstractC0314b.f2690i = null;
        }
        int i4 = this.f2653a;
        K k4 = abstractC0314b.f;
        k4.sendMessage(k4.obtainMessage(6, i4, 1));
    }
}
