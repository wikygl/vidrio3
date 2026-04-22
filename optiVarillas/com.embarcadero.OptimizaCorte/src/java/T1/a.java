package T1;

import W1.C0324l;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.errorprone.annotations.ResultIgnorabilityUnspecified;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    public boolean f2338a = false;

    /* renamed from: b  reason: collision with root package name */
    public final LinkedBlockingQueue f2339b = new LinkedBlockingQueue();

    @ResultIgnorabilityUnspecified
    public final IBinder a(TimeUnit timeUnit) {
        C0324l.c("BlockingServiceConnection.getServiceWithTimeout() called on main thread");
        if (!this.f2338a) {
            this.f2338a = true;
            IBinder iBinder = (IBinder) this.f2339b.poll(10000L, timeUnit);
            if (iBinder != null) {
                return iBinder;
            }
            throw new TimeoutException("Timed out waiting for the service connection");
        }
        throw new IllegalStateException("Cannot call get on this connection more than once");
    }

    @Override // android.content.ServiceConnection
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.f2339b.add(iBinder);
    }

    @Override // android.content.ServiceConnection
    public final void onServiceDisconnected(ComponentName componentName) {
    }
}
