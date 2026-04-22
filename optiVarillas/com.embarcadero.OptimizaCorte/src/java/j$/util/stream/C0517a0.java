package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.IntConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.a0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0517a0 extends AbstractC0527c0 {
    @Override // j$.util.stream.AbstractC0521b
    final boolean M() {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractC0527c0, j$.util.stream.IntStream
    public final void forEach(IntConsumer intConsumer) {
        if (isParallel()) {
            super.forEach(intConsumer);
        } else {
            AbstractC0527c0.U(P()).forEachRemaining(intConsumer);
        }
    }

    @Override // j$.util.stream.AbstractC0527c0, j$.util.stream.IntStream
    public final void forEachOrdered(IntConsumer intConsumer) {
        if (isParallel()) {
            super.forEachOrdered(intConsumer);
        } else {
            AbstractC0527c0.U(P()).forEachRemaining(intConsumer);
        }
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* bridge */ /* synthetic */ IntStream parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* bridge */ /* synthetic */ IntStream sequential() {
        sequential();
        return this;
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h
    public final /* bridge */ /* synthetic */ Spliterator spliterator() {
        return spliterator();
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final InterfaceC0551h unordered() {
        return !H() ? this : new C0624w(this, EnumC0540e3.f4493r, 2);
    }
}
