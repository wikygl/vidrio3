package j$.util.stream;

import j$.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class Z extends AbstractC0569k2 {

    /* renamed from: b  reason: collision with root package name */
    boolean f4441b;

    /* renamed from: c  reason: collision with root package name */
    V f4442c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ X f4443d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z(X x4, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4443d = x4;
        InterfaceC0599q2 interfaceC0599q22 = this.f4534a;
        Objects.requireNonNull(interfaceC0599q22);
        this.f4442c = new V(interfaceC0599q22);
    }

    @Override // j$.util.stream.InterfaceC0589o2, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        IntStream intStream = (IntStream) ((IntFunction) this.f4443d.f4432n).apply(i4);
        if (intStream != null) {
            try {
                boolean z4 = this.f4441b;
                V v4 = this.f4442c;
                if (z4) {
                    j$.util.J spliterator = intStream.sequential().spliterator();
                    while (!this.f4534a.n() && spliterator.tryAdvance((IntConsumer) v4)) {
                    }
                } else {
                    intStream.sequential().forEach(v4);
                }
            } catch (Throwable th) {
                try {
                    intStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (intStream != null) {
            intStream.close();
        }
    }

    @Override // j$.util.stream.AbstractC0569k2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4534a.l(-1L);
    }

    @Override // j$.util.stream.AbstractC0569k2, j$.util.stream.InterfaceC0599q2
    public final boolean n() {
        this.f4441b = true;
        return this.f4534a.n();
    }
}
