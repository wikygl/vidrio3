package j$.util;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class H implements J {

    /* renamed from: a */
    public final /* synthetic */ Spliterator.OfInt f4084a;

    private /* synthetic */ H(Spliterator.OfInt ofInt) {
        this.f4084a = ofInt;
    }

    public static /* synthetic */ J a(Spliterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof I ? ((I) ofInt).f4085a : new H(ofInt);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ int characteristics() {
        return this.f4084a.characteristics();
    }

    public final /* synthetic */ boolean equals(Object obj) {
        Spliterator.OfInt ofInt = this.f4084a;
        if (obj instanceof H) {
            obj = ((H) obj).f4084a;
        }
        return ofInt.equals(obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long estimateSize() {
        return this.f4084a.estimateSize();
    }

    @Override // j$.util.P
    public final /* synthetic */ void forEachRemaining(Object obj) {
        this.f4084a.forEachRemaining((Spliterator.OfInt) obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4084a.forEachRemaining((Consumer<? super Integer>) consumer);
    }

    @Override // j$.util.J
    public final /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.f4084a.forEachRemaining(intConsumer);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ Comparator getComparator() {
        return this.f4084a.getComparator();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return this.f4084a.getExactSizeIfKnown();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return this.f4084a.hasCharacteristics(i4);
    }

    public final /* synthetic */ int hashCode() {
        return this.f4084a.hashCode();
    }

    @Override // j$.util.P
    public final /* synthetic */ boolean tryAdvance(Object obj) {
        return this.f4084a.tryAdvance((Spliterator.OfInt) obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.f4084a.tryAdvance((Consumer<? super Integer>) consumer);
    }

    @Override // j$.util.J
    public final /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return this.f4084a.tryAdvance(intConsumer);
    }

    @Override // j$.util.J, j$.util.P, j$.util.Spliterator
    public final /* synthetic */ J trySplit() {
        return a(this.f4084a.trySplit());
    }

    @Override // j$.util.P, j$.util.Spliterator
    public final /* synthetic */ P trySplit() {
        return N.a(this.f4084a.trySplit());
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ Spliterator trySplit() {
        return Q.a(this.f4084a.trySplit());
    }
}
