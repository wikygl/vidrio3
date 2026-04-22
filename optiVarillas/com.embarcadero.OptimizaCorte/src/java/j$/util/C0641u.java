package j$.util;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.util.u  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0641u implements InterfaceC0643w, InterfaceC0507j {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ PrimitiveIterator.OfInt f4644a;

    private /* synthetic */ C0641u(PrimitiveIterator.OfInt ofInt) {
        this.f4644a = ofInt;
    }

    public static /* synthetic */ InterfaceC0643w a(PrimitiveIterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof C0642v ? ((C0642v) ofInt).f4645a : new C0641u(ofInt);
    }

    public final /* synthetic */ boolean equals(Object obj) {
        PrimitiveIterator.OfInt ofInt = this.f4644a;
        if (obj instanceof C0641u) {
            obj = ((C0641u) obj).f4644a;
        }
        return ofInt.equals(obj);
    }

    @Override // j$.util.B
    public final /* synthetic */ void forEachRemaining(Object obj) {
        this.f4644a.forEachRemaining((PrimitiveIterator.OfInt) obj);
    }

    @Override // j$.util.InterfaceC0643w, java.util.Iterator, j$.util.InterfaceC0507j
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4644a.forEachRemaining((Consumer<? super Integer>) consumer);
    }

    @Override // j$.util.InterfaceC0643w
    public final /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.f4644a.forEachRemaining(intConsumer);
    }

    @Override // java.util.Iterator
    public final /* synthetic */ boolean hasNext() {
        return this.f4644a.hasNext();
    }

    public final /* synthetic */ int hashCode() {
        return this.f4644a.hashCode();
    }

    @Override // j$.util.InterfaceC0643w, java.util.Iterator
    public final /* synthetic */ Integer next() {
        return this.f4644a.next();
    }

    @Override // java.util.Iterator
    public final /* synthetic */ Object next() {
        return this.f4644a.next();
    }

    @Override // j$.util.InterfaceC0643w
    public final /* synthetic */ int nextInt() {
        return this.f4644a.nextInt();
    }

    @Override // java.util.Iterator
    public final /* synthetic */ void remove() {
        this.f4644a.remove();
    }
}
