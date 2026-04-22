package j$.util.stream;

import j$.util.Objects;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/* renamed from: j$.util.stream.d2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0534d2 extends AbstractC0579m2 {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ int f4470b = 1;

    /* renamed from: c  reason: collision with root package name */
    boolean f4471c;

    /* renamed from: d  reason: collision with root package name */
    Object f4472d;

    /* renamed from: e  reason: collision with root package name */
    final /* synthetic */ AbstractC0521b f4473e;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0534d2(X x4, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4473e = x4;
        InterfaceC0599q2 interfaceC0599q22 = this.f4545a;
        Objects.requireNonNull(interfaceC0599q22);
        this.f4472d = new V(interfaceC0599q22);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0534d2(C0557i0 c0557i0, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4473e = c0557i0;
        InterfaceC0599q2 interfaceC0599q22 = this.f4545a;
        Objects.requireNonNull(interfaceC0599q22);
        this.f4472d = new C0532d0(interfaceC0599q22);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0534d2(C0636z c0636z, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4473e = c0636z;
        InterfaceC0599q2 interfaceC0599q22 = this.f4545a;
        Objects.requireNonNull(interfaceC0599q22);
        this.f4472d = new C0596q(interfaceC0599q22);
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        switch (this.f4470b) {
            case 0:
                InterfaceC0587o0 interfaceC0587o0 = (InterfaceC0587o0) ((Function) ((C0557i0) this.f4473e).f4527n).apply(obj);
                if (interfaceC0587o0 != null) {
                    try {
                        boolean z4 = this.f4471c;
                        C0532d0 c0532d0 = (C0532d0) this.f4472d;
                        if (z4) {
                            j$.util.M spliterator = interfaceC0587o0.sequential().spliterator();
                            while (!this.f4545a.n() && spliterator.tryAdvance((LongConsumer) c0532d0)) {
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
                    return;
                }
                return;
            case 1:
                IntStream intStream = (IntStream) ((Function) ((X) this.f4473e).f4432n).apply(obj);
                if (intStream != null) {
                    try {
                        boolean z5 = this.f4471c;
                        V v4 = (V) this.f4472d;
                        if (z5) {
                            j$.util.J spliterator2 = intStream.sequential().spliterator();
                            while (!this.f4545a.n() && spliterator2.tryAdvance((IntConsumer) v4)) {
                            }
                        } else {
                            intStream.sequential().forEach(v4);
                        }
                    } catch (Throwable th3) {
                        try {
                            intStream.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                        throw th3;
                    }
                }
                if (intStream != null) {
                    intStream.close();
                    return;
                }
                return;
            default:
                F f = (F) ((Function) ((C0636z) this.f4473e).f4630n).apply(obj);
                if (f != null) {
                    try {
                        boolean z6 = this.f4471c;
                        C0596q c0596q = (C0596q) this.f4472d;
                        if (z6) {
                            j$.util.G spliterator3 = f.sequential().spliterator();
                            while (!this.f4545a.n() && spliterator3.tryAdvance((DoubleConsumer) c0596q)) {
                            }
                        } else {
                            f.sequential().forEach(c0596q);
                        }
                    } catch (Throwable th5) {
                        try {
                            f.close();
                        } catch (Throwable th6) {
                            th5.addSuppressed(th6);
                        }
                        throw th5;
                    }
                }
                if (f != null) {
                    f.close();
                    return;
                }
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        switch (this.f4470b) {
            case 0:
                this.f4545a.l(-1L);
                return;
            case 1:
                this.f4545a.l(-1L);
                return;
            default:
                this.f4545a.l(-1L);
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public final boolean n() {
        switch (this.f4470b) {
            case 0:
                this.f4471c = true;
                return this.f4545a.n();
            case 1:
                this.f4471c = true;
                return this.f4545a.n();
            default:
                this.f4471c = true;
                return this.f4545a.n();
        }
    }
}
