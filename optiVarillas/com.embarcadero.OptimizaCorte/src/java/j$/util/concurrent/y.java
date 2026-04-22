package j$.util.concurrent;

import j$.util.D;
import j$.util.G;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class y implements G {

    /* renamed from: a  reason: collision with root package name */
    long f4190a;

    /* renamed from: b  reason: collision with root package name */
    final long f4191b;

    /* renamed from: c  reason: collision with root package name */
    final double f4192c;

    /* renamed from: d  reason: collision with root package name */
    final double f4193d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public y(long j4, long j5, double d4, double d5) {
        this.f4190a = j4;
        this.f4191b = j5;
        this.f4192c = d4;
        this.f4193d = d5;
    }

    @Override // j$.util.Spliterator
    /* renamed from: a */
    public final y trySplit() {
        long j4 = this.f4190a;
        long j5 = (this.f4191b + j4) >>> 1;
        if (j5 <= j4) {
            return null;
        }
        this.f4190a = j5;
        return new y(j4, j5, this.f4192c, this.f4193d);
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return 17728;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        return this.f4191b - this.f4190a;
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        D.a(this, consumer);
    }

    @Override // j$.util.P
    public final void forEachRemaining(DoubleConsumer doubleConsumer) {
        doubleConsumer.getClass();
        long j4 = this.f4190a;
        long j5 = this.f4191b;
        if (j4 < j5) {
            this.f4190a = j5;
            ThreadLocalRandom current = ThreadLocalRandom.current();
            do {
                doubleConsumer.accept(current.c(this.f4192c, this.f4193d));
                j4++;
            } while (j4 < j5);
        }
    }

    @Override // j$.util.Spliterator
    public final Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return D.d(this);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return D.e(this, i4);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return D.f(this, consumer);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(DoubleConsumer doubleConsumer) {
        doubleConsumer.getClass();
        long j4 = this.f4190a;
        if (j4 < this.f4191b) {
            doubleConsumer.accept(ThreadLocalRandom.current().c(this.f4192c, this.f4193d));
            this.f4190a = j4 + 1;
            return true;
        }
        return false;
    }
}
