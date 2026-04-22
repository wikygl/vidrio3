package j$.util.stream;

import java.util.function.Consumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.g2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0549g2 extends AbstractC0559i2 {
    @Override // j$.util.stream.AbstractC0521b
    final boolean M() {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractC0559i2, j$.util.stream.Stream
    public final void forEach(Consumer consumer) {
        if (isParallel()) {
            super.forEach(consumer);
        } else {
            P().forEachRemaining(consumer);
        }
    }

    @Override // j$.util.stream.AbstractC0559i2, j$.util.stream.Stream
    public final void forEachOrdered(Consumer consumer) {
        if (isParallel()) {
            super.forEachOrdered(consumer);
        } else {
            P().forEachRemaining(consumer);
        }
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final InterfaceC0551h unordered() {
        return !H() ? this : new AbstractC0554h2(this, EnumC0540e3.f4493r, 1);
    }
}
