package j$.util;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class Y extends D implements G {
    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        D.a(this, consumer);
    }

    @Override // j$.util.G
    public final void forEachRemaining(DoubleConsumer doubleConsumer) {
        Objects.requireNonNull(doubleConsumer);
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

    @Override // j$.util.G
    public final boolean tryAdvance(DoubleConsumer doubleConsumer) {
        Objects.requireNonNull(doubleConsumer);
        return false;
    }

    @Override // j$.util.D, j$.util.G, j$.util.P, j$.util.Spliterator
    public final /* bridge */ /* synthetic */ G trySplit() {
        return null;
    }

    @Override // j$.util.D, j$.util.G, j$.util.P, j$.util.Spliterator
    public final /* bridge */ /* synthetic */ P trySplit() {
        return null;
    }
}
