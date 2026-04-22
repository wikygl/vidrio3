package j$.util.stream;

import j$.util.Objects;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;

/* renamed from: j$.util.stream.y  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0632y extends AbstractC0564j2 {

    /* renamed from: b  reason: collision with root package name */
    boolean f4623b;

    /* renamed from: c  reason: collision with root package name */
    C0596q f4624c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0636z f4625d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0632y(C0636z c0636z, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4625d = c0636z;
        InterfaceC0599q2 interfaceC0599q22 = this.f4530a;
        Objects.requireNonNull(interfaceC0599q22);
        this.f4624c = new C0596q(interfaceC0599q22);
    }

    @Override // j$.util.stream.InterfaceC0584n2, j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        F f = (F) ((DoubleFunction) this.f4625d.f4630n).apply(d4);
        if (f != null) {
            try {
                boolean z4 = this.f4623b;
                C0596q c0596q = this.f4624c;
                if (z4) {
                    j$.util.G spliterator = f.sequential().spliterator();
                    while (!this.f4530a.n() && spliterator.tryAdvance((DoubleConsumer) c0596q)) {
                    }
                } else {
                    f.sequential().forEach(c0596q);
                }
            } catch (Throwable th) {
                try {
                    f.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (f != null) {
            f.close();
        }
    }

    @Override // j$.util.stream.AbstractC0564j2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4530a.l(-1L);
    }

    @Override // j$.util.stream.AbstractC0564j2, j$.util.stream.InterfaceC0599q2
    public final boolean n() {
        this.f4623b = true;
        return this.f4530a.n();
    }
}
