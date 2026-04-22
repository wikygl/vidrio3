package j$.util.concurrent;

import j$.util.D;
import j$.util.J;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class z implements J {

    /* renamed from: a  reason: collision with root package name */
    long f4194a;

    /* renamed from: b  reason: collision with root package name */
    final long f4195b;

    /* renamed from: c  reason: collision with root package name */
    final int f4196c;

    /* renamed from: d  reason: collision with root package name */
    final int f4197d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public z(long j4, long j5, int i4, int i5) {
        this.f4194a = j4;
        this.f4195b = j5;
        this.f4196c = i4;
        this.f4197d = i5;
    }

    @Override // j$.util.Spliterator
    /* renamed from: a */
    public final z trySplit() {
        long j4 = this.f4194a;
        long j5 = (this.f4195b + j4) >>> 1;
        if (j5 <= j4) {
            return null;
        }
        this.f4194a = j5;
        return new z(j4, j5, this.f4196c, this.f4197d);
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return 17728;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        return this.f4195b - this.f4194a;
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        D.b(this, consumer);
    }

    @Override // j$.util.P
    public final void forEachRemaining(IntConsumer intConsumer) {
        intConsumer.getClass();
        long j4 = this.f4194a;
        long j5 = this.f4195b;
        if (j4 < j5) {
            this.f4194a = j5;
            ThreadLocalRandom current = ThreadLocalRandom.current();
            do {
                intConsumer.accept(current.d(this.f4196c, this.f4197d));
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
        return D.g(this, consumer);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(IntConsumer intConsumer) {
        intConsumer.getClass();
        long j4 = this.f4194a;
        if (j4 < this.f4195b) {
            intConsumer.accept(ThreadLocalRandom.current().d(this.f4196c, this.f4197d));
            this.f4194a = j4 + 1;
            return true;
        }
        return false;
    }
}
