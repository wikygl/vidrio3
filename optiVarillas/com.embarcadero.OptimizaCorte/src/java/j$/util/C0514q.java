package j$.util;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.util.q  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0514q implements InterfaceC0515s, InterfaceC0507j {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ PrimitiveIterator.OfDouble f4242a;

    private /* synthetic */ C0514q(PrimitiveIterator.OfDouble ofDouble) {
        this.f4242a = ofDouble;
    }

    public static /* synthetic */ InterfaceC0515s a(PrimitiveIterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof r ? ((r) ofDouble).f4243a : new C0514q(ofDouble);
    }

    public final /* synthetic */ boolean equals(Object obj) {
        PrimitiveIterator.OfDouble ofDouble = this.f4242a;
        if (obj instanceof C0514q) {
            obj = ((C0514q) obj).f4242a;
        }
        return ofDouble.equals(obj);
    }

    @Override // j$.util.B
    public final /* synthetic */ void forEachRemaining(Object obj) {
        this.f4242a.forEachRemaining((PrimitiveIterator.OfDouble) obj);
    }

    @Override // j$.util.InterfaceC0515s, java.util.Iterator, j$.util.InterfaceC0507j
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4242a.forEachRemaining((Consumer<? super Double>) consumer);
    }

    @Override // j$.util.InterfaceC0515s
    public final /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.f4242a.forEachRemaining(doubleConsumer);
    }

    @Override // java.util.Iterator
    public final /* synthetic */ boolean hasNext() {
        return this.f4242a.hasNext();
    }

    public final /* synthetic */ int hashCode() {
        return this.f4242a.hashCode();
    }

    @Override // j$.util.InterfaceC0515s, java.util.Iterator
    public final /* synthetic */ Double next() {
        return this.f4242a.next();
    }

    @Override // java.util.Iterator
    public final /* synthetic */ Object next() {
        return this.f4242a.next();
    }

    @Override // j$.util.InterfaceC0515s
    public final /* synthetic */ double nextDouble() {
        return this.f4242a.nextDouble();
    }

    @Override // java.util.Iterator
    public final /* synthetic */ void remove() {
        this.f4242a.remove();
    }
}
