package j$.util.concurrent;

import j$.util.D;
import j$.util.M;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class A implements M {

    /* renamed from: a  reason: collision with root package name */
    long f4128a;

    /* renamed from: b  reason: collision with root package name */
    final long f4129b;

    /* renamed from: c  reason: collision with root package name */
    final long f4130c;

    /* renamed from: d  reason: collision with root package name */
    final long f4131d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public A(long j4, long j5, long j6, long j7) {
        this.f4128a = j4;
        this.f4129b = j5;
        this.f4130c = j6;
        this.f4131d = j7;
    }

    @Override // j$.util.Spliterator
    /* renamed from: a */
    public final A trySplit() {
        long j4 = this.f4128a;
        long j5 = (this.f4129b + j4) >>> 1;
        if (j5 <= j4) {
            return null;
        }
        this.f4128a = j5;
        return new A(j4, j5, this.f4130c, this.f4131d);
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return 17728;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        return this.f4129b - this.f4128a;
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        D.c(this, consumer);
    }

    @Override // j$.util.P
    public final void forEachRemaining(LongConsumer longConsumer) {
        longConsumer.getClass();
        long j4 = this.f4128a;
        long j5 = this.f4129b;
        if (j4 < j5) {
            this.f4128a = j5;
            ThreadLocalRandom current = ThreadLocalRandom.current();
            do {
                longConsumer.accept(current.e(this.f4130c, this.f4131d));
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
        return D.h(this, consumer);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(LongConsumer longConsumer) {
        longConsumer.getClass();
        long j4 = this.f4128a;
        if (j4 < this.f4129b) {
            longConsumer.accept(ThreadLocalRandom.current().e(this.f4130c, this.f4131d));
            this.f4128a = j4 + 1;
            return true;
        }
        return false;
    }
}
