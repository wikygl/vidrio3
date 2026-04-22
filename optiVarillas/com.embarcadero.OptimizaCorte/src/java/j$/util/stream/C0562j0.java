package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.LongConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.j0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0562j0 extends AbstractC0572l0 {
    @Override // j$.util.stream.AbstractC0521b
    final boolean M() {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractC0572l0, j$.util.stream.InterfaceC0587o0
    public final void forEach(LongConsumer longConsumer) {
        if (isParallel()) {
            super.forEach(longConsumer);
        } else {
            AbstractC0572l0.U(P()).forEachRemaining(longConsumer);
        }
    }

    @Override // j$.util.stream.AbstractC0572l0, j$.util.stream.InterfaceC0587o0
    public final void forEachOrdered(LongConsumer longConsumer) {
        if (isParallel()) {
            super.forEachOrdered(longConsumer);
        } else {
            AbstractC0572l0.U(P()).forEachRemaining(longConsumer);
        }
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* bridge */ /* synthetic */ InterfaceC0587o0 parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* bridge */ /* synthetic */ InterfaceC0587o0 sequential() {
        sequential();
        return this;
    }

    @Override // j$.util.stream.AbstractC0521b, j$.util.stream.InterfaceC0551h
    public final /* bridge */ /* synthetic */ Spliterator spliterator() {
        return spliterator();
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final InterfaceC0551h unordered() {
        return !H() ? this : new C0628x(this, EnumC0540e3.f4493r, 4);
    }
}
