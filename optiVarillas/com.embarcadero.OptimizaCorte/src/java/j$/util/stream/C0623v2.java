package j$.util.stream;

/* renamed from: j$.util.stream.v2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0623v2 extends AbstractC0574l2 {

    /* renamed from: b  reason: collision with root package name */
    long f4601b;

    /* renamed from: c  reason: collision with root package name */
    long f4602c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0627w2 f4603d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0623v2(C0627w2 c0627w2, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4603d = c0627w2;
        this.f4601b = c0627w2.f4609m;
        long j4 = c0627w2.f4610n;
        this.f4602c = j4 < 0 ? Long.MAX_VALUE : j4;
    }

    @Override // j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        long j5 = this.f4601b;
        if (j5 != 0) {
            this.f4601b = j5 - 1;
            return;
        }
        long j6 = this.f4602c;
        if (j6 > 0) {
            this.f4602c = j6 - 1;
            this.f4537a.accept(j4);
        }
    }

    @Override // j$.util.stream.AbstractC0574l2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4537a.l(AbstractC0637z0.A(j4, this.f4603d.f4609m, this.f4602c));
    }

    @Override // j$.util.stream.AbstractC0574l2, j$.util.stream.InterfaceC0599q2
    public final boolean n() {
        return this.f4602c == 0 || this.f4537a.n();
    }
}
