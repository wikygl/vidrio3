package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.atomic.AtomicReference;

/* renamed from: j$.util.stream.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class AbstractC0526c extends AbstractC0536e {

    /* renamed from: h  reason: collision with root package name */
    protected final AtomicReference f4461h;

    /* renamed from: i  reason: collision with root package name */
    protected volatile boolean f4462i;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractC0526c(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        super(abstractC0521b, spliterator);
        this.f4461h = new AtomicReference(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractC0526c(AbstractC0526c abstractC0526c, Spliterator spliterator) {
        super(abstractC0526c, spliterator);
        this.f4461h = abstractC0526c.f4461h;
    }

    @Override // j$.util.stream.AbstractC0536e
    public final Object c() {
        if (d()) {
            Object obj = this.f4461h.get();
            return obj == null ? j() : obj;
        }
        return super.c();
    }

    @Override // j$.util.stream.AbstractC0536e, java.util.concurrent.CountedCompleter
    public final void compute() {
        Object obj;
        Spliterator trySplit;
        Spliterator spliterator = this.f4477b;
        long estimateSize = spliterator.estimateSize();
        long j4 = this.f4478c;
        if (j4 == 0) {
            j4 = AbstractC0536e.g(estimateSize);
            this.f4478c = j4;
        }
        AtomicReference atomicReference = this.f4461h;
        boolean z4 = false;
        AbstractC0526c abstractC0526c = this;
        while (true) {
            obj = atomicReference.get();
            if (obj != null) {
                break;
            }
            boolean z5 = abstractC0526c.f4462i;
            if (!z5) {
                CountedCompleter<?> completer = abstractC0526c.getCompleter();
                while (true) {
                    AbstractC0526c abstractC0526c2 = (AbstractC0526c) ((AbstractC0536e) completer);
                    if (z5 || abstractC0526c2 == null) {
                        break;
                    }
                    z5 = abstractC0526c2.f4462i;
                    completer = abstractC0526c2.getCompleter();
                }
            }
            if (z5) {
                obj = abstractC0526c.j();
                break;
            } else if (estimateSize <= j4 || (trySplit = spliterator.trySplit()) == null) {
                break;
            } else {
                AbstractC0526c abstractC0526c3 = (AbstractC0526c) abstractC0526c.e(trySplit);
                abstractC0526c.f4479d = abstractC0526c3;
                AbstractC0526c abstractC0526c4 = (AbstractC0526c) abstractC0526c.e(spliterator);
                abstractC0526c.f4480e = abstractC0526c4;
                abstractC0526c.setPendingCount(1);
                if (z4) {
                    spliterator = trySplit;
                    abstractC0526c = abstractC0526c3;
                    abstractC0526c3 = abstractC0526c4;
                } else {
                    abstractC0526c = abstractC0526c4;
                }
                z4 = !z4;
                abstractC0526c3.fork();
                estimateSize = spliterator.estimateSize();
            }
        }
        obj = abstractC0526c.a();
        abstractC0526c.f(obj);
        abstractC0526c.tryComplete();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final void f(Object obj) {
        if (!d()) {
            super.f(obj);
        } else if (obj != null) {
            AtomicReference atomicReference = this.f4461h;
            while (!atomicReference.compareAndSet(null, obj) && atomicReference.get() == null) {
            }
        }
    }

    @Override // j$.util.stream.AbstractC0536e, java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    public final Object getRawResult() {
        return c();
    }

    protected void h() {
        this.f4462i = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void i() {
        AbstractC0526c abstractC0526c = this;
        for (AbstractC0526c abstractC0526c2 = (AbstractC0526c) ((AbstractC0536e) getCompleter()); abstractC0526c2 != null; abstractC0526c2 = (AbstractC0526c) ((AbstractC0536e) abstractC0526c2.getCompleter())) {
            if (abstractC0526c2.f4479d == abstractC0526c) {
                AbstractC0526c abstractC0526c3 = (AbstractC0526c) abstractC0526c2.f4480e;
                if (!abstractC0526c3.f4462i) {
                    abstractC0526c3.h();
                }
            }
            abstractC0526c = abstractC0526c2;
        }
    }

    protected abstract Object j();
}
