package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class T extends CountedCompleter {

    /* renamed from: a  reason: collision with root package name */
    private final AbstractC0521b f4400a;

    /* renamed from: b  reason: collision with root package name */
    private Spliterator f4401b;

    /* renamed from: c  reason: collision with root package name */
    private final long f4402c;

    /* renamed from: d  reason: collision with root package name */
    private final ConcurrentHashMap f4403d;

    /* renamed from: e  reason: collision with root package name */
    private final InterfaceC0599q2 f4404e;
    private final T f;

    /* renamed from: g  reason: collision with root package name */
    private L0 f4405g;

    T(T t3, Spliterator spliterator, T t4) {
        super(t3);
        this.f4400a = t3.f4400a;
        this.f4401b = spliterator;
        this.f4402c = t3.f4402c;
        this.f4403d = t3.f4403d;
        this.f4404e = t3.f4404e;
        this.f = t4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public T(AbstractC0521b abstractC0521b, Spliterator spliterator, InterfaceC0599q2 interfaceC0599q2) {
        super(null);
        this.f4400a = abstractC0521b;
        this.f4401b = spliterator;
        this.f4402c = AbstractC0536e.g(spliterator.estimateSize());
        this.f4403d = new ConcurrentHashMap(Math.max(16, AbstractC0536e.b() << 1));
        this.f4404e = interfaceC0599q2;
        this.f = null;
    }

    @Override // java.util.concurrent.CountedCompleter
    public final void compute() {
        Spliterator trySplit;
        Spliterator spliterator = this.f4401b;
        long j4 = this.f4402c;
        boolean z4 = false;
        T t3 = this;
        while (spliterator.estimateSize() > j4 && (trySplit = spliterator.trySplit()) != null) {
            T t4 = new T(t3, trySplit, t3.f);
            T t5 = new T(t3, spliterator, t4);
            t3.addToPendingCount(1);
            t5.addToPendingCount(1);
            t3.f4403d.put(t4, t5);
            if (t3.f != null) {
                t4.addToPendingCount(1);
                if (t3.f4403d.replace(t3.f, t3, t4)) {
                    t3.addToPendingCount(-1);
                } else {
                    t4.addToPendingCount(-1);
                }
            }
            if (z4) {
                spliterator = trySplit;
                t3 = t4;
                t4 = t5;
            } else {
                t3 = t5;
            }
            z4 = !z4;
            t4.fork();
        }
        if (t3.getPendingCount() > 0) {
            r rVar = new r(9);
            AbstractC0521b abstractC0521b = t3.f4400a;
            D0 J3 = abstractC0521b.J(abstractC0521b.C(spliterator), rVar);
            t3.f4400a.R(spliterator, J3);
            t3.f4405g = J3.a();
            t3.f4401b = null;
        }
        t3.tryComplete();
    }

    @Override // java.util.concurrent.CountedCompleter
    public final void onCompletion(CountedCompleter countedCompleter) {
        L0 l0 = this.f4405g;
        if (l0 != null) {
            l0.forEach(this.f4404e);
            this.f4405g = null;
        } else {
            Spliterator spliterator = this.f4401b;
            if (spliterator != null) {
                this.f4400a.R(spliterator, this.f4404e);
                this.f4401b = null;
            }
        }
        T t3 = (T) this.f4403d.remove(this);
        if (t3 != null) {
            t3.tryComplete();
        }
    }
}
