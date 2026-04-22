package j$.util;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class O implements Spliterator.OfPrimitive {

    /* renamed from: a */
    public final /* synthetic */ P f4089a;

    private /* synthetic */ O(P p4) {
        this.f4089a = p4;
    }

    public static /* synthetic */ Spliterator.OfPrimitive a(P p4) {
        if (p4 == null) {
            return null;
        }
        return p4 instanceof N ? ((N) p4).f4088a : p4 instanceof G ? F.a((G) p4) : p4 instanceof J ? I.a((J) p4) : p4 instanceof M ? L.a((M) p4) : new O(p4);
    }

    @Override // java.util.Spliterator
    public final /* synthetic */ int characteristics() {
        return this.f4089a.characteristics();
    }

    public final /* synthetic */ boolean equals(Object obj) {
        P p4 = this.f4089a;
        if (obj instanceof O) {
            obj = ((O) obj).f4089a;
        }
        return p4.equals(obj);
    }

    @Override // java.util.Spliterator
    public final /* synthetic */ long estimateSize() {
        return this.f4089a.estimateSize();
    }

    @Override // java.util.Spliterator.OfPrimitive
    public final /* synthetic */ void forEachRemaining(Object obj) {
        this.f4089a.forEachRemaining(obj);
    }

    @Override // java.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4089a.forEachRemaining(consumer);
    }

    @Override // java.util.Spliterator
    public final /* synthetic */ Comparator getComparator() {
        return this.f4089a.getComparator();
    }

    @Override // java.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return this.f4089a.getExactSizeIfKnown();
    }

    @Override // java.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return this.f4089a.hasCharacteristics(i4);
    }

    public final /* synthetic */ int hashCode() {
        return this.f4089a.hashCode();
    }

    @Override // java.util.Spliterator.OfPrimitive
    public final /* synthetic */ boolean tryAdvance(Object obj) {
        return this.f4089a.tryAdvance(obj);
    }

    @Override // java.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.f4089a.tryAdvance(consumer);
    }

    @Override // java.util.Spliterator.OfPrimitive, java.util.Spliterator
    public final /* synthetic */ Spliterator.OfPrimitive trySplit() {
        return a(this.f4089a.trySplit());
    }

    @Override // java.util.Spliterator.OfPrimitive, java.util.Spliterator
    public final /* synthetic */ java.util.Spliterator trySplit() {
        return Spliterator.Wrapper.convert(this.f4089a.trySplit());
    }
}
