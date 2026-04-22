package j$.util.stream;

/* renamed from: j$.util.stream.x2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0631x2 extends AbstractC0564j2 {

    /* renamed from: b  reason: collision with root package name */
    long f4620b;

    /* renamed from: c  reason: collision with root package name */
    long f4621c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ C0635y2 f4622d;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0631x2(C0635y2 c0635y2, InterfaceC0599q2 interfaceC0599q2) {
        super(interfaceC0599q2);
        this.f4622d = c0635y2;
        this.f4620b = c0635y2.f4627m;
        long j4 = c0635y2.f4628n;
        this.f4621c = j4 < 0 ? Long.MAX_VALUE : j4;
    }

    @Override // j$.util.stream.InterfaceC0584n2, j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        long j4 = this.f4620b;
        if (j4 != 0) {
            this.f4620b = j4 - 1;
            return;
        }
        long j5 = this.f4621c;
        if (j5 > 0) {
            this.f4621c = j5 - 1;
            this.f4530a.accept(d4);
        }
    }

    @Override // j$.util.stream.AbstractC0564j2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4530a.l(AbstractC0637z0.A(j4, this.f4622d.f4627m, this.f4621c));
    }

    @Override // j$.util.stream.AbstractC0564j2, j$.util.stream.InterfaceC0599q2
    public final boolean n() {
        return this.f4621c == 0 || this.f4530a.n();
    }
}
