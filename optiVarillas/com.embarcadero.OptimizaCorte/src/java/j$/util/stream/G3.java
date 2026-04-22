package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
abstract class G3 {

    /* renamed from: a  reason: collision with root package name */
    protected final Spliterator f4289a;

    /* renamed from: b  reason: collision with root package name */
    protected final boolean f4290b;

    /* renamed from: c  reason: collision with root package name */
    protected final int f4291c;

    /* renamed from: d  reason: collision with root package name */
    private final long f4292d;

    /* renamed from: e  reason: collision with root package name */
    private final AtomicLong f4293e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public G3(Spliterator spliterator, long j4, long j5) {
        this.f4289a = spliterator;
        int i4 = (j5 > 0L ? 1 : (j5 == 0L ? 0 : -1));
        this.f4290b = i4 < 0;
        this.f4292d = i4 >= 0 ? j5 : 0L;
        this.f4291c = 128;
        this.f4293e = new AtomicLong(i4 >= 0 ? j4 + j5 : j4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public G3(Spliterator spliterator, G3 g32) {
        this.f4289a = spliterator;
        this.f4290b = g32.f4290b;
        this.f4293e = g32.f4293e;
        this.f4292d = g32.f4292d;
        this.f4291c = g32.f4291c;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final long b(long j4) {
        AtomicLong atomicLong;
        long j5;
        boolean z4;
        long min;
        do {
            atomicLong = this.f4293e;
            j5 = atomicLong.get();
            z4 = this.f4290b;
            if (j5 != 0) {
                min = Math.min(j5, j4);
                if (min <= 0) {
                    break;
                }
            } else if (z4) {
                return j4;
            } else {
                return 0L;
            }
        } while (!atomicLong.compareAndSet(j5, j5 - min));
        if (z4) {
            return Math.max(j4 - min, 0L);
        }
        long j6 = this.f4292d;
        return j5 > j6 ? Math.max(min - (j5 - j6), 0L) : min;
    }

    protected abstract Spliterator c(Spliterator spliterator);

    public final int characteristics() {
        return this.f4289a.characteristics() & (-16465);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final F3 d() {
        return this.f4293e.get() > 0 ? F3.MAYBE_MORE : this.f4290b ? F3.UNLIMITED : F3.NO_MORE;
    }

    public final long estimateSize() {
        return this.f4289a.estimateSize();
    }

    public /* bridge */ /* synthetic */ j$.util.G trySplit() {
        return (j$.util.G) m6trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ j$.util.J m3trySplit() {
        return (j$.util.J) m6trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ j$.util.M m4trySplit() {
        return (j$.util.M) m6trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ j$.util.P m5trySplit() {
        return (j$.util.P) m6trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public final Spliterator m6trySplit() {
        Spliterator trySplit;
        if (this.f4293e.get() == 0 || (trySplit = this.f4289a.trySplit()) == null) {
            return null;
        }
        return c(trySplit);
    }
}
