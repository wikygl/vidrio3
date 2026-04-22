package t1;

import A1.D0;
import A1.r1;
import android.os.RemoteException;

/* renamed from: t1.o  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0813o {

    /* renamed from: a  reason: collision with root package name */
    public final Object f5808a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public D0 f5809b;

    /* renamed from: c  reason: collision with root package name */
    public a f5810c;

    public final void a(a aVar) {
        r1 r1Var;
        synchronized (this.f5808a) {
            this.f5810c = aVar;
            D0 d02 = this.f5809b;
            if (d02 == null) {
                return;
            }
            if (aVar == null) {
                r1Var = null;
            } else {
                try {
                    r1Var = new r1(aVar);
                } catch (RemoteException e4) {
                    E1.m.e("Unable to call setVideoLifecycleCallbacks on video controller.", e4);
                }
            }
            d02.h1(r1Var);
        }
    }

    public final void b(D0 d02) {
        synchronized (this.f5808a) {
            try {
                this.f5809b = d02;
                a aVar = this.f5810c;
                if (aVar != null) {
                    a(aVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* renamed from: t1.o$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static abstract class a {
        public void a() {
        }

        public void b() {
        }

        public void c() {
        }
    }
}
