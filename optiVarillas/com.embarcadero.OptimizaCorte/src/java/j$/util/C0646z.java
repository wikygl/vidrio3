package j$.util;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.util.z  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class C0646z implements PrimitiveIterator.OfLong {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ A f4648a;

    private /* synthetic */ C0646z(A a4) {
        this.f4648a = a4;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong a(A a4) {
        if (a4 == null) {
            return null;
        }
        return a4 instanceof C0645y ? ((C0645y) a4).f4647a : new C0646z(a4);
    }

    public final /* synthetic */ boolean equals(Object obj) {
        A a4 = this.f4648a;
        if (obj instanceof C0646z) {
            obj = ((C0646z) obj).f4648a;
        }
        return a4.equals(obj);
    }

    @Override // java.util.PrimitiveIterator
    public final /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.f4648a.forEachRemaining((Object) longConsumer);
    }

    @Override // java.util.PrimitiveIterator.OfLong, java.util.Iterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4648a.forEachRemaining(consumer);
    }

    @Override // java.util.PrimitiveIterator.OfLong
    /* renamed from: forEachRemaining  reason: avoid collision after fix types in other method */
    public final /* synthetic */ void forEachRemaining2(LongConsumer longConsumer) {
        this.f4648a.forEachRemaining(longConsumer);
    }

    @Override // java.util.Iterator
    public final /* synthetic */ boolean hasNext() {
        return this.f4648a.hasNext();
    }

    public final /* synthetic */ int hashCode() {
        return this.f4648a.hashCode();
    }

    @Override // java.util.PrimitiveIterator.OfLong, java.util.Iterator
    public final /* synthetic */ Long next() {
        return this.f4648a.next();
    }

    @Override // java.util.PrimitiveIterator.OfLong, java.util.Iterator
    public final /* synthetic */ Object next() {
        return this.f4648a.next();
    }

    @Override // java.util.PrimitiveIterator.OfLong
    public final /* synthetic */ long nextLong() {
        return this.f4648a.nextLong();
    }

    @Override // java.util.Iterator
    public final /* synthetic */ void remove() {
        this.f4648a.remove();
    }
}
