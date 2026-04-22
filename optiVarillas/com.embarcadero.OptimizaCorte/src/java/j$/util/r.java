package j$.util;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class r implements PrimitiveIterator.OfDouble {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ InterfaceC0515s f4243a;

    private /* synthetic */ r(InterfaceC0515s interfaceC0515s) {
        this.f4243a = interfaceC0515s;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble a(InterfaceC0515s interfaceC0515s) {
        if (interfaceC0515s == null) {
            return null;
        }
        return interfaceC0515s instanceof C0514q ? ((C0514q) interfaceC0515s).f4242a : new r(interfaceC0515s);
    }

    public final /* synthetic */ boolean equals(Object obj) {
        InterfaceC0515s interfaceC0515s = this.f4243a;
        if (obj instanceof r) {
            obj = ((r) obj).f4243a;
        }
        return interfaceC0515s.equals(obj);
    }

    @Override // java.util.PrimitiveIterator
    public final /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.f4243a.forEachRemaining((Object) doubleConsumer);
    }

    @Override // java.util.PrimitiveIterator.OfDouble, java.util.Iterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4243a.forEachRemaining(consumer);
    }

    @Override // java.util.PrimitiveIterator.OfDouble
    /* renamed from: forEachRemaining  reason: avoid collision after fix types in other method */
    public final /* synthetic */ void forEachRemaining2(DoubleConsumer doubleConsumer) {
        this.f4243a.forEachRemaining(doubleConsumer);
    }

    @Override // java.util.Iterator
    public final /* synthetic */ boolean hasNext() {
        return this.f4243a.hasNext();
    }

    public final /* synthetic */ int hashCode() {
        return this.f4243a.hashCode();
    }

    @Override // java.util.PrimitiveIterator.OfDouble, java.util.Iterator
    public final /* synthetic */ Double next() {
        return this.f4243a.next();
    }

    @Override // java.util.PrimitiveIterator.OfDouble, java.util.Iterator
    public final /* synthetic */ Object next() {
        return this.f4243a.next();
    }

    @Override // java.util.PrimitiveIterator.OfDouble
    public final /* synthetic */ double nextDouble() {
        return this.f4243a.nextDouble();
    }

    @Override // java.util.Iterator
    public final /* synthetic */ void remove() {
        this.f4243a.remove();
    }
}
