package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.function.Consumer;

/* renamed from: j$.util.stream.x1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class AbstractC0630x1 extends CountedCompleter implements InterfaceC0599q2 {

    /* renamed from: a  reason: collision with root package name */
    protected final Spliterator f4614a;

    /* renamed from: b  reason: collision with root package name */
    protected final AbstractC0521b f4615b;

    /* renamed from: c  reason: collision with root package name */
    protected final long f4616c;

    /* renamed from: d  reason: collision with root package name */
    protected long f4617d;

    /* renamed from: e  reason: collision with root package name */
    protected long f4618e;
    protected int f;

    /* renamed from: g  reason: collision with root package name */
    protected int f4619g;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0630x1(Spliterator spliterator, AbstractC0521b abstractC0521b, int i4) {
        this.f4614a = spliterator;
        this.f4615b = abstractC0521b;
        this.f4616c = AbstractC0536e.g(spliterator.estimateSize());
        this.f4617d = 0L;
        this.f4618e = i4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0630x1(AbstractC0630x1 abstractC0630x1, Spliterator spliterator, long j4, long j5, int i4) {
        super(abstractC0630x1);
        this.f4614a = spliterator;
        this.f4615b = abstractC0630x1.f4615b;
        this.f4616c = abstractC0630x1.f4616c;
        this.f4617d = j4;
        this.f4618e = j5;
        if (j4 < 0 || j5 < 0 || (j4 + j5) - 1 >= i4) {
            throw new IllegalArgumentException(String.format("offset and length interval [%d, %d + %d) is not within array size interval [0, %d)", Long.valueOf(j4), Long.valueOf(j4), Long.valueOf(j5), Integer.valueOf(i4)));
        }
    }

    public /* synthetic */ void accept(double d4) {
        AbstractC0637z0.a();
        throw null;
    }

    public /* synthetic */ void accept(int i4) {
        AbstractC0637z0.k();
        throw null;
    }

    public /* synthetic */ void accept(long j4) {
        AbstractC0637z0.l();
        throw null;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    abstract AbstractC0630x1 b(Spliterator spliterator, long j4, long j5);

    @Override // java.util.concurrent.CountedCompleter
    public final void compute() {
        Spliterator trySplit;
        Spliterator spliterator = this.f4614a;
        AbstractC0630x1 abstractC0630x1 = this;
        while (spliterator.estimateSize() > abstractC0630x1.f4616c && (trySplit = spliterator.trySplit()) != null) {
            abstractC0630x1.setPendingCount(1);
            long estimateSize = trySplit.estimateSize();
            abstractC0630x1.b(trySplit, abstractC0630x1.f4617d, estimateSize).fork();
            abstractC0630x1 = abstractC0630x1.b(spliterator, abstractC0630x1.f4617d + estimateSize, abstractC0630x1.f4618e - estimateSize);
        }
        abstractC0630x1.f4615b.R(spliterator, abstractC0630x1);
        abstractC0630x1.propagateCompletion();
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        long j5 = this.f4618e;
        if (j4 > j5) {
            throw new IllegalStateException("size passed to Sink.begin exceeds array length");
        }
        int i4 = (int) this.f4617d;
        this.f = i4;
        this.f4619g = i4 + ((int) j5);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }
}
