package F3;

import C3.AbstractC0171v;
import C3.C0173x;
import C3.E;
import C3.H;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class j extends AbstractC0171v implements H {

    /* renamed from: q  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f922q = AtomicIntegerFieldUpdater.newUpdater(j.class, "runningWorkers");

    /* renamed from: l  reason: collision with root package name */
    public final AbstractC0171v f923l;

    /* renamed from: m  reason: collision with root package name */
    public final int f924m;

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ H f925n;

    /* renamed from: o  reason: collision with root package name */
    public final n<Runnable> f926o;

    /* renamed from: p  reason: collision with root package name */
    public final Object f927p;
    private volatile int runningWorkers;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public final class a implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public Runnable f928j;

        public a(Runnable runnable) {
            this.f928j = runnable;
        }

        @Override // java.lang.Runnable
        public final void run() {
            int i4 = 0;
            while (true) {
                try {
                    this.f928j.run();
                } catch (Throwable th) {
                    C0173x.a(n3.g.f5388j, th);
                }
                j jVar = j.this;
                Runnable H4 = jVar.H();
                if (H4 == null) {
                    return;
                }
                this.f928j = H4;
                i4++;
                if (i4 >= 16) {
                    AbstractC0171v abstractC0171v = jVar.f923l;
                    if (abstractC0171v.G()) {
                        abstractC0171v.F(jVar, this);
                        return;
                    }
                }
            }
        }
    }

    public j(AbstractC0171v abstractC0171v, int i4) {
        H h4;
        this.f923l = abstractC0171v;
        this.f924m = i4;
        if (abstractC0171v instanceof H) {
            h4 = (H) abstractC0171v;
        } else {
            h4 = null;
        }
        this.f925n = h4 == null ? E.f428a : h4;
        this.f926o = new n<>();
        this.f927p = new Object();
    }

    @Override // C3.AbstractC0171v
    public final void F(n3.f fVar, Runnable runnable) {
        this.f926o.a(runnable);
        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater = f922q;
        if (atomicIntegerFieldUpdater.get(this) < this.f924m) {
            synchronized (this.f927p) {
                if (atomicIntegerFieldUpdater.get(this) < this.f924m) {
                    atomicIntegerFieldUpdater.incrementAndGet(this);
                    Runnable H4 = H();
                    if (H4 != null) {
                        this.f923l.F(this, new a(H4));
                    }
                }
            }
        }
    }

    public final Runnable H() {
        while (true) {
            Runnable d4 = this.f926o.d();
            if (d4 == null) {
                synchronized (this.f927p) {
                    AtomicIntegerFieldUpdater atomicIntegerFieldUpdater = f922q;
                    atomicIntegerFieldUpdater.decrementAndGet(this);
                    if (this.f926o.c() == 0) {
                        return null;
                    }
                    atomicIntegerFieldUpdater.incrementAndGet(this);
                }
            } else {
                return d4;
            }
        }
    }
}
