package j$.util;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.util.v  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class C0642v implements PrimitiveIterator.OfInt {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ InterfaceC0643w f4645a;

    private /* synthetic */ C0642v(InterfaceC0643w interfaceC0643w) {
        this.f4645a = interfaceC0643w;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt a(InterfaceC0643w interfaceC0643w) {
        if (interfaceC0643w == null) {
            return null;
        }
        return interfaceC0643w instanceof C0641u ? ((C0641u) interfaceC0643w).f4644a : new C0642v(interfaceC0643w);
    }

    public final /* synthetic */ boolean equals(Object obj) {
        InterfaceC0643w interfaceC0643w = this.f4645a;
        if (obj instanceof C0642v) {
            obj = ((C0642v) obj).f4645a;
        }
        return interfaceC0643w.equals(obj);
    }

    @Override // java.util.PrimitiveIterator
    public final /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.f4645a.forEachRemaining((Object) intConsumer);
    }

    @Override // java.util.PrimitiveIterator.OfInt, java.util.Iterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4645a.forEachRemaining(consumer);
    }

    @Override // java.util.PrimitiveIterator.OfInt
    /* renamed from: forEachRemaining  reason: avoid collision after fix types in other method */
    public final /* synthetic */ void forEachRemaining2(IntConsumer intConsumer) {
        this.f4645a.forEachRemaining(intConsumer);
    }

    @Override // java.util.Iterator
    public final /* synthetic */ boolean hasNext() {
        return this.f4645a.hasNext();
    }

    public final /* synthetic */ int hashCode() {
        return this.f4645a.hashCode();
    }

    @Override // java.util.PrimitiveIterator.OfInt, java.util.Iterator
    public final /* synthetic */ Integer next() {
        return this.f4645a.next();
    }

    @Override // java.util.PrimitiveIterator.OfInt, java.util.Iterator
    public final /* synthetic */ Object next() {
        return this.f4645a.next();
    }

    @Override // java.util.PrimitiveIterator.OfInt
    public final /* synthetic */ int nextInt() {
        return this.f4645a.nextInt();
    }

    @Override // java.util.Iterator
    public final /* synthetic */ void remove() {
        this.f4645a.remove();
    }
}
