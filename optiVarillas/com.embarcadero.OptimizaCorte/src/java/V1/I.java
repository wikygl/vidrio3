package V1;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import p2.C0758g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class I extends A {

    /* renamed from: b  reason: collision with root package name */
    public final C0758g f2551b;

    public I(C0758g c0758g) {
        super(4);
        this.f2551b = c0758g;
    }

    @Override // V1.L
    public final void a(Status status) {
        this.f2551b.a(new U1.b(status));
    }

    @Override // V1.L
    public final void b(RuntimeException runtimeException) {
        this.f2551b.a(runtimeException);
    }

    @Override // V1.L
    public final void c(u uVar) {
        try {
            h(uVar);
        } catch (DeadObjectException e4) {
            a(L.e(e4));
            throw e4;
        } catch (RemoteException e5) {
            a(L.e(e5));
        } catch (RuntimeException e6) {
            this.f2551b.a(e6);
        }
    }

    public abstract void h(u uVar);
}
