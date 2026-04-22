package j$.util.stream;

import j$.util.Spliterator;

/* renamed from: j$.util.stream.n  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0581n extends AbstractC0579m2 {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ int f4547b = 2;

    /* renamed from: c  reason: collision with root package name */
    boolean f4548c;

    /* renamed from: d  reason: collision with root package name */
    Object f4549d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0581n(O3 o32, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4549d = o32;
        this.f4548c = true;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0581n(C0544f2 c0544f2, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4549d = c0544f2;
    }

    public /* synthetic */ C0581n(InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        switch (this.f4547b) {
            case 0:
                InterfaceC0599q2 interfaceC0599q2 = this.f4545a;
                if (obj != null) {
                    Object obj2 = this.f4549d;
                    if (obj2 != null && obj.equals(obj2)) {
                        return;
                    }
                } else if (this.f4548c) {
                    return;
                } else {
                    this.f4548c = true;
                    obj = null;
                }
                this.f4549d = obj;
                interfaceC0599q2.accept((InterfaceC0599q2) obj);
                return;
            case 1:
                Stream stream = (Stream) ((C0544f2) this.f4549d).f4507n.apply(obj);
                if (stream != null) {
                    try {
                        boolean z4 = this.f4548c;
                        InterfaceC0599q2 interfaceC0599q22 = this.f4545a;
                        if (z4) {
                            Spliterator spliterator = ((Stream) stream.sequential()).spliterator();
                            while (!interfaceC0599q22.n() && spliterator.tryAdvance(interfaceC0599q22)) {
                            }
                        } else {
                            ((Stream) stream.sequential()).forEach(interfaceC0599q22);
                        }
                    } catch (Throwable th) {
                        try {
                            stream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                }
                if (stream != null) {
                    stream.close();
                    return;
                }
                return;
            default:
                if (this.f4548c) {
                    boolean test = ((O3) this.f4549d).f4352m.test(obj);
                    this.f4548c = test;
                    if (test) {
                        this.f4545a.accept((InterfaceC0599q2) obj);
                        return;
                    }
                    return;
                }
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public void k() {
        switch (this.f4547b) {
            case 0:
                this.f4548c = false;
                this.f4549d = null;
                this.f4545a.k();
                return;
            default:
                super.k();
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        switch (this.f4547b) {
            case 0:
                this.f4548c = false;
                this.f4549d = null;
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
    public boolean n() {
        switch (this.f4547b) {
            case 1:
                this.f4548c = true;
                return this.f4545a.n();
            case 2:
                return !this.f4548c || this.f4545a.n();
            default:
                return super.n();
        }
    }
}
