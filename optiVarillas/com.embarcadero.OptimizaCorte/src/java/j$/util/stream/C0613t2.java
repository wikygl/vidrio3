package j$.util.stream;

/* renamed from: j$.util.stream.t2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0613t2 extends AbstractC0569k2 {

    /* renamed from: b  reason: collision with root package name */
    long f4589b;

    /* renamed from: c  reason: collision with root package name */
    long f4590c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0618u2 f4591d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0613t2(C0618u2 c0618u2, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4591d = c0618u2;
        this.f4589b = c0618u2.f4595m;
        long j4 = c0618u2.f4596n;
        this.f4590c = j4 < 0 ? Long.MAX_VALUE : j4;
    }

    @Override // j$.util.stream.InterfaceC0589o2, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        long j4 = this.f4589b;
        if (j4 != 0) {
            this.f4589b = j4 - 1;
            return;
        }
        long j5 = this.f4590c;
        if (j5 > 0) {
            this.f4590c = j5 - 1;
            this.f4534a.accept(i4);
        }
    }

    @Override // j$.util.stream.AbstractC0569k2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4534a.l(AbstractC0637z0.A(j4, this.f4591d.f4595m, this.f4590c));
    }

    @Override // j$.util.stream.AbstractC0569k2, j$.util.stream.InterfaceC0599q2
    public final boolean n() {
        return this.f4590c == 0 || this.f4534a.n();
    }
}
