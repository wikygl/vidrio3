package j$.util.stream;

/* renamed from: j$.util.stream.r2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0603r2 extends AbstractC0579m2 {

    /* renamed from: b  reason: collision with root package name */
    long f4577b;

    /* renamed from: c  reason: collision with root package name */
    long f4578c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0608s2 f4579d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0603r2(C0608s2 c0608s2, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4579d = c0608s2;
        this.f4577b = c0608s2.f4582m;
        long j4 = c0608s2.f4583n;
        this.f4578c = j4 < 0 ? Long.MAX_VALUE : j4;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        long j4 = this.f4577b;
        if (j4 != 0) {
            this.f4577b = j4 - 1;
            return;
        }
        long j5 = this.f4578c;
        if (j5 > 0) {
            this.f4578c = j5 - 1;
            this.f4545a.accept((InterfaceC0599q2) obj);
        }
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4545a.l(AbstractC0637z0.A(j4, this.f4579d.f4582m, this.f4578c));
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public final boolean n() {
        return this.f4578c == 0 || this.f4545a.n();
    }
}
