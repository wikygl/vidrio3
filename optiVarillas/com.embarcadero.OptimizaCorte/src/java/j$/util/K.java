package j$.util;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class K implements M {

    /* renamed from: a */
    public final /* synthetic */ Spliterator.OfLong f4086a;

    private /* synthetic */ K(Spliterator.OfLong ofLong) {
        this.f4086a = ofLong;
    }

    public static /* synthetic */ M a(Spliterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof L ? ((L) ofLong).f4087a : new K(ofLong);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ int characteristics() {
        return this.f4086a.characteristics();
    }

    public final /* synthetic */ boolean equals(Object obj) {
        Spliterator.OfLong ofLong = this.f4086a;
        if (obj instanceof K) {
            obj = ((K) obj).f4086a;
        }
        return ofLong.equals(obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long estimateSize() {
        return this.f4086a.estimateSize();
    }

    @Override // j$.util.P
    public final /* synthetic */ void forEachRemaining(Object obj) {
        this.f4086a.forEachRemaining((Spliterator.OfLong) obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4086a.forEachRemaining((Consumer<? super Long>) consumer);
    }

    @Override // j$.util.M
    public final /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.f4086a.forEachRemaining(longConsumer);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ Comparator getComparator() {
        return this.f4086a.getComparator();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return this.f4086a.getExactSizeIfKnown();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return this.f4086a.hasCharacteristics(i4);
    }

    public final /* synthetic */ int hashCode() {
        return this.f4086a.hashCode();
    }

    @Override // j$.util.P
    public final /* synthetic */ boolean tryAdvance(Object obj) {
        return this.f4086a.tryAdvance((Spliterator.OfLong) obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.f4086a.tryAdvance((Consumer<? super Long>) consumer);
    }

    @Override // j$.util.M
    public final /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
        return this.f4086a.tryAdvance(longConsumer);
    }

    @Override // j$.util.M, j$.util.P, j$.util.Spliterator
    public final /* synthetic */ M trySplit() {
        return a(this.f4086a.trySplit());
    }

    @Override // j$.util.P, j$.util.Spliterator
    public final /* synthetic */ P trySplit() {
        return N.a(this.f4086a.trySplit());
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ Spliterator trySplit() {
        return Q.a(this.f4086a.trySplit());
    }
}
