package j$.util;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class N implements P {

    /* renamed from: a */
    public final /* synthetic */ Spliterator.OfPrimitive f4088a;

    private /* synthetic */ N(Spliterator.OfPrimitive ofPrimitive) {
        this.f4088a = ofPrimitive;
    }

    public static /* synthetic */ P a(Spliterator.OfPrimitive ofPrimitive) {
        if (ofPrimitive == null) {
            return null;
        }
        return ofPrimitive instanceof O ? ((O) ofPrimitive).f4089a : ofPrimitive instanceof Spliterator.OfDouble ? E.a((Spliterator.OfDouble) ofPrimitive) : ofPrimitive instanceof Spliterator.OfInt ? H.a((Spliterator.OfInt) ofPrimitive) : ofPrimitive instanceof Spliterator.OfLong ? K.a((Spliterator.OfLong) ofPrimitive) : new N(ofPrimitive);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ int characteristics() {
        return this.f4088a.characteristics();
    }

    public final /* synthetic */ boolean equals(Object obj) {
        Spliterator.OfPrimitive ofPrimitive = this.f4088a;
        if (obj instanceof N) {
            obj = ((N) obj).f4088a;
        }
        return ofPrimitive.equals(obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long estimateSize() {
        return this.f4088a.estimateSize();
    }

    @Override // j$.util.P
    public final /* synthetic */ void forEachRemaining(Object obj) {
        this.f4088a.forEachRemaining((Spliterator.OfPrimitive) obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4088a.forEachRemaining(consumer);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ Comparator getComparator() {
        return this.f4088a.getComparator();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return this.f4088a.getExactSizeIfKnown();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return this.f4088a.hasCharacteristics(i4);
    }

    public final /* synthetic */ int hashCode() {
        return this.f4088a.hashCode();
    }

    @Override // j$.util.P
    public final /* synthetic */ boolean tryAdvance(Object obj) {
        return this.f4088a.tryAdvance((Spliterator.OfPrimitive) obj);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.f4088a.tryAdvance(consumer);
    }

    @Override // j$.util.P, j$.util.Spliterator
    public final /* synthetic */ P trySplit() {
        return a(this.f4088a.trySplit());
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ Spliterator trySplit() {
        return Q.a(this.f4088a.trySplit());
    }
}
