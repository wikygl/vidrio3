package j$.util.stream;

import java.util.Comparator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class E2 extends AbstractC0579m2 {

    /* renamed from: b  reason: collision with root package name */
    protected final Comparator f4272b;

    /* renamed from: c  reason: collision with root package name */
    protected boolean f4273c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public E2(InterfaceC0599q2 interfaceC0599q2, Comparator comparator) {
        super(interfaceC0599q2);
        this.f4272b = comparator;
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public final boolean n() {
        this.f4273c = true;
        return false;
    }
}
