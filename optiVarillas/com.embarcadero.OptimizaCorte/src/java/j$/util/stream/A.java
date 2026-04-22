package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.DoubleConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class A extends C {
    @Override // j$.util.stream.AbstractC0521b
    final boolean M() {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.C, j$.util.stream.F
    public final void forEach(DoubleConsumer doubleConsumer) {
        if (isParallel()) {
            super.forEach(doubleConsumer);
        } else {
            C.U(P()).forEachRemaining(doubleConsumer);
        }
    }

    @Override // j$.util.stream.C, j$.util.stream.F
    public final void forEachOrdered(DoubleConsumer doubleConsumer) {
        if (isParallel()) {
            super.forEachOrdered(doubleConsumer);
        } else {
            C.U(P()).forEachRemaining(doubleConsumer);
        }
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* bridge */ /* synthetic */ F parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* bridge */ /* synthetic */ F sequential() {
        sequential();
        return this;
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h
    public final /* bridge */ /* synthetic */ Spliterator spliterator() {
        return spliterator();
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final InterfaceC0551h unordered() {
        return !H() ? this : new C0620v(this, EnumC0540e3.f4493r, 1);
    }
}
