package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class M extends AbstractC0526c {

    /* renamed from: j  reason: collision with root package name */
    private final G f4326j;

    /* renamed from: k  reason: collision with root package name */
    private final boolean f4327k;

    /* JADX INFO: Access modifiers changed from: package-private */
    public M(G g4, boolean z4, AbstractC0521b abstractC0521b, Spliterator spliterator) {
        super(abstractC0521b, spliterator);
        this.f4327k = z4;
        this.f4326j = g4;
    }

    M(M m4, Spliterator spliterator) {
        super(m4, spliterator);
        this.f4327k = m4.f4327k;
        this.f4326j = m4.f4326j;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final Object a() {
        AbstractC0521b abstractC0521b = this.f4476a;
        L3 l32 = (L3) this.f4326j.f4283d.get();
        abstractC0521b.R(this.f4477b, l32);
        Object obj = l32.get();
        if (!this.f4327k) {
            if (obj != null) {
                AtomicReference atomicReference = this.f4461h;
                while (!atomicReference.compareAndSet(null, obj) && atomicReference.get() == null) {
                }
            }
            return null;
        } else if (obj != null) {
            AbstractC0536e abstractC0536e = this;
            while (true) {
                if (abstractC0536e != null) {
                    AbstractC0536e abstractC0536e2 = (AbstractC0536e) abstractC0536e.getCompleter();
                    if (abstractC0536e2 != null && abstractC0536e2.f4479d != abstractC0536e) {
                        i();
                        break;
                    }
                    abstractC0536e = abstractC0536e2;
                } else {
                    AtomicReference atomicReference2 = this.f4461h;
                    while (!atomicReference2.compareAndSet(null, obj) && atomicReference2.get() == null) {
                    }
                }
            }
            return obj;
        } else {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final AbstractC0536e e(Spliterator spliterator) {
        return new M(this, spliterator);
    }

    @Override // j$.util.stream.AbstractC0526c
    protected final Object j() {
        return this.f4326j.f4281b;
    }

    @Override // j$.util.stream.AbstractC0536e, java.util.concurrent.CountedCompleter
    public final void onCompletion(CountedCompleter countedCompleter) {
        if (this.f4327k) {
            M m4 = (M) this.f4479d;
            M m5 = null;
            while (true) {
                if (m4 != m5) {
                    Object c4 = m4.c();
                    if (c4 != null && this.f4326j.f4282c.test(c4)) {
                        f(c4);
                        AbstractC0536e abstractC0536e = this;
                        while (true) {
                            if (abstractC0536e != null) {
                                AbstractC0536e abstractC0536e2 = (AbstractC0536e) abstractC0536e.getCompleter();
                                if (abstractC0536e2 != null && abstractC0536e2.f4479d != abstractC0536e) {
                                    i();
                                    break;
                                }
                                abstractC0536e = abstractC0536e2;
                            } else {
                                AtomicReference atomicReference = this.f4461h;
                                while (!atomicReference.compareAndSet(null, c4) && atomicReference.get() == null) {
                                }
                            }
                        }
                    } else {
                        m5 = m4;
                        m4 = (M) this.f4480e;
                    }
                } else {
                    break;
                }
            }
        }
        super.onCompletion(countedCompleter);
    }
}
