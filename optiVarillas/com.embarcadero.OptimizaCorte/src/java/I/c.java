package I;

@Deprecated
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class c {

    /* renamed from: a  reason: collision with root package name */
    public boolean f1130a;

    /* renamed from: b  reason: collision with root package name */
    public a f1131b;

    /* renamed from: c  reason: collision with root package name */
    public boolean f1132c;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public interface a {
        void a();
    }

    public final void a() {
        synchronized (this) {
            try {
                if (this.f1130a) {
                    return;
                }
                this.f1130a = true;
                this.f1132c = true;
                a aVar = this.f1131b;
                if (aVar != null) {
                    try {
                        aVar.a();
                    } catch (Throwable th) {
                        synchronized (this) {
                            this.f1132c = false;
                            notifyAll();
                            throw th;
                        }
                    }
                }
                synchronized (this) {
                    this.f1132c = false;
                    notifyAll();
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public final void b(a aVar) {
        synchronized (this) {
            while (this.f1132c) {
                try {
                    try {
                        wait();
                    } catch (InterruptedException unused) {
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (this.f1131b == aVar) {
                return;
            }
            this.f1131b = aVar;
            if (this.f1130a) {
                aVar.a();
            }
        }
    }
}
