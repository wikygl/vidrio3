package j$.util.stream;

import j$.util.Objects;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;

/* renamed from: j$.util.stream.h0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0552h0 extends AbstractC0574l2 {

    /* renamed from: b  reason: collision with root package name */
    boolean f4518b;

    /* renamed from: c  reason: collision with root package name */
    C0532d0 f4519c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0557i0 f4520d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0552h0(C0557i0 c0557i0, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4520d = c0557i0;
        InterfaceC0599q2 interfaceC0599q22 = this.f4537a;
        Objects.requireNonNull(interfaceC0599q22);
        this.f4519c = new C0532d0(interfaceC0599q22);
    }

    @Override // j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        InterfaceC0587o0 interfaceC0587o0 = (InterfaceC0587o0) ((LongFunction) this.f4520d.f4527n).apply(j4);
        if (interfaceC0587o0 != null) {
            try {
                boolean z4 = this.f4518b;
                C0532d0 c0532d0 = this.f4519c;
                if (z4) {
                    j$.util.M spliterator = interfaceC0587o0.sequential().spliterator();
                    while (!this.f4537a.n() && spliterator.tryAdvance((LongConsumer) c0532d0)) {
                    }
                } else {
                    interfaceC0587o0.sequential().forEach(c0532d0);
                }
            } catch (Throwable th) {
                try {
                    interfaceC0587o0.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        if (interfaceC0587o0 != null) {
            interfaceC0587o0.close();
        }
    }

    @Override // j$.util.stream.AbstractC0574l2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4537a.l(-1L);
    }

    @Override // j$.util.stream.AbstractC0574l2, j$.util.stream.InterfaceC0599q2
    public final boolean n() {
        this.f4518b = true;
        return this.f4537a.n();
    }
}
