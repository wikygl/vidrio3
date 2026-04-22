package j$.util.stream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class N0 implements L0 {

    /* renamed from: a  reason: collision with root package name */
    protected final L0 f4335a;

    /* renamed from: b  reason: collision with root package name */
    protected final L0 f4336b;

    /* renamed from: c  reason: collision with root package name */
    private final long f4337c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public N0(L0 l0, L0 l02) {
        this.f4335a = l0;
        this.f4336b = l02;
        this.f4337c = l0.count() + l02.count();
    }

    @Override // j$.util.stream.L0
    public /* bridge */ /* synthetic */ K0 b(int i4) {
        return (K0) b(i4);
    }

    @Override // j$.util.stream.L0
    public final L0 b(int i4) {
        if (i4 == 0) {
            return this.f4335a;
        }
        if (i4 == 1) {
            return this.f4336b;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.L0
    public final long count() {
        return this.f4337c;
    }

    @Override // j$.util.stream.L0
    public final int q() {
        return 2;
    }
}
