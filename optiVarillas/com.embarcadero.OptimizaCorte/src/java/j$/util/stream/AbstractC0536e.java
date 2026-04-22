package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

/* renamed from: j$.util.stream.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
abstract class AbstractC0536e extends CountedCompleter {

    /* renamed from: g  reason: collision with root package name */
    private static final int f4475g = ForkJoinPool.getCommonPoolParallelism() << 2;

    /* renamed from: a  reason: collision with root package name */
    protected final AbstractC0521b f4476a;

    /* renamed from: b  reason: collision with root package name */
    protected Spliterator f4477b;

    /* renamed from: c  reason: collision with root package name */
    protected long f4478c;

    /* renamed from: d  reason: collision with root package name */
    protected AbstractC0536e f4479d;

    /* renamed from: e  reason: collision with root package name */
    protected AbstractC0536e f4480e;
    private Object f;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractC0536e(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        super(null);
        this.f4476a = abstractC0521b;
        this.f4477b = spliterator;
        this.f4478c = 0L;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractC0536e(AbstractC0536e abstractC0536e, Spliterator spliterator) {
        super(abstractC0536e);
        this.f4477b = spliterator;
        this.f4476a = abstractC0536e.f4476a;
        this.f4478c = abstractC0536e.f4478c;
    }

    public static int b() {
        return f4475g;
    }

    public static long g(long j4) {
        long j5 = j4 / f4475g;
        if (j5 > 0) {
            return j5;
        }
        return 1L;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract Object a();

    /* JADX INFO: Access modifiers changed from: protected */
    public Object c() {
        return this.f;
    }

    @Override // java.util.concurrent.CountedCompleter
    public void compute() {
        Spliterator trySplit;
        Spliterator spliterator = this.f4477b;
        long estimateSize = spliterator.estimateSize();
        long j4 = this.f4478c;
        if (j4 == 0) {
            j4 = g(estimateSize);
            this.f4478c = j4;
        }
        boolean z4 = false;
        AbstractC0536e abstractC0536e = this;
        while (estimateSize > j4 && (trySplit = spliterator.trySplit()) != null) {
            AbstractC0536e e4 = abstractC0536e.e(trySplit);
            abstractC0536e.f4479d = e4;
            AbstractC0536e e5 = abstractC0536e.e(spliterator);
            abstractC0536e.f4480e = e5;
            abstractC0536e.setPendingCount(1);
            if (z4) {
                spliterator = trySplit;
                abstractC0536e = e4;
                e4 = e5;
            } else {
                abstractC0536e = e5;
            }
            z4 = !z4;
            e4.fork();
            estimateSize = spliterator.estimateSize();
        }
        abstractC0536e.f(abstractC0536e.a());
        abstractC0536e.tryComplete();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean d() {
        return ((AbstractC0536e) getCompleter()) == null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract AbstractC0536e e(Spliterator spliterator);

    /* JADX INFO: Access modifiers changed from: protected */
    public void f(Object obj) {
        this.f = obj;
    }

    @Override // java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    public Object getRawResult() {
        return this.f;
    }

    @Override // java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        this.f4477b = null;
        this.f4480e = null;
        this.f4479d = null;
    }

    @Override // java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    protected final void setRawResult(Object obj) {
        if (obj != null) {
            throw new IllegalStateException();
        }
    }
}
