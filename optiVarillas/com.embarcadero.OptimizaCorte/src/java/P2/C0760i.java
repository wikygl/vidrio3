package p2;

import com.google.android.gms.internal.ads.sm;
import java.util.concurrent.Executor;

/* renamed from: p2.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0760i implements n {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f5555j;

    /* renamed from: k  reason: collision with root package name */
    public final Executor f5556k;

    /* renamed from: l  reason: collision with root package name */
    public final Object f5557l;

    /* renamed from: m  reason: collision with root package name */
    public final Object f5558m;

    public C0760i(Executor executor, InterfaceC0752a interfaceC0752a, q qVar) {
        this.f5555j = 0;
        this.f5556k = executor;
        this.f5557l = interfaceC0752a;
        this.f5558m = qVar;
    }

    @Override // p2.n
    public final void a(AbstractC0757f abstractC0757f) {
        switch (this.f5555j) {
            case 0:
                this.f5556k.execute(new sm(this, abstractC0757f, 7));
                return;
            default:
                if (abstractC0757f.k()) {
                    synchronized (this.f5557l) {
                        try {
                            if (((C0761j) this.f5558m) != null) {
                                this.f5556k.execute(new sm(this, abstractC0757f, 8));
                            }
                        } finally {
                        }
                    }
                    return;
                }
                return;
        }
    }

    public C0760i(o oVar, C0761j c0761j) {
        this.f5555j = 1;
        this.f5557l = new Object();
        this.f5556k = oVar;
        this.f5558m = c0761j;
    }
}
