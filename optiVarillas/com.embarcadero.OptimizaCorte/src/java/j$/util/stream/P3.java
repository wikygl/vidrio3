package j$.util.stream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class P3 extends AbstractC0579m2 {

    /* renamed from: b  reason: collision with root package name */
    long f4360b;

    /* renamed from: c  reason: collision with root package name */
    boolean f4361c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ boolean f4362d;

    /* renamed from: e  reason: collision with root package name */
    final /* synthetic */ Q3 f4363e;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public P3(Q3 q32, InterfaceC0599q2 interfaceC0599q2, boolean z4) {
        super(interfaceC0599q2);
        this.f4363e = q32;
        this.f4362d = z4;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        boolean z4 = true;
        if (!this.f4361c) {
            boolean z5 = !this.f4363e.f4370m.test(obj);
            this.f4361c = z5;
            if (!z5) {
                z4 = false;
            }
        }
        boolean z6 = this.f4362d;
        if (z6 && !z4) {
            this.f4360b++;
        }
        if (z6 || z4) {
            this.f4545a.accept((InterfaceC0599q2) obj);
        }
    }
}
