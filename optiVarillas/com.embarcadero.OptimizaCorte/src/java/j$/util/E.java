package j$.util;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class E implements G {

    /* renamed from: a */
    public final /* synthetic */ Spliterator.OfDouble f4082a;

    private /* synthetic */ E(Spliterator.OfDouble ofDouble) {
        this.f4082a = ofDouble;
    }

    public static /* synthetic */ G a(Spliterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof F ? ((F) ofDouble).f4083a : new E(ofDouble);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ int characteristics() {
        return this.f4082a.characteristics();
    }

    public final /* synthetic */ boolean equals(Object obj) {
        Spliterator.OfDouble ofDouble = this.f4082a;
        if (obj instanceof E) {
            obj = ((E) obj).f4082a;
        }
        return ofDouble.equals(obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long estimateSize() {
        return this.f4082a.estimateSize();
    }

    @Override // j$.util.P
    public final /* synthetic */ void forEachRemaining(Object obj) {
        this.f4082a.forEachRemaining((Spliterator.OfDouble) obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4082a.forEachRemaining((Consumer<? super Double>) consumer);
    }

    @Override // j$.util.G
    public final /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.f4082a.forEachRemaining(doubleConsumer);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ Comparator getComparator() {
        return this.f4082a.getComparator();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return this.f4082a.getExactSizeIfKnown();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return this.f4082a.hasCharacteristics(i4);
    }

    public final /* synthetic */ int hashCode() {
        return this.f4082a.hashCode();
    }

    @Override // j$.util.P
    public final /* synthetic */ boolean tryAdvance(Object obj) {
        return this.f4082a.tryAdvance((Spliterator.OfDouble) obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.f4082a.tryAdvance((Consumer<? super Double>) consumer);
    }

    @Override // j$.util.G
    public final /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
        return this.f4082a.tryAdvance(doubleConsumer);
    }

    @Override // j$.util.G, j$.util.P, j$.util.Spliterator
    public final /* synthetic */ G trySplit() {
        return a(this.f4082a.trySplit());
    }

    @Override // j$.util.P, j$.util.Spliterator
    public final /* synthetic */ P trySplit() {
        return N.a(this.f4082a.trySplit());
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ Spliterator trySplit() {
        return Q.a(this.f4082a.trySplit());
    }
}
