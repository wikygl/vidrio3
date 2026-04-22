package j$.util;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.util.y  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0645y implements A, InterfaceC0507j {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ PrimitiveIterator.OfLong f4647a;

    private /* synthetic */ C0645y(PrimitiveIterator.OfLong ofLong) {
        this.f4647a = ofLong;
    }

    public static /* synthetic */ A a(PrimitiveIterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof C0646z ? ((C0646z) ofLong).f4648a : new C0645y(ofLong);
    }

    public final /* synthetic */ boolean equals(Object obj) {
        PrimitiveIterator.OfLong ofLong = this.f4647a;
        if (obj instanceof C0645y) {
            obj = ((C0645y) obj).f4647a;
        }
        return ofLong.equals(obj);
    }

    @Override // j$.util.B
    public final /* synthetic */ void forEachRemaining(Object obj) {
        this.f4647a.forEachRemaining((PrimitiveIterator.OfLong) obj);
    }

    @Override // j$.util.A, java.util.Iterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.f4647a.forEachRemaining((Consumer<? super Long>) consumer);
    }

    @Override // j$.util.A
    public final /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.f4647a.forEachRemaining(longConsumer);
    }

    @Override // java.util.Iterator
    public final /* synthetic */ boolean hasNext() {
        return this.f4647a.hasNext();
    }

    public final /* synthetic */ int hashCode() {
        return this.f4647a.hashCode();
    }

    @Override // j$.util.A, java.util.Iterator
    public final /* synthetic */ Long next() {
        return this.f4647a.next();
    }

    @Override // java.util.Iterator
    public final /* synthetic */ Object next() {
        return this.f4647a.next();
    }

    @Override // j$.util.A
    public final /* synthetic */ long nextLong() {
        return this.f4647a.nextLong();
    }

    @Override // java.util.Iterator
    public final /* synthetic */ void remove() {
        this.f4647a.remove();
    }
}
