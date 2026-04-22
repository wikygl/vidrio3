package D1;

import i2.C0466l;
import i2.C0467m;

/* renamed from: D1.n  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class RunnableC0194n implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f742j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f743k;

    public /* synthetic */ RunnableC0194n(int i4, Object obj) {
        this.f742j = i4;
        this.f743k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f742j) {
            case 0:
                C0195o c0195o = (C0195o) this.f743k;
                c0195o.getClass();
                z1.p.f6575A.f6587m.a(c0195o.f745a);
                return;
            default:
                i2.b0 b0Var = new i2.b0("Web view timed out.", 4);
                C0466l c0466l = (C0466l) ((C0467m) this.f743k).f3779i.getAndSet(null);
                if (c0466l != null) {
                    c0466l.b(b0Var.a());
                    return;
                }
                return;
        }
    }
}
